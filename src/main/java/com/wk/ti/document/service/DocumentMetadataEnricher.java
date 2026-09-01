package com.wk.ti.document.service;

import com.wk.ti.document.persistence.QuestionGenerationSectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.wk.ti.document.model.DocumentMetadataKeys.DOCUMENT_ID;
import static com.wk.ti.document.model.DocumentMetadataKeys.FILENAME;
import static com.wk.ti.document.model.DocumentMetadataKeys.SECTION_ID;
import static com.wk.ti.document.model.DocumentMetadataKeys.SECTION_NUMBER;

@Component
@RequiredArgsConstructor
public class DocumentMetadataEnricher {

    public void enrich(
            List<Document> pages,
            List<QuestionGenerationSectionEntity> sections,
            String filename) {

        if (pages == null || pages.isEmpty()
                || sections == null || sections.isEmpty()) {
            return;
        }

        for (int index = 0; index < pages.size(); index++) {

            Document page = pages.get(index);

            int physicalPageNumber = index + 1;

            QuestionGenerationSectionEntity section =
                    findSection(
                            sections,
                            physicalPageNumber
                    );

            if (section == null) {
                continue;
            }

            enrichPageMetadata(
                    page,
                    section,
                    filename
            );
        }
    }

    private void enrichPageMetadata(
            Document page,
            QuestionGenerationSectionEntity section,
            String filename) {

        page.getMetadata().put(
                DOCUMENT_ID,
                section.getDocumentId()
        );

        page.getMetadata().put(
                SECTION_ID,
                section.getId()
        );

        page.getMetadata().put(
                SECTION_NUMBER,
                section.getSectionNumber()
        );

        if (StringUtils.hasText(filename)) {
            page.getMetadata().put(
                    FILENAME,
                    filename
            );
        }
    }

    private QuestionGenerationSectionEntity findSection(
            List<QuestionGenerationSectionEntity> sections,
            int pageNumber) {

        return sections.stream()
                .filter(section ->
                        section.getStartPageNumber() != null
                                && section.getEndPageNumber() != null
                                && pageNumber >= section.getStartPageNumber()
                                && pageNumber <= section.getEndPageNumber()
                )
                .findFirst()
                .orElse(null);
    }

    private Integer getIntegerMetadata(
            Document document,
            String key) {

        Object value =
                document.getMetadata().get(key);

        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String string
                && StringUtils.hasText(string)) {

            try {
                return Integer.valueOf(string);
            } catch (NumberFormatException ignored) {
                // Ignore malformed metadata.
            }
        }

        return null;
    }
}
