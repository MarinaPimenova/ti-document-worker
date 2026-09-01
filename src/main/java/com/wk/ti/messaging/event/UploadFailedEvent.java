package com.wk.ti.messaging.event;

import java.io.Serializable;

public record UploadFailedEvent(
        String uploadId,
        String reason
) implements Serializable {}
