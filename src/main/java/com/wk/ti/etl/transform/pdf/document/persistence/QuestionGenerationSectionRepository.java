package com.wk.ti.etl.transform.pdf.document.persistence;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface QuestionGenerationSectionRepository
        extends ListCrudRepository<QuestionGenerationSectionEntity, Long> {

    List<QuestionGenerationSectionEntity>
    findByDocumentIdOrderBySectionNumber(Long documentId);

}
