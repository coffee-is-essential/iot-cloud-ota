import { RegionApiService } from "../../../entities/region/api/regionApi";
import { Region } from "../../../entities/region/model/types";
import { Group } from "../../../entities/group/model/types";
import { Device } from "../../../entities/device/model/types";
import { GroupApiService } from "../../../entities/group/api/api";
import { deviceApiService } from "../../../entities/device/api/api";
import { apiClient } from "../../../shared/api/client";

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
  return await GroupApiService.getGroups();
};

/**
 * Fetches all available devices from the API
 * @returns {Promise<Device[]>} Promise that resolves to an array of Device objects
 */
export const fetchDevices = async (): Promise<Device[]> => {
  return await deviceApiService.getDevices();
};

/**
 * Requests a firmware deployment to specified regions, groups, and devices
 * @param {number} firmwareId - The ID of the firmware to deploy
 * @param {Region[]} regions - Array of regions to deploy the firmware to
 * @param {Group[]} groups - Array of groups to deploy the firmware to
 * @param {Device[]} devices - Array of devices to deploy the firmware to
 * @returns {Promise<void>} Promise that resolves when the deployment request is complete
 */
export const requestFirmwareDeploy = async (
  firmwareId: number,
  regions: Region[],
  groups: Group[],
  devices: Device[],
): Promise<void> => {
  const regionIds = regions.map((region) => region.regionId);
  const groupIds = groups.map((group) => group.groupId);
  const deviceIds = devices.map((device) => device.deviceId);

  await apiClient.post(`/api/firmwares/metadata/${firmwareId}/deployment`, {
    regionIds: regionIds,
    groupIds: groupIds,
    deviceIds: deviceIds,
  });
};
