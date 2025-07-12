import { RegionApiService } from "../../../entities/region/api/regionApi";
import { Region } from "../../../entities/region/model/types";
import { Group } from "../../../entities/group/model/types";
import { Device } from "../../../entities/device/model/types";
import { GroupApiService } from "../../../entities/group/api/api";
import { deviceApiService } from "../../../entities/device/api/api";

/**
 * Fetches all available regions from the API
 * @returns {Promise<Region[]>} Promise that resolves to an array of Region objects
 */
export const fetchRegions = async (): Promise<Region[]> => {
  return await RegionApiService.getRegions();
};

/**
 * Fetches all available groups from the API
 * @returns {Promise<Group[]>} Promise that resolves to an array of Group objects
 */
export const fetchGroups = async (): Promise<Group[]> => {
  return await GroupApiService.getAll();
};

/**
 * Fetches all available devices from the API
 * @returns {Promise<Device[]>} Promise that resolves to an array of Device objects
 */
export const fetchDevices = async (): Promise<Device[]> => {
  return await deviceApiService.getAll();
};
