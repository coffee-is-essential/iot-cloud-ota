import { apiClient } from "../../../shared/api/client";
import { mapRegionDto } from "../model/mappers";
import { Region, RegionDto } from "../model/types";

/**
 * Service for fetching region data from the API.
 * @namespace RegionApiService
 */
export const RegionApiService = {
  /**
   * Retrieves all regions from the API.
   * @returns {Promise<Region[]>} A promise that resolves to an array of Region objects.
   * @throws Will throw an error if the API call fails.
   */
  getAll: async (): Promise<Region[]> => {
    const response = await apiClient.get("api/region");

    if (response.status !== 200) {
      throw new Error(`Failed to fetch regions: ${response.status}`);
    }

    const data = response.data.data as RegionDto[];

    return data.map((region) => mapRegionDto(region));
  },
};
