package com.wk.ti.document.service;

import com.wk.ti.document.persistence.QuestionGenerationSectionEntity;
import com.wk.ti.document.persistence.QuestionGenerationSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionGenerationSectionService {

    private final QuestionGenerationSectionRepository repository;

    @Transactional
    public List<QuestionGenerationSectionEntity> saveAll(
            List<LogicalSectionSplitter.SectionDraft> drafts) {

        OffsetDateTime now =
                OffsetDateTime.now();

        List<QuestionGenerationSectionEntity> entities =
                drafts.stream()
                        .map(draft -> {

                            QuestionGenerationSectionEntity entity =
                                    new QuestionGenerationSectionEntity();

                            entity.setDocumentId(
                                    draft.documentId()
                            );

                            entity.setSectionNumber(
                                    draft.sectionNumber()
                            );

                            entity.setTitle(
                                    draft.title()
                            );

                            entity.setContent(
                                    draft.content()
                            );

                            entity.setStartPageNumber(
                                    draft.startPageNumber()
                            );

                            entity.setEndPageNumber(
                                    draft.endPageNumber()
                            );

                            entity.setTokenCount(null);

                            entity.setCreatedAt(now);

                            return entity;
                        })
                        .toList();

        return repository.saveAll(entities);
    }
}
