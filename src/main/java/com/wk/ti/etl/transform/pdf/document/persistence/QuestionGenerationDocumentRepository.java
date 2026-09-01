package com.wk.ti.etl.transform.pdf.document.persistence;

import com.wk.ti.etl.transform.pdf.document.model.DocumentStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface QuestionGenerationDocumentRepository
        extends ListCrudRepository<QuestionGenerationDocumentEntity, Long> {

    List<QuestionGenerationDocumentEntity> findByStatus(
            DocumentStatus status
    );
}
