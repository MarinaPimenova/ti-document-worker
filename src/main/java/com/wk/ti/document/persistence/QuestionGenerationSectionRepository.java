package com.wk.ti.document.persistence;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface QuestionGenerationSectionRepository
        extends ListCrudRepository<QuestionGenerationSectionEntity, Long> {

    List<QuestionGenerationSectionEntity>
    findByDocumentIdOrderBySectionNumber(Long documentId);

    void deleteByDocumentId(Long documentId);
}
