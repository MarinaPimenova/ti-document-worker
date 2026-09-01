package com.wk.ti.document.model;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;

public final class DocumentMetadataKeys_2 {

    private DocumentMetadataKeys_2() {
    }

    public static final String DOCUMENT_ID =
            "document_id";

    public static final String SECTION_ID =
            "section_id";

    public static final String SECTION_NUMBER =
            "section_number";

    public static final String FILENAME =
            "filename";

    public static final String START_PAGE_NUMBER =
            PagePdfDocumentReader.METADATA_START_PAGE_NUMBER;

    public static final String END_PAGE_NUMBER =
            PagePdfDocumentReader.METADATA_END_PAGE_NUMBER;
}