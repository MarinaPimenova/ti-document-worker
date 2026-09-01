package com.wk.ti.document.service;

import com.wk.ti.document.config.SectionProperties;
import com.wk.ti.document.persistence.QuestionGenerationSectionEntity;
import com.wk.ti.document.persistence.QuestionGenerationSectionRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

@Service
public class DocumentParsingService {

    private final QuestionGenerationSectionRepository sectionRepository;
    private final SectionProperties sectionProperties;

    public DocumentParsingService(QuestionGenerationSectionRepository sectionRepository,
                                  SectionProperties sectionProperties) {
        this.sectionRepository = sectionRepository;
        this.sectionProperties = sectionProperties;
    }

    @Transactional
    public List<QuestionGenerationSectionEntity> parseAndSplitDocument(
            Long documentId,
            Resource fileResource) {
        // 1. Read document using Spring AI Tika Reader
        TikaDocumentReader tikaReader = new TikaDocumentReader(fileResource);
        List<Document> documents = tikaReader.read();

        if (documents.isEmpty()) {
            throw new IllegalArgumentException("Document content is empty");
        }

        // Combine extracted text segments into unified text string
        StringBuilder fullTextBuilder = new StringBuilder();
        for (Document doc : documents) {
            if (doc.getText() != null) {
                fullTextBuilder.append(doc.getText()).append("\n");
            }
        }
        String fullText = fullTextBuilder.toString().trim();

        // 2. Split into structural sections
        List<QuestionGenerationSectionEntity> sections = buildSections(documentId, fullText);

        // 3. Clean existing sections if retrying and save new sections
        sectionRepository.deleteByDocumentId(documentId);
        return sectionRepository.saveAll(sections);
    }

    private List<QuestionGenerationSectionEntity> buildSections(Long documentId, String text) {
        List<QuestionGenerationSectionEntity> result = new ArrayList<>();
        String[] lines = text.split("\\r?\\n");

        StringBuilder currentChunk = new StringBuilder();
        String currentTitle = "Introduction";
        int sectionNumber = 1;

        for (String line : lines) {
            String trimmedLine = line.trim();
            Matcher headingMatcher = sectionProperties.getCompiledPattern().matcher(trimmedLine);
            boolean isHeading = headingMatcher.matches();

            int currentLength = currentChunk.length();

            // Trigger split if:
            // - Met a heading AND current chunk meets minimum threshold
            // OR
            // - Current chunk exceeds target size
            // OR
            // - Hard stop limit (max-chars) is reached
            if ((isHeading && currentLength >= sectionProperties.getMinChars()) ||
                    currentLength >= sectionProperties.getTargetChars() ||
                    currentLength + line.length() > sectionProperties.getMaxChars()) {

                if (currentLength > 0) {
                    String chunkContent = currentChunk.toString().trim();
                    int estimatedTokens = estimateTokenCount(chunkContent);

                    result.add(QuestionGenerationSectionEntity.create(
                            documentId,
                            sectionNumber++,
                            currentTitle,
                            chunkContent,
                            estimatedTokens
                    ));
                    currentChunk.setLength(0);
                }

                if (isHeading) {
                    currentTitle = trimmedLine;
                }
            }

            currentChunk.append(line).append("\n");
        }

        // Add remaining trailing content
        if (currentChunk.length() > 0) {
            String chunkContent = currentChunk.toString().trim();
            int estimatedTokens = estimateTokenCount(chunkContent);

            result.add(QuestionGenerationSectionEntity.create(
                    documentId,
                    sectionNumber,
                    currentTitle,
                    chunkContent,
                    estimatedTokens
            ));
        }

        return result;
    }

    /**
     * Roughly estimates token count (1 token ≈ 4 characters in English text).
     */
    private int estimateTokenCount(String text) {
        return text != null ? text.length() / 4 : 0;
    }
}