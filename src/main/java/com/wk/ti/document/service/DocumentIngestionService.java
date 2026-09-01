package com.wk.ti.document.service;

import com.wk.ti.document.persistence.QuestionGenerationDocumentEntity;
import com.wk.ti.embedding.store.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIngestionService {

    private final QuestionGenerationDocumentService documentService;
    private final QuestionGenerationSectionService sectionService;
    private final LogicalSectionSplitter logicalSectionSplitter;
    private final VectorStoreService vectorStoreService;
    private final DocumentMetadataEnricher documentMetadataEnricher;
    private final DocumentParsingService documentParsingService;

    @Transactional
    public Long ingest(
            String filename,
            long fileSize,
            File file
            /*DocumentReader reader*/) {
        QuestionGenerationDocumentEntity document =
                documentService.create(filename, fileSize);
        Long documentId = document.getId();

        try {
            Resource resourceFromPath = new FileSystemResource(file.toPath());
            documentParsingService.parseAndSplitDocument(documentId, resourceFromPath);
//            //List<Document> pages = reader.read();
//            List<Document> pages = read(file);
//            log.info("Document {}: extracted {} pages from {}",
//                    documentId,
//                    pages.size(),
//                    filename);
//
//            List<LogicalSectionSplitter.SectionDraft> drafts =
//                    logicalSectionSplitter.split(
//                            documentId,
//                            pages);
//
//            List<QuestionGenerationSectionEntity> sections = sectionService.saveAll(drafts);
//            documentMetadataEnricher.enrich(
//                    pages,
//                    sections,
//                    filename);
//
//            vectorStoreService.store(
//                    documentId,
//                    filename,
//                    pages
//            );

            documentService.markReady(documentId);

            return documentId;
        } catch (Exception exception) {
            log.error(
                    "Document ingestion failed. documentId={}, filename={}",
                    documentId,
                    filename,
                    exception);

            documentService.markFailed(documentId);
            throw exception;
        }
    }


    public List<Document> read(File file) {
        FileSystemResource resource = new FileSystemResource(file);

        PdfDocumentReaderConfig config =
                PdfDocumentReaderConfig.builder()
                        .withPagesPerDocument(1)
                        .build();
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource, config);

        List<Document> documents = reader.read();
        log.info("PDF {} produced {} Spring AI Documents",
                file.getName(),
                documents.size());

        for (int i = 0; i < documents.size(); i++) {
            Document document = documents.get(i);
            log.info("Document [{}]: textLength={}, metadata={}",
                    i,
                    document.getText() == null
                            ? 0
                            : document.getText().length(),
                    document.getMetadata());
        }
        return documents;
    }

}

