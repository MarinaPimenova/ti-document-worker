package com.wk.ti.upload.model;

import com.wk.ti.document.model.DocumentStatus;

public record FileProcessingResponse(
        Long documentId,
        String filename,
        DocumentStatus status
) {
}
