package com.wk.ti.etl.transform.pdf.document.service;

import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationSectionEntity;
import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationSectionRepository;
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

        OffsetDateTime now = OffsetDateTime.now();

        List<QuestionGenerationSectionEntity> entities =
                drafts.stream()
                        .map(draft -> {
                            QuestionGenerationSectionEntity entity =
                                    new QuestionGenerationSectionEntity();
                            entity.setDocumentId(draft.documentId());

                            entity.setSectionNumber(draft.sectionNumber());

                            entity.setTitle(draft.title());

                            entity.setContent(draft.content());

                            entity.setStartPageNumber(draft.startPageNumber());

                            entity.setEndPageNumber(draft.endPageNumber());

                            entity.setTokenCount(estimateTokenCount(draft.content()));

                            entity.setCreatedAt(now);

                            return entity;
                        })
                        .toList();

        return repository.saveAll(entities);
    }

    /**
     * Roughly estimates token count (1 token ≈ 4 characters in English text).
     */
    private int estimateTokenCount(String text) {
        return text != null ? text.length() / 4 : 0;
    }
}
