import { useEffect, useState } from "react";
import { FirmwareDeploymentDetails } from "../model/types";
import { firmwareDeploymentApiService } from "./api";

/**
 * useFirmwareDeploymentDetail 훅의 반환 값 인터페이스
 */
export interface FirmwareDeploymentDetailHookResult {
  deployment: FirmwareDeploymentDetails | null;
  isLoading: boolean;
  error: string | null;
}

/**
 * 특정 ID의 펌웨어 배포 상세 정보를 가져오는 커스텀 React 훅
 *
 * 이 훅은 API 호출을 처리하여 특정 펌웨어 배포의 상세 정보를 가져오고,
 * 로딩 상태를 관리하며, 데이터 가져오기 중 발생할 수 있는 오류를 처리합니다.
 *
 * @param {number | null} id - 가져올 펌웨어 배포의 ID. null인 경우 오류 상태가 설정됩니다.
 * @returns {FirmwareDeploymentDetailHookResult} 펌웨어 배포 데이터, 로딩 상태, 오류 메시지를 포함하는 객체
 */
export const useFirmwareDeploymentDetail = (
  id: number | null,
): FirmwareDeploymentDetailHookResult => {
  const [deployment, setDeployment] =
    useState<FirmwareDeploymentDetails | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDeployment = async () => {
      if (!id) {
        setError("배포 ID가 유효하지 않습니다.");
        setIsLoading(false);
        return;
      }

      try {
        const result =
          await firmwareDeploymentApiService.getFirmwareDeploymentDetails(id);
        if (!result) {
          setError("배포를 찾을 수 없습니다.");
          setIsLoading(false);
          return;
        }
        setDeployment(result);
      } catch (error) {
        setError("배포를 가져오는 중 오류가 발생했습니다.");
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDeployment();
  }, [id]);

  return { deployment, isLoading, error };
};
