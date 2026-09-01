package com.wk.ti.embedding.service;

import com.wk.ti.document.service.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class PdfFileReaderService {

    private final DocumentIngestionService documentIngestionService;

    public void addResource(File file) {

        FileSystemResource resource = new FileSystemResource(file);
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader documentReader =
                new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);

        documentIngestionService.ingest(file.getName(), file.length(),
                file);
//                documentReader);
    }
}
