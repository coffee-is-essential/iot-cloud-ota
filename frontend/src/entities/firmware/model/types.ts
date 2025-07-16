import { PaginationMeta } from "../../../shared/api/types";

/**
 * 펌웨어 정보를 나타내는 인터페이스입니다.
 */
export interface Firmware {
  id: number;
  version: string;
  fileName: string;
  releaseNote: string;
  createdAt: Date;
  modifiedAt: Date;
}

/**
 * 페이징된 펌웨어 목록 응답 인터페이스입니다.
 */
export interface PaginatedFirmware {
  items: Firmware[];
  paginationMeta: PaginationMeta;
}

/**
 * 펌웨어 업로드를 위한 S3 사전 서명 URL 응답 인터페이스입니다.
 */
export interface PresignedUploadUrlResponse {
  url: string;
  s3Path: string;
}

/**
 * 펌웨어 다운로드를 위한 사전 서명된 URL 응답 인터페이스입니다.
 */
export interface PresignedDownloadUrlResponse {
  url: string;
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
