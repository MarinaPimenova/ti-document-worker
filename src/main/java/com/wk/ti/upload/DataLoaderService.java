package com.wk.ti.upload;

import com.wk.ti.embedding.service.ImageReaderService;
import com.wk.ti.embedding.service.PdfFileReaderService;
import com.wk.ti.embedding.web.WebPageReaderService;
import com.wk.ti.exception.UnsupportedFileType;
import com.wk.ti.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;

import static com.wk.ti.util.FileValidationUtil.*;

@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final PdfFileReaderService pdfFileReaderService;
    private final WebPageReaderService webPageReaderService;
    private final ImageReaderService imageReaderService;

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
