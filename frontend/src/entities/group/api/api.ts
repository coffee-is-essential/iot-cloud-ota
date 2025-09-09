import { apiClient } from "../../../shared/api/client";
import { Group } from "../model/types";

/**
 * API로부터 그룹(Group) 데이터를 가져오는 서비스입니다.
 * @namespace GroupApiService
 */
export const GroupApiService = {
  /**
   * 모든 그룹(Group) 목록을 조회합니다.
   * @async
   * @returns {Promise<Group[]>} 그룹 정보 객체의 배열을 반환합니다.
   * @example
   * const groups = await GroupApiService.getGroups();
   */
  getGroups: async (): Promise<Group[]> => {
    const { data } = await apiClient.get<Group[]>(`/api/groups`);
    return data;
  },
};
