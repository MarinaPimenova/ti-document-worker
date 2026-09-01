package com.wk.ti.etl.transform.pdf.document.service;

import com.wk.ti.etl.transform.pdf.document.model.DocumentExtension;
import com.wk.ti.etl.transform.pdf.document.model.DocumentStatus;
import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationDocumentEntity;
import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class QuestionGenerationDocumentService {

    private final QuestionGenerationDocumentRepository repository;

    @Transactional
    public QuestionGenerationDocumentEntity create(
            String filename,
            long fileSize) {

        QuestionGenerationDocumentEntity entity = new QuestionGenerationDocumentEntity();

        entity.setFilename(filename);
        entity.setFileExtension(DocumentExtension.fromFileName(filename));
        entity.setFileSize(fileSize);
        entity.setStatus(DocumentStatus.PROCESSING);

        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return repository.save(entity);
    }

    @Transactional
    public void markReady(Long documentId) {

        QuestionGenerationDocumentEntity entity =
                repository.findById(documentId)
                        .orElseThrow();

        entity.setStatus(DocumentStatus.READY);
        entity.setUpdatedAt(OffsetDateTime.now());

        repository.save(entity);
    }

    @Transactional
    public void markFailed(Long documentId) {

        QuestionGenerationDocumentEntity entity =
                repository.findById(documentId)
                        .orElseThrow();

        entity.setStatus(DocumentStatus.FAILED);
        entity.setUpdatedAt(OffsetDateTime.now());

        repository.save(entity);
    }
}
