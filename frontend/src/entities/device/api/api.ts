import { apiClient } from "../../../shared/api/client";
import {
  PaginatedApiResponse,
  PaginationMeta,
} from "../../../shared/api/types";
import { Device, PaginatedDevice } from "../model/types";

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
    // NOTE: 이 엔드포인트는 배포 모달창에서 사용됩니다.
    const { data } = await apiClient.get<Device[]>(`/api/devices`);

    return data.map((device) => ({
      ...device,
      lastActiveAt: new Date(device.lastActiveAt!),
    }));
  },

  /**
   * 모든 디바이스(Device) 목록을 조회합니다.
   * @async
   * @returns {Promise<Device[]>} 디바이스 정보 객체의 배열을 반환합니다.
   * @example
   * const devices = await deviceApiService.getDeviceList();
   */
  getDeviceList: async (
    page: number = 1,
    limit: number = 10,
    query?: string,
  ): Promise<PaginatedDevice> => {
    // NOTE: 이 엔드포인트는 기기 관리 페이지에서 사용됩니다.
    const { data } = await apiClient.get<PaginatedApiResponse<Device>>(
      `/api/devices/list`,
      {
        params: {
          page: page,
          limit: limit,
          search: query,
        },
      },
    );

    return {
      items: data.items.map((device) => ({
        ...device,
        lastActiveAt: new Date(device.lastActiveAt!),
      })),
      paginationMeta: data.paginationMeta,
    };
  },
};
