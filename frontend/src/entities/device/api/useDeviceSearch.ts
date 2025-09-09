import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { deviceApiService } from "./api";

/**
 * 디바이스 검색 및 페이지네이션을 처리하는 커스텀 훅
 * useQuery를 사용하여 디바이스 목록을 가져오고, 검색어와 페이지 변경을 관리합니다.
 */
export const useDeviceSearch = () => {
  const [page, setPage] = useState(1);
  const [query, setQuery] = useState("");
  const limit = 10;

  const { data, isLoading, error, isPlaceholderData } = useQuery({
    queryKey: ["devices", query, page, limit],
    queryFn: () => deviceApiService.getDeviceList(page, limit, query),

    placeholderData: keepPreviousData,
  });

  const handleSearch = (searchQuery: string) => {
    setPage(1);
    setQuery(searchQuery);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  return {
    devices: data?.items ?? [],
    pagination: data?.paginationMeta,
    isLoading: isLoading || isPlaceholderData,
    error: error ? "디바이스 목록을 불러오는 중 오류가 발생했습니다." : null,
    handleSearch,
    handlePageChange,
  };
};
