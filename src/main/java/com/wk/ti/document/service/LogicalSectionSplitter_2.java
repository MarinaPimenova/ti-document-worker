package com.wk.ti.document.service;

import com.wk.ti.document.config.QuestionGenerationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wk.ti.document.model.DocumentMetadataKeys_2.END_PAGE_NUMBER;
import static com.wk.ti.document.model.DocumentMetadataKeys_2.START_PAGE_NUMBER;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogicalSectionSplitter_2 {

    private final QuestionGenerationProperties properties;

    public List<SectionDraft> split(
            Long documentId,
            List<Document> pages) {

        if (pages == null || pages.isEmpty()) {
            return List.of();
        }

        List<SectionDraft> sections = new ArrayList<>();

        StringBuilder content = new StringBuilder();
        Integer startPage = null;
        Integer endPage = null;

        int sectionNumber = 1;

        for (Document page : pages) {
            log.info("PDF Document metadata: {}",
                    page.getMetadata());
            String pageText = normalize(page.getText());
            if (!StringUtils.hasText(pageText)) {
                continue;
            }

            Integer pageStart = getIntegerMetadata(page, START_PAGE_NUMBER);
            Integer pageEnd = getIntegerMetadata(page, END_PAGE_NUMBER);
            if (startPage == null) {
                startPage = pageStart;
            }
            endPage = pageEnd;

            if (!content.isEmpty()) {
                content.append("\n\n");
            }
            content.append(pageText);

            if (shouldFlush(content)) {
                sections.add(
                        createSection(
                                documentId,
                                sectionNumber++,
                                content,
                                startPage,
                                endPage)
                );

                content.setLength(0);
                startPage = null;
                endPage = null;
            }
        }

        if (!content.isEmpty()) {

            sections.add(
                    createSection(
                            documentId,
                            sectionNumber,
                            content,
                            startPage,
                            endPage
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
            Integer startPage,
            Integer endPage) {

        return new SectionDraft(
                documentId,
                sectionNumber,
                null,
                content.toString().trim(),
                startPage,
                endPage
        );
    }

    private List<SectionDraft> mergeSmallLastSection(
            List<SectionDraft> sections) {

        if (sections.size() < 2) {
            return sections;
        }

        SectionDraft last =
                sections.getLast();

        if (last.content().length()
                >= properties.getSection().getMinChars()) {

            return sections;
        }

        SectionDraft previous =
                sections.get(sections.size() - 2);

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
                new ArrayList<>(
                        sections.subList(
                                0,
                                sections.size() - 2
                        )
                );

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
            }
            catch (NumberFormatException ignored) {
                // Ignore malformed metadata.
            }
        }

        return null;
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