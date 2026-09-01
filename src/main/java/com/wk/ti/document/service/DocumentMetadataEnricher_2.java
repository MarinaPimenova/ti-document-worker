package com.wk.ti.document.service;

import com.wk.ti.document.persistence.QuestionGenerationSectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.wk.ti.document.model.DocumentMetadataKeys.*;
import static com.wk.ti.document.model.DocumentMetadataKeys_2.END_PAGE_NUMBER;
import static com.wk.ti.document.model.DocumentMetadataKeys_2.START_PAGE_NUMBER;

@Component
@RequiredArgsConstructor
public class DocumentMetadataEnricher_2 {

    public void enrich(
            List<Document> pages,
            List<QuestionGenerationSectionEntity> sections,
            String filename) {

        if (pages == null || pages.isEmpty()
                || sections == null || sections.isEmpty()) {
            return;
        }

        for (Document page : pages) {

            Integer pageStart =
                    getIntegerMetadata(
                            page,
                            START_PAGE_NUMBER
                    );

            Integer pageEnd =
                    getIntegerMetadata(
                            page,
                            END_PAGE_NUMBER
                    );

            if (pageStart == null || pageEnd == null) {
                continue;
            }

            QuestionGenerationSectionEntity section =
                    findSection(
                            sections,
                            pageStart,
                            pageEnd
                    );

            if (section == null) {
                continue;
            }

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

            page.getMetadata().put(
                    FILENAME,
                    filename
            );
        }
    }

    private QuestionGenerationSectionEntity findSection(
            List<QuestionGenerationSectionEntity> sections,
            int pageStart,
            int pageEnd) {

        return sections.stream()
                .filter(section ->
                        section.getStartPageNumber() != null
                                && section.getEndPageNumber() != null
                                && pageStart >= section.getStartPageNumber()
                                && pageEnd <= section.getEndPageNumber()
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

        if (value instanceof String string) {

            try {
                return Integer.valueOf(string);
            }
            catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }
}
