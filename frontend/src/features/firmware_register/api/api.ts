import { firmwareApiService } from "../../../entities/firmware/api/api";
import {
  FirmwareMetadataUploadRequest,
  PresignedUploadUrlResponse,
} from "../../../entities/firmware/model/types";

/**
 * 펌웨어 등록 관련 API 요청을 처리하는 서비스
 * @namespace firmwareRegisterApiService
 */
export const firmwareRegisterApiService = {
  /**
   * 펌웨어 파일 업로드를 위한 사전 서명된 URL을 가져옵니다.
   * @async
   * @param {string} version - 펌웨어 버전
   * @param {string} fileName - 업로드할 파일 이름
   * @return {Promise<PresignedUploadUrlResponse>} - S3에 업로드할 수 있는 사전 서명된 URL과 S3 경로를 반환합니다.
   * */
  getPresignedUrl: async (
    version: string,
    fileName: string,
  ): Promise<PresignedUploadUrlResponse> => {
    return await firmwareApiService.getFirmwarePresignedUploadUrl(
      version,
      fileName,
    );
  },

  /**
   * S3에 펌웨어 파일을 업로드합니다.
   * @param {string} url - S3에 업로드할 사전 서명된 URL
   * @param {File} file - 업로드할 펌웨어 파일
   * @return {Promise<void>} - 업로드 완료 후 아무 값도 반환하지 않습니다.
   */
  uploadFirmwareViaPresignedUrl: async (
    url: string,
    file: File,
  ): Promise<void> => {
    return await firmwareApiService.uploadFirmwareViaPresignedUrl(url, file);
  },

  /**
   * 펌웨어 메타데이터를 서버에 업로드합니다.
   * @param {FirmwareMetadataUploadRequest} firmwareMetadata - 업로드할 펌웨어 메타데이터
   * @return {Promise<void>} - 업로드 완료 후 아무 값도 반환하지 않습니다.
   */
  uploadFirmwareMetadata: async (
    firmwareMetadata: FirmwareMetadataUploadRequest,
  ): Promise<void> => {
    return await firmwareApiService.uploadFirmwareMetadata(firmwareMetadata);
  },
};
