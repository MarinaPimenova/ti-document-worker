package com.wk.ti.etl.transform.pdf.document.service;

import com.wk.ti.etl.transform.pdf.document.config.QuestionGenerationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wk.ti.etl.transform.pdf.document.model.DocumentMetadataKeys.END_PAGE_NUMBER;
import static com.wk.ti.etl.transform.pdf.document.model.DocumentMetadataKeys.START_PAGE_NUMBER;
import static com.wk.ti.etl.transform.pdf.document.service.DocumentMetadataUtility.getIntegerMetadata;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogicalSectionSplitter {

    private final QuestionGenerationProperties properties;

    public List<SectionDraft> split(
            Long documentId,
            List<Document> pages,
            String originalFilename) {

        if (pages == null || pages.isEmpty()) {
            return List.of();
        }

        List<SectionDraft> sections = new ArrayList<>();

        StringBuilder content = new StringBuilder();
        Integer startPage = null;
        Integer endPage = null;

        int sectionNumber = 1;

        for (Document page : pages) {
            log.info("PDF Document metadata: {}", page.getMetadata());
            String pageText = normalize(page.getText());
            if (!StringUtils.hasText(pageText)) {
                continue;
            }

            Integer pageStart = getIntegerMetadata(page, START_PAGE_NUMBER);
            Integer pageEnd = getIntegerMetadata(page, END_PAGE_NUMBER);
            if (startPage == null) {
                startPage = pageStart;
            }
            if (endPage == null || endPage < pageStart) {
                endPage = pageStart;
            } else if (pageEnd != null) {
                endPage = pageEnd;
            }

            if (!content.isEmpty()) {
                content.append("\n\n");
            }
            content.append(pageText);

            if (shouldFlush(content)) {
                sections.add(createSection(
                                documentId,
                                sectionNumber++,
                                content,
                                startPage,
                                endPage,
                        originalFilename));

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
                            endPage,
                            originalFilename));
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
            Integer endPage,
            String filename) {

        return new SectionDraft(
                documentId,
                sectionNumber,
                format("%s_section_%d", filename, sectionNumber),
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
        SectionDraft last = sections.getLast();
        if (last.content().length()
                >= properties.getSection().getMinChars()) {
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

        List<SectionDraft> result = new ArrayList<>(sections.subList(0, sections.size() - 2));
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