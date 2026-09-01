package com.wk.ti.messaging.event;

import java.io.Serializable;

public record UploadCompletedEvent(
        String uploadId
) implements Serializable {}
