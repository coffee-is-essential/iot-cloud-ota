package com.coffee_is_essential.iot_cloud_ota.domain;

/**
 * S3 객체의 파일 크기와 SHA-256 해시 값을 담는 클래스 입니다.
 *
 * @param fileSize S3 객체의 크기 (바이트 단위)
 * @param fileHash S3 객체의 SHA-256 해시 값 (16진수 문자열)
 */
public record S3FileHashResult(
        long fileSize,
        String fileHash
) {
}
