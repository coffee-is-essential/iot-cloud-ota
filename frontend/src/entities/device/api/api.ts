import { apiClient } from "../../../shared/api/client";
import { mapDeviceDto } from "../model/mappers";
import { Device, DeviceDto } from "../model/types";

/**
 * Service for fetching device data from the API.
 * @namespace deviceApiService
 */
export const deviceApiService = {
  /**
   * Retrieves all devices from the API.
   * @returns {Promise<Device[]>} A promise that resolves to an array of Device objects.
   * @throws Will throw an error if the API call fails.
   */
  getAll: async (): Promise<Device[]> => {
    const response = await apiClient.get("/api/device");
    if (response.status !== 200) {
      throw new Error(`Failed to fetch devices: ${response.status}`);
    }

    const data = response.data.data as DeviceDto[];

    return data.map((dto) => mapDeviceDto(dto));
  },
};
