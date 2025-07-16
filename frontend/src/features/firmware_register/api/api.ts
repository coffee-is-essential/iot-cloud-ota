import { apiClient } from "../../../shared/api/client";
import {
  FirmwareMetadataUploadRequest,
  PresignedUrlResponse,
} from "../model/types";

/**
 * 펌웨어를 등록하는 API 서비스입니다.
 * @namespace firmwareRegisterApiService
 */
export const firmwareRegisterApiService = {
  getPresignedUrl: async (
    version: string,
    fileName: string,
  ): Promise<PresignedUrlResponse> => {
    const { data } = await apiClient.post<PresignedUrlResponse>(
      `/api/s3/presigned_upload`,
      {
        version,
        fileName,
      },
    );
    return data;
  },

  uploadFirmwareViaPresignedUrl: async (
    url: string,
    file: File,
  ): Promise<void> => {
    await apiClient.put(url, file, {
      headers: {
        "Content-Type": file.type,
      },
    });
  },

  uploadFirmwareMetadata: async (
    firmwareMetadata: FirmwareMetadataUploadRequest,
  ): Promise<void> => {
    await apiClient.post<void>(`/api/firmwares/metadata`, firmwareMetadata);
  },
};
