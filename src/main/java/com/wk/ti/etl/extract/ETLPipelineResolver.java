package com.wk.ti.etl.extract;

import com.wk.ti.etl.transform.web.WebPageReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ETLPipelineResolver {
    private final List<ETLPipelineStrategy> extractors;
    private final WebPageReaderService webPageReaderService;

    public void process(File uploadedFile, String originalFileName) {
        extractors.stream()
                .filter(extractor -> extractor.supports(originalFileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(""))
                .process(uploadedFile, originalFileName);
    }

    public String loadFromUrl(String url) {
        Assert.hasText(url, "URL must not be empty");
        webPageReaderService.addWebPageContent(url);
        return "Web content ingested from: " + url;
    }
}
