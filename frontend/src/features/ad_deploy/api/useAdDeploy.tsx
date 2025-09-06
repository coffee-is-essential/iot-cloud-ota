import { useMutation } from "@tanstack/react-query";
import { AdDeployRequest, deployAds } from "./api";

/**
 * useAdDeploy 훅은 광고 배포 요청을 처리하는 뮤테이션 훅입니다.
 * @returns {UseMutationResult} 광고 배포 요청을 처리하는 뮤테이션 객체
 */
export const useAdDeploy = () => {
  return useMutation({
    mutationFn: (request: AdDeployRequest) => deployAds(request),
  });
};
