package com.wk.ti.upload;

import com.wk.ti.embedding.service.DataLoaderService;
import com.wk.ti.upload.model.FileProcessingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestService {
    private final DataLoaderService dataLoaderService;

    public FileProcessingResponse upload(MultipartFile file) {
        // 1. Capture file metadata and content synchronously on the main thread
        String originalFilename = file.getOriginalFilename();

        File uploadedFile;
        try {
            uploadedFile = DataLoaderService.store(file);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read uploaded file", e);
        }

        // 2. Pass the byte array / filename to the virtual thread
        Thread.startVirtualThread(() -> {
            try {
                dataLoaderService.uploadKnowledge(uploadedFile, originalFilename);
                log.info("Successfully uploaded content from file {}", originalFilename);
            } catch (Exception e) {
                log.error("Async parsing failed for file {}", originalFilename, e);
            }
        });
        return new FileProcessingResponse(UUID.randomUUID().toString());
    }

    public FileProcessingResponse loadFromUrl(String url) {
        dataLoaderService.loadFromUrl(url);
        Thread.startVirtualThread(() -> {
            try {
                dataLoaderService.loadFromUrl(url);
                log.info("Successfully uploaded content from {}", url);
            } catch (Exception e) {
                log.error("Async parsing failed for {}", url, e);
            }
        });
        return new FileProcessingResponse(UUID.randomUUID().toString());
    }
}
