package com.wk.ti.document.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "question-generation")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuestionGenerationProperties {

    private Section section = new Section();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Section {

        private int targetChars = 12_000;

        private int maxChars = 18_000;

        private int minChars = 2_000;
    }
}
