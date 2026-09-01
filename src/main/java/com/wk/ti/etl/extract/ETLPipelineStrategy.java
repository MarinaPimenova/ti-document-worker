package com.wk.ti.etl.extract;

import java.io.File;

public interface ETLPipelineStrategy {
    boolean supports(String originalFileName);

    void process(File uploadedFile, String originalFileName);

}
