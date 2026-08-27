package com.wk.ti.embedding.service;

import com.wk.ti.embedding.web.WebPageReaderService;
import com.wk.ti.exception.StorageException;
import com.wk.ti.exception.UnsupportedFileType;
import com.wk.ti.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.wk.ti.util.FileValidationUtil.*;

@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final PdfFileReaderService pdfFileReaderService;
    private final WebPageReaderService webPageReaderService;
    private final ImageReaderService imageReaderService;

    public static File store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            String uploadDir = "/uploads";
            Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
            Files.createDirectories(filePath.getParent()); // Create directory if it doesn't exist
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toFile();
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    // Upload new knowledge context
    public void uploadKnowledge(File uploadedFile,
                                String originalFileName) {
        Assert.notNull(uploadedFile, "Upload new knowledge context cannot be null");

        if (!FileValidationUtil.isValidFile(uploadedFile, originalFileName)) {
            throw new UnsupportedFileType("Only PDF and image files are allowed.");
        }

        if (isPdf(uploadedFile, originalFileName)) {
            pdfFileReaderService.addResource(uploadedFile);
        }

        if (isKnownImage(uploadedFile, originalFileName)) {
            imageReaderService.addResource(uploadedFile, originalFileName);
        }

    }

    public String loadFromUrl(String url) {
        Assert.hasText(url, "URL must not be empty");
        webPageReaderService.addWebPageContent(url);
        return "Web content ingested from: " + url;
    }

}
