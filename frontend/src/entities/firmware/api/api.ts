import { Firmware, FirmwareDto } from "../model/types";
import { mapFirmwareDto } from "../model/mappers";
import { apiClient } from "../../../shared/api/client";
import { ApiReponse } from "../../../shared/api/types";

/**
 * Service responsible for handling firmware-related API requests
 * @namespace firmwareApiService
 */
export const firmwareApiService = {
  /**
   * Retrieves a paginated list of all firmware
   * @async
   * @param {number} [page=1] - The page number to retrieve
   * @param {number} [limit=10] - The number of items per page
   * @returns {Promise<Firmware[]>} - A promise that resolves to an array of Firmware objects
   * @throws Will log error and return empty array if API call fails
   * @example
   * // Get first page with default limit
   * const firmwares = await firmwareApiService.getAll();
   *
   * // Get third page with 20 items per page
   * const firmwaresPage3 = await firmwareApiService.getAll(3, 20);
   */
  getAll: async (page: number = 1, limit: number = 10): Promise<Firmware[]> => {
    try {
      const { data } = await apiClient.get<ApiReponse<FirmwareDto[]>>(
        `/api/firmware`,
        { params: { page: page, limit: limit } }
      );

      return data.data.map(mapFirmwareDto);
    } catch (error) {
      console.error("Failed to fetch firmware list:", error);
      return [];
    }
  },

  /**
   * Retrieves a specific firmware by its ID
   * @async
   * @param {number} id - The unique identifier of the firmware to retrieve
   * @returns {Promise<Firmware | null>} - A promise that resolves to a Firmware object or null if not found
   * @throws Will log error and return null if API call fails
   * @example
   * // Get firmware with ID 1
   * const firmware = await firmwareApiService.getOneById(1);
   *
   * // Check if firmware exists before using it
   * if (firmware) {
   *   console.log(`Firmware version: ${firmware.version}`);
   * } else {
   *   console.log('Firmware not found');
   * }
   */
  getOneById: async (id: number): Promise<Firmware | null> => {
    try {
      const { data } = await apiClient.get<ApiReponse<FirmwareDto>>(
        `api/firmware/${id}`
      );

      return mapFirmwareDto(data.data);
    } catch (error) {
      console.error(`Failed to fetch firmware with id ${id}:`, error);
      return null;
    }
  },
};
