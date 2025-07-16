package com.coffee_is_essential.iot_cloud_ota.controller;

import com.coffee_is_essential.iot_cloud_ota.service.CloudFrontSignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class CloudFrontController {
    private final CloudFrontSignedUrlService cloudFrontSignedUrlService;

    @GetMapping("/cloudfront/signd-url")
    public ResponseEntity<String> getSignedUrl(@RequestParam String path) throws Exception {
        String url = cloudFrontSignedUrlService.generateSignedUrl(path, Duration.ofMinutes(5));
        return ResponseEntity.ok(url);
    }
}
