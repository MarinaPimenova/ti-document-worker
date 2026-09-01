package com.wk.ti.etl.transform.pdf.document.service;

import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationDocumentEntity;
import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationSectionEntity;
import com.wk.ti.etl.load.service.VectorStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Long ingest(
            String filename,
            long fileSize,
            DocumentReader reader) {
        QuestionGenerationDocumentEntity document = documentService.create(filename, fileSize);
        Long documentId = document.getId();

        try {
            List<Document> pages = reader.read();
            log.info("Document {}: extracted {} pages from {}",
                    documentId, pages.size(), filename);

            List<LogicalSectionSplitter.SectionDraft> drafts =
                    logicalSectionSplitter.split(documentId, pages, filename);

            List<QuestionGenerationSectionEntity> sections = sectionService.saveAll(drafts);

            documentMetadataEnricher.enrich(pages, sections);
            vectorStoreService.store(documentId, filename, pages);
            documentService.markReady(documentId);

            return documentId;
        } catch (Exception exception) {
            log.error(
                    "Document ingestion failed. documentId={}, filename={}",
                    documentId, filename, exception);

            documentService.markFailed(documentId);
            throw exception;
        }
    }



}

