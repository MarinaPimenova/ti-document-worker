package com.wk.ti.document.model;

import java.time.OffsetDateTime;

public record DocumentSection(
        Long id,
        Long documentId,
        int sectionNumber,
        String title,
        String content,
        Integer startPageNumber,
        Integer endPageNumber,
        Integer tokenCount,
        OffsetDateTime createdAt
) {
}
