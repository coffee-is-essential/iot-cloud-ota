import { useEffect, useState } from "react";
import { AdDetails } from "../model/types";
import { adApiService } from "./api";

/**
 * 광고 세부 정보를 가져오는 커스텀 훅의 결과 타입
 */
export interface AdDetailHookResult {
  adDetail: AdDetails | null;
  isLoading: boolean;
  error: string | null;
}

/**
 * 광고 ID를 기반으로 광고 세부 정보를 가져오는 커스텀 훅
 * @param {number | null} id - 조회할 광고의 고유 ID
 * @returns {AdDetailHookResult} - 광고 세부 정보, 로딩 상태, 오류 메시지를 포함하는 객체
 */
export const useAdDetail = (id: number | null): AdDetailHookResult => {
  const [adDetail, setAdDetail] = useState<AdDetails | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAdDetail = async () => {
      if (!id) {
        setError("광고 ID가 유효하지 않습니다.");
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        const result = await adApiService.getAdDetail(id);
        if (!result) {
          setError("광고를 찾을 수 없습니다.");
          setIsLoading(false);
          return;
        }
        setAdDetail(result);
      } catch (error) {
        setError("광고를 가져오는 중 오류가 발생했습니다.");
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAdDetail();
  }, [id]);

  return { adDetail, isLoading, error };
};
