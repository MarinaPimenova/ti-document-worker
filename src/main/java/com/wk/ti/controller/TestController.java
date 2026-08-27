package com.wk.ti.controller;

import com.wk.ti.upload.TestService;
import com.wk.ti.upload.model.FileProcessingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/rest/v1/upload")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileProcessingResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(testService.upload(file));
    }

    @PostMapping("/load-url")
    public ResponseEntity<FileProcessingResponse> loadUrl(@RequestBody Map<String, String> request) {
        String url = request.get("url");
        return ResponseEntity.ok(testService.loadFromUrl(url));
    }
}
