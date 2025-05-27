import { RegionApiService } from "../../../entities/region/api/regionApi";
import { Region } from "../../../entities/region/model/types";
import { Group } from "../../../entities/group/model/types";
import { Device } from "../../../entities/device/model/types";
import { GroupApiService } from "../../../entities/group/api/api";
import { deviceApiService } from "../../../entities/device/api/api";

export const fetchRegions = async (): Promise<Region[]> => {
  return await RegionApiService.getAll();
};

export const fetchGroups = async (): Promise<Group[]> => {
  return await GroupApiService.getAll();
};

export const fetchDevices = async (): Promise<Device[]> => {
  return await deviceApiService.getAll();
};
