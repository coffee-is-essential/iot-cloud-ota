package com.coffee_is_essential.iot_cloud_ota.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileInfo {
    Long id;
    @JsonProperty("file_hash")
    String fileHash;
    Long size;
}
