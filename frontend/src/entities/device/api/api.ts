import { apiClient } from "../../../shared/api/client";
import { Device } from "../model/types";

/**
 * API로부터 디바이스(Device) 데이터를 가져오는 서비스입니다.
 * @namespace deviceApiService
 */
export const deviceApiService = {
  /**
   * 모든 디바이스(Device) 목록을 조회합니다.
   * @async
   * @returns {Promise<Device[]>} 디바이스 정보 객체의 배열을 반환합니다.
   * @example
   * const devices = await deviceApiService.getDevices();
   */
  getDevices: async (): Promise<Device[]> => {
    const { data } = await apiClient.get<Device[]>(`/api/devices`);
    return data;
  },
};
