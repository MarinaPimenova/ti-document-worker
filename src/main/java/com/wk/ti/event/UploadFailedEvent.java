package com.wk.ti.event;

import java.io.Serializable;

public record UploadFailedEvent(
        String uploadId,
        String reason
) implements Serializable {}
