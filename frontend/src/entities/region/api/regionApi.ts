import { apiClient } from "../../../shared/api/client";
import { Region } from "../model/types";

/**
 * API로부터 지역(Region) 데이터를 가져오는 서비스입니다.
 * @namespace RegionApiService
 */
export const RegionApiService = {
  /**
   * 모든 지역(Region) 목록을 조회합니다.
   * @async
   * @returns {Promise<Region[]>} 지역 정보 객체의 배열을 반환합니다.
   * @example
   * const regions = await RegionApiService.getRegions();
   */
  getRegions: async (): Promise<Region[]> => {
    const { data } = await apiClient.get<Region[]>(`/api/regions`);
    return data;
  },
};
