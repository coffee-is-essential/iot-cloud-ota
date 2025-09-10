import { apiClient } from "../../../shared/api/client";
import { DeviceRegisterInfo, DeviceRegisterResponse } from "../model/types";

/**
 * 디바이스 등록 관련 API 요청을 처리하는 서비스
 * @namespace deviceRegisterApiService
 */
export const deviceRegisterApiService = {
  /**
   * 새로운 디바이스를 등록합니다.
   * @async
   * @param {number} regionId - 디바이스가 속한 지역 ID
   * @param {number} groupId - 디바이스가 속한 그룹 ID
   * @return {Promise<DeviceRegisterInfo>} - 등록 코드와 만료 날짜를 반환합니다.
   */
  registerDevice: async (
    regionId: number,
    groupId: number,
  ): Promise<DeviceRegisterInfo> => {
    const { data } = await apiClient.post<DeviceRegisterResponse>(
      "/api/devices",
      {
        regionId,
        groupId,
      },
    );

    return {
      code: data.code,
      expiresAt: new Date(data.expiresAt),
    };
  },
};
