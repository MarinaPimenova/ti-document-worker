package com.wk.ti.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class DocumentProcessingConfiguration {

    @Bean(name = "documentProcessingExecutor")
    public Executor documentProcessingExecutor() {

        SimpleAsyncTaskExecutor executor =
                new SimpleAsyncTaskExecutor("document-processing-");

        executor.setVirtualThreads(true);
        executor.setConcurrencyLimit(4);

        return executor;
    }
}
