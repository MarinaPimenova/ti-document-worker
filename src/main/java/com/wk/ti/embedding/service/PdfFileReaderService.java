package com.wk.ti.embedding.service;

import com.wk.ti.embedding.store.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class PdfFileReaderService {

    private final VectorStoreService vectorStoreService;

    public void addResource(File file) {

        FileSystemResource resource = new FileSystemResource(file);
        ExtractedTextFormatter textFormatter = ExtractedTextFormatter.builder()
                .withNumberOfBottomTextLinesToDelete(3)
                .withNumberOfTopPagesToSkipBeforeDelete(1)
                .build();
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(textFormatter)
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader pagePdfDocumentReader =
                new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);

        vectorStoreService.storeToVectorStore(pagePdfDocumentReader);
    }

    public List<Document> getResources(String query) {
        return vectorStoreService.similaritySearch(query);
    }
}
