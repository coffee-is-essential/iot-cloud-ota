import {
  Firmware,
  FirmwareMetadataUploadRequest,
  PaginatedFirmware,
  PresignedDownloadUrlResponse,
  PresignedUploadUrlResponse,
} from "../model/types";
import { apiClient } from "../../../shared/api/client";
import { PaginatedApiResponse } from "../../../shared/api/types";

/**
 * 펌웨어 관련 API 요청을 처리하는 서비스
 * @namespace firmwareApiService
 */
export const firmwareApiService = {
  /**
   * 펌웨어 목록을 조회합니다.
   * @async
   * @param {number} [page=1] - 조회할 페이지 번호 (기본값: 1)
   * @param {number} [limit=10] - 페이지 당 항목 수 (기본값: 10)
   * @param {string} [query] - 검색어 (선택)
   * @returns {Promise<PaginatedFirmware>} - 페이징된 펌웨어 목록을 반환합니다.
   * @example
   * const data = await firmwareApiService.getFirmwares(1, 10, '검색어');
   */
  getFirmwares: async (
    page: number = 1,
    limit: number = 10,
    query?: string,
  ): Promise<PaginatedFirmware> => {
    const { data } = await apiClient.get<PaginatedApiResponse<Firmware>>(
      `/api/firmwares/metadata`,
      {
        params: {
          page: page,
          limit: limit,
          search: query,
        },
      },
    );
    return {
      items: data.items.map((item) => ({
        ...item,
        createdAt: new Date(item.createdAt),
        modifiedAt: new Date(item.modifiedAt),
      })),
      paginationMeta: data.paginationMeta,
    };
  },

  /**
   * 특정 ID의 펌웨어 정보를 조회합니다.
   * @async
   * @param {number} id - 조회할 펌웨어의 고유 ID
   * @returns {Promise<Firmware>} - 해당 펌웨어 정보를 반환합니다.
   * @example
   * const firmware = await firmwareApiService.getFirmware(1);
   */
  getFirmware: async (id: number): Promise<Firmware> => {
    const { data } = await apiClient.get<Firmware>(
      `/api/firmwares/metadata/${id}`,
    );
    return {
      ...data,
      createdAt: new Date(data.createdAt),
      modifiedAt: new Date(data.modifiedAt),
    };
  },

  /**
   * 새로운 펌웨어를 등록합니다.
   * @async
   * @param {string} version - 등록할 펌웨어 버전
   * @param {string} releaseNote - 펌웨어 릴리즈 노트
   * @param {File} file - 업로드할 펌웨어 파일
   * @returns {Promise<boolean>} - 등록 성공 시 true, 실패 시 false를 반환합니다.
   * @throws API 호출 실패 시 에러를 콘솔에 출력합니다.
   * @example
   * // 펌웨어 등록 예시
   * await firmwareApiService.register('1.0', '최초 릴리즈', myFirmwareFile);
   */
  register: async (
    version: string,
    releaseNote: string,
    file: File,
  ): Promise<boolean> => {
    try {
      const formData = new FormData();
      formData.append("version", version);
      formData.append("release_note", releaseNote);
      formData.append("file", file);

      const response = await apiClient.post(
        "/api/firmware/register",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );

      return response.status === 200;
    } catch (error) {
      console.error("펌웨어 등록에 실패했습니다:", error);
      return false;
    }
  },

  /**
   * S3에 펌웨어 파일 업로드를 위한 Presigned URL을 발급받습니다.
   * @async
   * @param {string} version - 펌웨어 버전
   * @param {string} fileName - 업로드할 파일명
   * @returns {Promise<PresignedUploadUrlResponse>} - Presigned URL 응답을 반환합니다.
   * @example
   * const res = await firmwareApiService.getFirmwarePresignedUploadUrl('1.0', 'firmware.bin');
   */
  getFirmwarePresignedUploadUrl: async (
    version: string,
    fileName: string,
  ): Promise<PresignedUploadUrlResponse> => {
    const { data } = await apiClient.post<PresignedUploadUrlResponse>(
      `/api/s3/presigned_upload`,
      {
        version,
        fileName,
      },
    );
    return data;
  },

  /**
   * S3에서 펌웨어 파일 다운로드를 위한 Presigned URL을 발급받습니다.
   * @async
   * @param {string} version - 다운로드할 펌웨어 버전
   * @param {string} fileName - 다운로드할 파일명
   * @return {Promise<string>} - Presigned URL을 반환합니다.
   * @example
   * const url = await firmwareApiService.getFirmwarePresignedDownloadUrl('1.0', 'firmware.bin');
   */
  getFirmwarePresignedDownloadUrl: async (
    version: string,
    fileName: string,
  ): Promise<string> => {
    const { data } = await apiClient.get<PresignedDownloadUrlResponse>(
      `/api/s3/firmware/download`,
      {
        params: {
          version,
          fileName,
        },
      },
    );
    return data.url;
  },

  /**
   * S3에서 펌웨어 파일을 다운로드합니다.
   * @async
   * @param {string} presignedUrl - 다운로드를 위한 Presigned URL
   * @return {Promise<Blob>} - 다운로드된 파일의 Blob 객체를 반환합니다.
   * @example
   * const blob = await firmwareApiService.downloadFirmwareFile('https://example.com/presigned-url');
   */
  downloadFirmwareFile: async (presignedUrl: string): Promise<Blob> => {
    const response = await apiClient.get(presignedUrl, {
      responseType: "blob",
    });

    return response.data;
  },

  /**
   * S3에 펌웨어 파일을 업로드합니다.
   * @async
   * @param {string} url - Presigned URL
   * @param {File} file - 업로드할 파일
   * @return {Promise<void>} - 업로드 완료 후 void를 반환합니다.
   * @example
   * await firmwareApiService.uploadFirmwareViaPresignedUrl('https://example.com/presigned-url', myFirmwareFile);
   */
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

  /**
   * 펌웨어 메타데이터를 S3에 업로드합니다.
   * @async
   * @param {FirmwareMetadataUploadRequest} firmwareMetadata - 업로드할 펌웨어 메타데이터
   * @return {Promise<void>} - 업로드 완료 후 void를 반환합니다.
   * @example
   * await firmwareApiService.uploadFirmwareMetadata({
   * version: '1.0',
   * releaseNote: '최초 릴리즈',
   * fileName: 'firmware.bin',
   * s3Path: 'path/to/firmware.bin',
   * });
   */
  uploadFirmwareMetadata: async (
    firmwareMetadata: FirmwareMetadataUploadRequest,
  ): Promise<void> => {
    await apiClient.post<void>(`/api/firmwares/metadata`, firmwareMetadata);
  },
};
