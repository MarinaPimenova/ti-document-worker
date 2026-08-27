package com.wk.ti.event;

import java.io.Serializable;

public record UploadCompletedEvent(
        String uploadId
) implements Serializable {}
