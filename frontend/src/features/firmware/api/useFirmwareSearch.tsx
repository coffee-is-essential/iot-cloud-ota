import { useEffect, useState } from "react";
import { Firmware } from "../../../entities/firmware/model/types";
import { firmwareApiService } from "../../../entities/firmware/api/api";

/**
 * Custom hook for searching firmware
 * @function useFirmwareSearch
 * @description This hook provides functionality to search for firmware by version.
 * It manages the state of the firmware list, loading status, and error messages.
 * It also provides a function to handle the search operation.
 * @returns {firmwares: Firmware[], isLoading: boolean, error: string | null, handleSearch: (query: string) => void}
 */
export const useFirmwareSearch = () => {
  const [firmwares, setFirmwares] = useState<Firmware[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const handleSearch = async (query: string) => {
    try {
      setIsLoading(true);
      setError(null);

      if (!query.trim()) {
        const allFirmwares = await firmwareApiService.getAll();
        setFirmwares(allFirmwares);
        return;
      }

      const results = await firmwareApiService.search(query);
      setFirmwares(results);
    } catch (err) {
      setError("검색 중 오류가 발생했습니다.");
      console.error("Error searching firmware:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    handleSearch("");
  }, []);

  return { firmwares, isLoading, error, handleSearch };
};
