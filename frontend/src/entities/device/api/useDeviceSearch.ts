import { useState } from "react";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { Device } from "../model/types";
import { deviceApiService } from "./api";
import { PaginationMeta } from "../../../shared/api/types";

/**
 * 디바이스 검색을 위한 커스텀 훅입니다.
 * React Query를 사용하여 디바이스 목록 조회, 검색, 필터링, 페이지네이션을 관리합니다.
 */
export const useDeviceSearch = () => {
  const [page, setPage] = useState(1);
  const [query, setQuery] = useState("");
  const [filters, setFilters] = useState<{
    regionId?: number;
    groupId?: number;
  }>({});
  const limit = 10;

  const { data, isLoading, error, isPlaceholderData } = useQuery({
    queryKey: ["devices", query, page, limit, filters],
    queryFn: () =>
      deviceApiService.getDeviceList(
        page,
        limit,
        filters.regionId,
        filters.groupId,
      ),
    placeholderData: keepPreviousData,
  });

  const handleSearch = (searchQuery: string) => {
    setPage(1);
    setQuery(searchQuery);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const handleFilterChange = (newFilters: {
    regionId?: number;
    groupId?: number;
  }) => {
    setPage(1);
    setFilters(newFilters);
  };

  return {
    devices: (data?.items as Device[]) ?? [],
    pagination: data?.paginationMeta as PaginationMeta | undefined,
    isLoading: isLoading || isPlaceholderData,
    error: error ? "디바이스 목록을 불러오는 중 오류가 발생했습니다." : null,
    handleSearch,
    handlePageChange,
    handleFilterChange,
  };
};
