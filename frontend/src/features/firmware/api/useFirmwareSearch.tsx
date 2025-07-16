import { useCallback, useEffect, useState } from "react";
import { Firmware } from "../../../entities/firmware/model/types";
import { firmwareApiService } from "../../../entities/firmware/api/api";
import { PaginationMeta } from "../../../shared/api/types";

/**
 * Custom hook for searching firmware with pagination
 * @function useFirmwareSearch
 * @description This hook provides functionality to search for firmware by version with pagination.
 * It manages the state of the firmware list, pagination, loading status, and error messages.
 * @returns {Object} Firmware data, pagination controls and search functionality
 */
export const useFirmwareSearch = () => {
  const [firmwares, setFirmwares] = useState<Firmware[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState<string>("");

  // Add pagination state
  const [pagination, setPagination] = useState<PaginationMeta>({
    page: 1,
    totalCount: 0,
    limit: 10,
    totalPage: 1,
  });

  /**
   * Search handling function
   * @param {string} searchQuery - The search query string
   * @param {number} [page=1] - Page number
   */
  const handleSearch = useCallback(
    async (searchQuery: string, page: number = 1) => {
      try {
        setIsLoading(true);
        setError(null);
        setQuery(searchQuery);

        // Get all firmwares if search query is empty
        if (!searchQuery.trim()) {
          const response = await firmwareApiService.getFirmwares(
            page,
            pagination.limit,
          );
          console.log("firmwareApiService.getAll", response);
          setFirmwares(response.items);
          setPagination(response.paginationMeta);
          return;
        }

        // Search for firmwares based on the query
        const response = await firmwareApiService.getFirmwares(
          page,
          pagination.limit,
          searchQuery,
        );

        setFirmwares(response.items);
        setPagination(response.paginationMeta);
      } catch (err) {
        setError("검색 중 오류가 발생했습니다.");
        console.error("Error searching firmware:", err);
      } finally {
        setIsLoading(false);
      }
    },
    [pagination.limit],
  );

  /**
   * Page change handling function
   * @param newPage - New page number
   */
  const handlePageChange = (newPage: number) => {
    // Verify if the new page is within the valid range
    if (newPage < 1 || newPage > pagination.totalPage) return;

    // Search with current query on the new page
    handleSearch(query, newPage);
  };

  /**
   * Items per page change handling function
   * @param newLimit - New number of items per page
   */
  const handleLimitChange = (newLimit: number) => {
    // After changing the limit, reset to the first page
    const updatedPagination = { ...pagination, limit: newLimit, page: 1 };
    setPagination(updatedPagination);
    handleSearch(query, 1);
  };

  // Load the first page when component mounts
  useEffect(() => {
    handleSearch("", 1);
  }, [handleSearch]);

  return {
    firmwares,
    isLoading,
    error,
    pagination,
    handleSearch,
    handlePageChange,
    handleLimitChange,
  };
};
