import { useEffect, useState } from "react";
import { Firmware } from "../../../entities/firmware/model/types";
import { firmwareApiService } from "../../../entities/firmware/api/api";

/**
 * Interface for the return value of the useFirmwareDetail hook
 */
export interface FirmwareDetailHookResult {
  firmware: Firmware | null;
  isLoading: boolean;
  error: string | null;
}

/**
 * Custom React hook to fetch firmware details by ID
 *
 * This hook handles the API call to retrieve a specific firmware's details,
 * manages loading states, and handles potential errors during the fetch process.
 *
 * @param {number | null} id - The ID of the firmware to fetch. If null, an error state will be set.
 * @returns {FirmwareDetailHookResult} An object containing the firmware data, loading state, and any error messages
 */
export const useFirmwareDetail = (
  id: number | null
): FirmwareDetailHookResult => {
  const [firmware, setFirmware] = useState<Firmware | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFirmware = async () => {
      if (!id) {
        setError("펌웨어 ID가 유효하지 않습니다.");
        setIsLoading(false);
        return;
      }

      try {
        const result = await firmwareApiService.getOneById(id);
        if (!result) {
          setError("펌웨어를 찾을 수 없습니다.");
          setIsLoading(false);
          return;
        }
        setFirmware(result);
      } catch (error) {
        setError("펌웨어를 가져오는 중 오류가 발생했습니다.");
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchFirmware();
  }, [id]);

  return { firmware, isLoading, error };
};
