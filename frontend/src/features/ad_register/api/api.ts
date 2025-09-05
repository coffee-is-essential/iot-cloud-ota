import { apiClient } from "../../../shared/api/client";
import { AdUploadPresignedUrls } from "../model/types";

/**
 * 광고 등록 관련 API 요청을 처리하는 서비스
 * @namespace adRegisterApiService
 */
export const adRegisterApiService = {
  /**
   * 광고 파일 업로드를 위한 사전 서명된 URL을 가져옵니다.
   * @async
   * @param {string} title - 광고 제목
   * @return {Promise<AdUploadPresignedUrls>} - S3에 업로드할 수 있는 사전 서명된 URL과 원본 URL을 반환합니다.
   * */
  getPresignedUploadUrl: async (
    title: string,
  ): Promise<AdUploadPresignedUrls> => {
    const { data } = await apiClient.post<AdUploadPresignedUrls>(
      `/api/s3/ads/presigned_upload`,
      {
        title,
      },
    );
    return data;
  },

  /**
   * S3에 광고 파일을 업로드합니다.
   * @param {string} url - S3에 업로드할 사전 서명된 URL
   * @param {File} file - 업로드할 광고 파일
   * @return {Promise<void>} - 업로드 완료 후 아무 값도 반환하지 않습니다.
   */
  uploadAdViaPresignedUrl: async (url: string, file: File): Promise<void> => {
    await apiClient.put(url, file, {
      headers: {
        "Content-Type": file.type,
      },
    });
  },

  /**
   * 광고 메타데이터를 서버에 업로드합니다.
   * @param {string} title - 광고 제목
   * @param {string} description - 광고 설명
   * @param {string} originalS3Path - 원본 광고 파일의 S3 경로
   * @param {string} binaryS3Path - 광고 미리보기 파일의 S3 경로
   * @return {Promise<void>} - 업로드 완료 후 아무 값도 반환하지 않습니다.
   */
  uploadAdMetadata: async (
    title: string,
    description: string,
    originalS3Path: string,
    binaryS3Path: string,
  ): Promise<void> => {
    await apiClient.post(`/api/ads/metadata`, {
      title,
      description,
      originalS3Path,
      binaryS3Path,
    });
  },
};
