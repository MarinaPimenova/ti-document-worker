package com.wk.ti.etl.transform.pdf.document.service;

import com.wk.ti.etl.transform.pdf.document.persistence.QuestionGenerationSectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wk.ti.etl.transform.pdf.document.model.DocumentMetadataKeys.*;
import static com.wk.ti.etl.transform.pdf.document.service.DocumentMetadataUtility.getIntegerMetadata;

@Component
@RequiredArgsConstructor
public class DocumentMetadataEnricher {

    public void enrich(
            List<Document> pages,
            List<QuestionGenerationSectionEntity> sections) {

        if (pages == null || pages.isEmpty()
                || sections == null || sections.isEmpty()) {
            return;
        }

        for (Document page : pages) {
            Integer pageStart = getIntegerMetadata(page, START_PAGE_NUMBER);

            QuestionGenerationSectionEntity section = findSection(sections, pageStart);
            if (section == null) {
                continue;
            }
            page.getMetadata().put(DOCUMENT_ID, section.getDocumentId());

            page.getMetadata().put(SECTION_ID, section.getId());
        }
    }

    private QuestionGenerationSectionEntity findSection(
            List<QuestionGenerationSectionEntity> sections,
            int pageStart) {

        return sections.stream()
                .filter(section ->
                        section.getStartPageNumber() != null
                                && section.getEndPageNumber() != null
                                && pageStart >= section.getStartPageNumber()
                                && pageStart <= section.getEndPageNumber()
                )
                .findFirst()
                .orElse(null);
    }

}
