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

  /**
   * 새로운 지역(Region)을 등록합니다.
   * @async
   * @param {string} regionName - 등록할 지역의 이름
   * @returns {Promise<Region>} 등록된 지역 정보 객체를 반환합니다.
   * @example
   * const newRegion = await RegionApiService.registerRegion('New Region');
   */
  registerRegion: async (regionName: string): Promise<Region> => {
    const { data } = await apiClient.post<Region>(`/api/regions`, {
      regionName,
    });
    return data;
  },
};
