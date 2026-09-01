package com.wk.ti.ai.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wk.ti.ai.logger.interceptor.DetailedLLMLoggingInterceptor;
import com.wk.ti.ai.logger.interceptor.LLMRetryInterceptor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import org.springframework.web.filter.RequestContextFilter;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    @Bean
    public RequestContextFilter requestContextFilter() {
        return new RequestContextFilter();
    }

    // Increase HTTP client read timeout to 120+ seconds
    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(
            @Value("${http.client.connect-timeout:30000}") int connectTimeout,
            @Value("${http.client.read-timeout:125000}") int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout); // from property, default 30 seconds
        factory.setReadTimeout(readTimeout);       // from property, default 125 seconds
        return factory;
    }

    @Bean
    public RestClientCustomizer llmRestClientCustomizer(
            SimpleClientHttpRequestFactory simpleClientHttpRequestFactory,
            ObjectMapper objectMapper,
            @Value("${spring.ai.openai.chat.completions-path}") String gptCompletionUrl) {

        return restClientBuilder -> restClientBuilder
                .requestFactory(new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory))
                .requestInterceptor(new LLMRetryInterceptor(gptCompletionUrl))
                .requestInterceptor(new DetailedLLMLoggingInterceptor(objectMapper));
    }
}
