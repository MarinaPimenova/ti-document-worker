package com.wk.ti.etl.transform.pdf;

import com.wk.ti.etl.transform.pdf.document.model.DocumentExtension;
import com.wk.ti.etl.transform.pdf.document.service.DocumentIngestionService;
import com.wk.ti.etl.extract.ETLPipelineStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class PdfFileReaderService implements ETLPipelineStrategy {

    private final DocumentIngestionService documentIngestionService;

    @Override
    public boolean supports(String originalFileName) {
        return DocumentExtension.PDF == DocumentExtension.fromFileName(originalFileName);
    }

    @Override
    public void process(File file, String originalFileName) {
        FileSystemResource resource = new FileSystemResource(file);
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader documentReader = new PagePdfDocumentReader(resource, pdfDocumentReaderConfig);

        documentIngestionService.ingest(originalFileName, file.length(), documentReader);
    }
}
