package com.wk.ti.document.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QuestionGenerationProperties.class)
public class QuestionGenerationConfiguration {
}
