package com.wk.ti.document.model;

import java.time.OffsetDateTime;

public record QuestionGenerationDocument(
        Long id,
        String filename,
        String contentType,
        Long fileSize,
        DocumentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
