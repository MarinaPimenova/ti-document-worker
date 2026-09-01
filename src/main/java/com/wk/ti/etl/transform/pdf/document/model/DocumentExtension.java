package com.wk.ti.etl.transform.pdf.document.model;

import lombok.Getter;

@Getter
public enum DocumentExtension {
    PDF("pdf"),
    XLSX("xlsx"),
    DOCX("docx"),
    TXT("txt"),
    MD("md"),
    CSV("csv");

    private final String value;

    DocumentExtension(String value) {
        this.value = value;
    }

    public static DocumentExtension fromFileName(String filename) {
        String ext = getRawFileExtension(filename);
        for (DocumentExtension docExt : values()) {
            if (docExt.value.equals(ext)) {
                return docExt;
            }
        }
        throw new IllegalArgumentException("Unsupported file extension: " + ext);
    }

    public static String getRawFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename without extension: " + filename);
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
