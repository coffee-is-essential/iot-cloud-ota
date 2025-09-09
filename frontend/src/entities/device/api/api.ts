import { apiClient } from "../../../shared/api/client";
import { PaginatedApiResponse } from "../../../shared/api/types";
import {
  Device,
  DeviceDetail,
  DeviceDetailResponse,
  PaginatedDevice,
} from "../model/types";

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
   * 디바이스(Device) 목록을 페이지네이션하여 조회합니다.
   * @async
   * @param {number} page - 조회할 페이지 번호 (기본값: 1)
   * @param {number} limit - 페이지당 아이템 수 (기본값: 10)
   * @param {number} [regionId] - 선택적 지역 ID 필터
   * @param {number} [groupId] - 선택적 그룹 ID 필터
   * @returns {Promise<PaginatedDevice>} 디바이스 정보 객체의 배열과 페이지네이션 메타데이터를 포함한 객체를 반환합니다.
   * @example
   * const { items, paginationMeta } = await deviceApiService.getDeviceList(1, 10, 2, 3);
   */
  getDeviceList: async (
    page: number = 1,
    limit: number = 10,
    regionId?: number,
    groupId?: number,
  ): Promise<PaginatedDevice> => {
    // NOTE: 이 엔드포인트는 기기 관리 페이지에서 사용됩니다.
    const { data } = await apiClient.get<PaginatedApiResponse<Device>>(
      `/api/devices/list`,
      {
        params: {
          page: page,
          limit: limit,
          regionId: regionId,
          groupId: groupId,
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

  /**
   * 특정 디바이스(Device)의 상세 정보를 조회합니다.
   * @async
   * @param {number} deviceId - 조회할 디바이스의 ID
   * @returns {Promise<DeviceDetail>} 디바이스 상세 정보 객체를 반환합니다.
   * @example
   * const deviceDetail = await deviceApiService.getDeviceDetail(1);
   */
  getDeviceDetail: async (deviceId: number): Promise<DeviceDetail> => {
    const { data } = await apiClient.get<DeviceDetailResponse>(
      `/api/devices/${deviceId}`,
    );

    return {
      ...data,
      createdAt: new Date(data.createdAt),
      modifiedAt: new Date(data.modifiedAt),
      lastActiveAt: data.lastActiveAt ? new Date(data.lastActiveAt) : null,
      advertisements: data.advertisements.map((ad) => ({
        ...ad,
        deployedAt: new Date(ad.deployedAt),
      })),
    };
  },
};
