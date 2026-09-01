
package com.wk.ti.document.service;

import com.wk.ti.document.config.QuestionGenerationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogicalSectionSplitter {

    private final QuestionGenerationProperties properties;

    public List<SectionDraft> split(
            Long documentId,
            List<Document> pages) {

        if (pages == null || pages.isEmpty()) {
            return List.of();
        }

        List<SectionDraft> sections = new ArrayList<>();
        StringBuilder content = new StringBuilder();

        Integer startPageNumber = null;
        Integer endPageNumber = null;
        int sectionNumber = 1;

        /*
         * PagePdfDocumentReader with:
         *
         *     withPagesPerDocument(1)
         *
         * returns one Document per PDF page.
         *
         * Therefore the list position represents the physical
         * page number.
         */
        for (int index = 0; index < pages.size(); index++) {

            Document page = pages.get(index);
            String pageText = normalize(page.getText());

            if (!StringUtils.hasText(pageText)) {
                continue;
            }

            /*
             * The Document metadata "page_number" in our current
             * Spring AI 1.1.5 configuration represents the TOTAL
             * NUMBER OF PAGES, not the current page number.
             *
             * Therefore DO NOT use:
             *
             *     page.getMetadata().get("page_number")
             *
             * as the physical page number.
             */
            int physicalPageNumber = index + 1;

            /*
             * First page contributing content to this section.
             */
            if (content.isEmpty()) {
                startPageNumber = physicalPageNumber;
            }

            /*
             * Last page contributing content to this section.
             */
            endPageNumber = physicalPageNumber;

            /*
             * Append page content.
             */
            if (!content.isEmpty()) {
                content.append("\n\n");
            }

            content.append(pageText);

            /*
             * Close the section when the configured target size
             * has been reached.
             */
            if (shouldFlush(content)) {
                sections.add(
                        createSection(
                                documentId,
                                sectionNumber++,
                                content,
                                startPageNumber,
                                endPageNumber
                        ));

                content.setLength(0);
                startPageNumber = null;
                endPageNumber = null;
            }
        }

        /*
         * Flush remaining content.
         */
        if (!content.isEmpty()) {
            sections.add(
                    createSection(
                            documentId,
                            sectionNumber,
                            content,
                            startPageNumber,
                            endPageNumber
                    )
            );
        }

        return mergeSmallLastSection(sections);
    }

    private boolean shouldFlush(
            StringBuilder content) {
        return content.length() >= properties.getSection().getTargetChars();
    }

    private SectionDraft createSection(
            Long documentId,
            int sectionNumber,
            StringBuilder content,
            Integer startPageNumber,
            Integer endPageNumber) {

        return new SectionDraft(
                documentId,
                sectionNumber,
                null,
                content.toString().trim(),
                startPageNumber,
                endPageNumber
        );
    }

    private List<SectionDraft> mergeSmallLastSection(
            List<SectionDraft> sections) {
        if (sections.size() < 2) {
            return sections;
        }
        SectionDraft last = sections.getLast();
        if (last.content().length() >= properties.getSection().getMinChars()) {
            return sections;
        }

        SectionDraft previous = sections.get(sections.size() - 2);
        SectionDraft merged =
                new SectionDraft(
                        previous.documentId(),
                        previous.sectionNumber(),
                        previous.title(),
                        previous.content()
                                + "\n\n"
                                + last.content(),
                        previous.startPageNumber(),
                        last.endPageNumber()
                );

        List<SectionDraft> result =
                new ArrayList<>(sections.subList(0, sections.size() - 2));

        result.add(merged);

        return result;
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    public record SectionDraft(
            Long documentId,
            int sectionNumber,
            String title,
            String content,
            Integer startPageNumber,
            Integer endPageNumber
    ) {
    }
}
