package com.wk.ti.upload;

import com.wk.ti.exception.StorageException;
import com.wk.ti.etl.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final StorageProperties storageProperties;

    public File store(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        String originalFilename = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFilename)) {
            throw new StorageException("Failed to store file without a filename.");
        }

        try {
            Path uploadDirectory = Paths.get(storageProperties.getStoragePath())
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(uploadDirectory);
            Path filePath = uploadDirectory
                    .resolve(Paths.get(originalFilename).getFileName())
                    .normalize();
            // Prevent path traversal:
            if (!filePath.startsWith(uploadDirectory)) {
                throw new StorageException("Invalid filename: " + originalFilename);
            }
            Files.copy(file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING);

            log.info("File '{}' stored at '{}'", originalFilename, filePath);

            return filePath.toFile();

        } catch (IOException e) {
            throw new StorageException("Failed to store file: " + originalFilename, e);
        }
    }
}
