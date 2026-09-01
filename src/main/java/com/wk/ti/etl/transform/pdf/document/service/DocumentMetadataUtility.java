package com.wk.ti.etl.transform.pdf.document.service;

import org.springframework.ai.document.Document;
import org.springframework.util.StringUtils;

public class DocumentMetadataUtility {
    private DocumentMetadataUtility() {}


    public static Integer getIntegerMetadata(
            Document document,
            String key) {

        Object value = document.getMetadata().get(key);

        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String string
                && StringUtils.hasText(string)) {

            try {
                return Integer.valueOf(string);
            } catch (NumberFormatException ignored) {
                // Ignore malformed metadata.
            }
        }
        return null;
    }

}
