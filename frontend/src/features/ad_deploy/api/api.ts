import { apiClient } from "../../../shared/api/client";
import { Region } from "../../../entities/region/model/types";
import { Group } from "../../../entities/group/model/types";
import { Device } from "../../../entities/device/model/types";
import { DeploymentType } from "../model/types";
import { RegionApiService } from "../../../entities/region/api/regionApi";
import { GroupApiService } from "../../../entities/group/api/api";
import { deviceApiService } from "../../../entities/device/api/api";

/**
 * 광고 배포 요청 인터페이스
 * - adIds: 배포할 광고 ID 배열
 * - deploymentType: 배포 유형 (지역, 그룹, 기기)
 * - regions: 선택된 지역 배열 (선택 사항)
 * - groups: 선택된 그룹 배열 (선택 사항)
 * - devices: 선택된 기기 배열 (선택 사항)
 */
export interface AdDeployRequest {
  adIds: number[];
  deploymentType: DeploymentType;
  regions?: Region[];
  groups?: Group[];
  devices?: Device[];
}

/**
 * 광고를 배포하는 함수
 * @param {AdDeployRequest} request - 배포 요청 데이터
 */
export const deployAds = async (request: AdDeployRequest) => {
  const response = await apiClient.post("/ad-deployments", request);
  return response.data;
};

/**
 * API로부터 지역(Region) 데이터를 가져오는 함수
 * @returns {Promise<Region[]>} 지역 정보 객체의 배열을 반환하는 프로미스
 */
export const fetchRegions = async (): Promise<Region[]> => {
  return await RegionApiService.getRegions();
};

/**
 * API로부터 그룹(Group) 데이터를 가져오는 함수
 * @returns {Promise<Group[]>} 그룹 정보 객체의 배열을 반환하는 프로미스
 */
export const fetchGroups = async (): Promise<Group[]> => {
  return await GroupApiService.getGroups();
};

/**
 * API로부터 디바이스(Device) 데이터를 가져오는 함수
 * @returns {Promise<Device[]>} 디바이스 정보 객체의 배열을 반환하는 프로미스
 */
export const fetchDevices = async (): Promise<Device[]> => {
  return await deviceApiService.getDevices();
};
