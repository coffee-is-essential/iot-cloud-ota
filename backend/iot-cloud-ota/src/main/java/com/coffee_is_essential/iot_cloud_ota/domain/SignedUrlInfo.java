package com.coffee_is_essential.iot_cloud_ota.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignedUrlInfo {
    String url;
    int timeout;
}
