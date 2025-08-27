import { apiClient } from "../../../shared/api/client";
import { PaginatedApiResponse } from "../../../shared/api/types";
import {
  toFirmwareDeployment,
  toFirmwareDeploymentDetails,
} from "../model/mappers";
import {
  FirmwareDeploymentDetails,
  FirmwareDeploymentDetailsResponse,
  FirmwareDeploymentResponse,
  PaginatedFirmwareDeployments,
} from "../model/types";

/**
 * 펌웨어 배포 관련 API 요청을 처리하는 서비스
 * @namespace firmwareDeploymentApiService
 */
export const firmwareDeploymentApiService = {
  /**
   * 펌웨어 배포 목록을 조회합니다.
   * @async
   * @param {number} [page=1] - 조회할 페이지 번호 (기본값: 1)
   * @param {number} [limit=10] - 페이지 당 항목 수 (기본값: 10)
   * @param {string} [query] - 검색어 (선택)
   * @returns {Promise<PaginatedFirmwareDeployments>} - 페이징된 펌웨어 배포 목록을 반환합니다.
   * @example
   * const data = await firmwareDeploymentApiService.getFirmwareDeployments(1, 10, '검색어');
   */
  getFirmwareDeployments: async (
    page: number = 1,
    limit: number = 10,
    query?: string,
  ): Promise<PaginatedFirmwareDeployments> => {
    const { data } = await apiClient.get<
      PaginatedApiResponse<FirmwareDeploymentResponse>
    >(`/api/firmwares/deployment/list`, {
      params: { page, limit, search: query },
    });

    return {
      ...data,
      items: data.items.map(toFirmwareDeployment),
      paginationMeta: data.paginationMeta,
    };
  },

  /**
   * 특정 ID의 펌웨어 배포 상세 정보를 조회합니다.
   * @async
   * @param {number} id - 조회할 펌웨어 배포의 고유 ID
   * @returns {Promise<FirmwareDeploymentDetails>} - 해당 펌웨어 배포 상세 정보를 반환합니다.
   * @example
   * const details = await firmwareDeploymentApiService.getFirmwareDeploymentDetails(1);
   */
  getFirmwareDeploymentDetails: async (
    id: number,
  ): Promise<FirmwareDeploymentDetails> => {
    const { data } = await apiClient.get<FirmwareDeploymentDetailsResponse>(
      `/api/firmwares/deployment/${id}`,
    );

    return toFirmwareDeploymentDetails(data);
  },
};
