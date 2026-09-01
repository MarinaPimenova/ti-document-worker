package com.wk.ti.document.persistence;

import com.wk.ti.document.model.DocumentStatus;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface QuestionGenerationDocumentRepository
        extends ListCrudRepository<QuestionGenerationDocumentEntity, Long> {

    List<QuestionGenerationDocumentEntity> findByStatus(
            DocumentStatus status
    );
}
