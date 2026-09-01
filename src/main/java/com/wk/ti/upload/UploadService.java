package com.wk.ti.upload;

import com.wk.ti.document.model.DocumentStatus;
import com.wk.ti.upload.model.FileProcessingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SuppressWarnings("SpringQualifierCopyableLombok")
@Service
@Slf4j
@RequiredArgsConstructor
public class UploadService {
    private final DataLoaderService dataLoaderService;
    private final FileStorageService fileStorageService;
    @Qualifier("documentProcessingExecutor")
    private final Executor executor;

    public FileProcessingResponse upload(MultipartFile file) {
        // 1. Capture file metadata and content synchronously on the main thread
        String originalFilename = file.getOriginalFilename();

        File uploadedFile;
        try {
            uploadedFile = fileStorageService.store(file);
        } catch (Exception e) {
            log.error("Failed to process the uploaded file: {}. Caused: {}",
                    originalFilename, e.getMessage());
            return new FileProcessingResponse(
                    null,
                    originalFilename,
                    DocumentStatus.FAILED
            );
        }

        CompletableFuture.runAsync(
                () -> dataLoaderService.uploadKnowledge(uploadedFile, originalFilename),
                executor
        );
        // 2. Pass the byte array / filename to the virtual thread
//        Thread.startVirtualThread(() -> {
//            try {
//                dataLoaderService.uploadKnowledge(uploadedFile, originalFilename);
//                log.info("Successfully uploaded content from {}", originalFilename);
//            } catch (Exception e) {
//                log.error("Async parsing failed for file {}", originalFilename, e);
//            }
//        });
        return new FileProcessingResponse(
                null,
                originalFilename,
                DocumentStatus.PROCESSING
        );
    }

    public FileProcessingResponse loadFromUrl(String url) {
        dataLoaderService.loadFromUrl(url);
        CompletableFuture.runAsync(
                () -> dataLoaderService.loadFromUrl(url),
                executor
        );
//        Thread.startVirtualThread(() -> {
//            try {
//                dataLoaderService.loadFromUrl(url);
//                log.info("Successfully uploaded content from {}", url);
//            } catch (Exception e) {
//                log.error("Async parsing failed for {}", url, e);
//            }
//        });
        return new FileProcessingResponse(
                null,
                url,
                DocumentStatus.PROCESSING
        );
    }
}
