import { apiClient } from "../../../shared/api/client";
import { PaginatedApiResponse } from "../../../shared/api/types";
import { Ad, PaginatedAds } from "../model/types";

/**
 * 광고 관련 API 요청을 처리하는 서비스
 * @namespace adApiService
 */
export const adApiService = {
  /**
   * 광고 목록을 조회합니다.
   * @async
   * @param {number} [page=1] - 조회할 페이지 번호 (기본값: 1)
   * @param {number} [limit=10] - 페이지 당 항목 수 (기본값: 10)
   * @param {string} [query] - 검색어 (선택)
   * @returns {Promise<PaginatedAds>} - 페이징된 광고 목록을 반환합니다.
   * @example
   * const data = await adApiService.getAds(1, 10, '검색어');
   */
  getAds: async (
    page: number = 1,
    limit: number = 10,
    query?: string,
  ): Promise<PaginatedAds> => {
    const { data } = await apiClient.get<PaginatedApiResponse<Ad>>(
      `/api/ads/metadata`,
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
};
