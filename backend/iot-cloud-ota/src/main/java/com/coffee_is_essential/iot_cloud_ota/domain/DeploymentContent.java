package com.coffee_is_essential.iot_cloud_ota.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeploymentContent {
    @JsonProperty("signed_url")
    SignedUrlInfo signedUrlInfo;

    @JsonProperty("file_info")
    FileInfo fileInfo;
}
