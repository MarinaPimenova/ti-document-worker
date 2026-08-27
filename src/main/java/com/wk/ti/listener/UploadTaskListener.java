package com.wk.ti.listener;

import com.wk.ti.embedding.service.DataLoaderService;
import com.wk.ti.event.FileProcessingEvent;
import com.wk.ti.event.UploadCompletedEvent;
import com.wk.ti.event.UploadFailedEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadTaskListener {

    private final DataLoaderService dataLoaderService;
    private final RabbitTemplate rabbitTemplate;
    private final MeterRegistry meterRegistry;

    private static final String EXCHANGE = "ti.upload";
    private static final String RK_COMPLETED = "upload.completed";
    private static final String RK_FAILED = "upload.failed";

    @RabbitListener(queues = "upload-worker.import")
    @Observed(name = "upload.worker.process", contextualName = "process-upload-file")
    public void processUpload(FileProcessingEvent event) {
        log.info("Received processing request for jobId={}, path={}", event.jobId(), event.storedFilePath());

        File rawFile = new File(event.storedFilePath());
        if (!rawFile.exists()) {
            String errorMsg = "File not found at path: " + event.storedFilePath();
            handleFailure(event.jobId(), errorMsg, new FileNotFoundException(errorMsg));
            return;
        }
        try {

            dataLoaderService.uploadKnowledge(rawFile, event.originalFilename());
            log.info("Successfully uploaded {} content from file {}", event.originalFilename());

            UploadCompletedEvent successEvent = new UploadCompletedEvent(event.jobId());
            rabbitTemplate.convertAndSend(EXCHANGE, RK_COMPLETED, successEvent);

        } catch (Exception ex) {
            handleFailure(event.jobId(), ex.getMessage(), ex);
        } finally {
            // Clean up temporary local file storage
            if (rawFile.exists() && !rawFile.delete()) {
                log.warn("Failed to delete processed file: {}", rawFile.getAbsolutePath());
            }
        }
    }

    private void handleFailure(String jobId, String failureReason, Exception ex) {
        log.error("Upload processing failed for jobId={}. Reason: {}", jobId, failureReason, ex);
        meterRegistry.counter("upload.worker.failures", "exception", ex.getClass().getSimpleName()).increment();

        UploadFailedEvent failedEvent = new UploadFailedEvent(jobId, failureReason);
        rabbitTemplate.convertAndSend(EXCHANGE, RK_FAILED, failedEvent);
    }
}
