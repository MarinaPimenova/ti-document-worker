package com.wk.ti.event;

import java.io.Serializable;

public record FileProcessingEvent(
        String jobId,
        String type,
        String originalFilename,
        String storedFilePath
        //String traceId
) implements Serializable {}
