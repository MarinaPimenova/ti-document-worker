package com.wk.ti.upload.model;

import com.wk.ti.etl.transform.pdf.document.model.DocumentStatus;

public record FileProcessingResponse(
        Long documentId,
        String filename,
        DocumentStatus status
) {
}
