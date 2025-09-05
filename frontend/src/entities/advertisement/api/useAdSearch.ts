import { useState } from "react";
import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { PaginationMeta } from "../../../shared/api/types";
import { Ad } from "../model/types";
import { adApiService } from "./api";

/**
 * 펌웨어 검색 및 페이지네이션을 처리하는 커스텀 훅
 * useQuery를 사용하여 펌웨어 목록을 가져오고, 검색어와 페이지 변경을 관리합니다.
 */
export const useAdSearch = () => {
  const [page, setPage] = useState(1);
  const [query, setQuery] = useState("");
  const limit = 8;

  const { data, isLoading, error, isPlaceholderData } = useQuery({
    queryKey: ["ads", query, page, limit],
    queryFn: () => adApiService.getAds(page, limit, query),

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
    ads: (data?.items as Ad[]) ?? [],
    pagination: data?.paginationMeta as PaginationMeta | undefined,
    isLoading: isLoading || isPlaceholderData,
    error: error ? "펌웨어 목록을 불러오는 중 오류가 발생했습니다." : null,
    handleSearch,
    handlePageChange,
  };
};
