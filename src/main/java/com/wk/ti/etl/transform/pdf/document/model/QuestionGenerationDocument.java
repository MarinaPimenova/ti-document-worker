package com.wk.ti.etl.transform.pdf.document.model;

import java.time.OffsetDateTime;

public record QuestionGenerationDocument(
        Long id,
        String filename,
        String fileExtension,
        Long fileSize,
        DocumentStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
