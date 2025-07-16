/**
 * 펌웨어 업로드를 위한 S3 사전 서명 URL 응답 인터페이스입니다.
 */
export interface PresignedUrlResponse {
  url: string;
  s3Path: string;
}

/**
 * 펌웨어 메타데이터 업로드 요청 인터페이스입니다.
 */
export interface FirmwareMetadataUploadRequest {
  version: string;
  releaseNote: string;
  fileName: string;
  s3Path: string;
}

/**
 * 펌웨어 등록 폼 데이터 인터페이스입니다.
 */
export interface FirmwareRegisterFormData {
  version: string;
  releaseNote: string;
  file: File | null;
}
