import { apiClient } from "../../../shared/api/client";
import { mapGroupDto } from "../model/mappers";
import { Group, GroupDto } from "../model/types";

/**
 * Service for fetching group data from the API.
 * @namespace GroupApiService
 */
export const GroupApiService = {
  /**
   * Retrieves all groups from the API.
   * @returns {Promise<Group[]>} A promise that resolves to an array of Group objects.
   * @throws Will throw an error if the API call fails.
   */
  getAll: async (): Promise<Group[]> => {
    const response = await apiClient.get("/api/groups");

    if (response.status !== 200) {
      throw new Error(`Failed to fetch groups: ${response.status}`);
    }

    const data = response.data.data as GroupDto[];

    return data.map((group) => mapGroupDto(group));
  },
};
