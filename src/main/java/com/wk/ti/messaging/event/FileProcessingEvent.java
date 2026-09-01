package com.wk.ti.messaging.event;

import java.io.Serializable;

public record FileProcessingEvent(
        String jobId,
        String type,
        String originalFilename,
        String storedFilePath
        //String traceId
) implements Serializable {}
