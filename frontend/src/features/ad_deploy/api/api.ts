import { apiClient } from "../../../shared/api/client";
import { Region } from "../../../entities/region/model/types";
import { Group } from "../../../entities/group/model/types";
import { Device } from "../../../entities/device/model/types";
import { DeploymentType } from "../model/types";
import { RegionApiService } from "../../../entities/region/api/regionApi";
import { GroupApiService } from "../../../entities/group/api/api";
import { deviceApiService } from "../../../entities/device/api/api";

export interface AdDeployRequest {
  adIds: number[];
  deploymentType: DeploymentType;
  regions?: Region[];
  groups?: Group[];
  devices?: Device[];
}

export const deployAds = async (request: AdDeployRequest) => {
  const response = await apiClient.post("/ad-deployments", request);
  return response.data;
};

export const fetchRegions = async (): Promise<Region[]> => {
  return await RegionApiService.getRegions();
};

export const fetchGroups = async (): Promise<Group[]> => {
  return await GroupApiService.getGroups();
};

export const fetchDevices = async (): Promise<Device[]> => {
  return await deviceApiService.getDevices();
};
