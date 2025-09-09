import { useQuery } from "@tanstack/react-query";
import { deviceApiService } from "./api";

/**
 * useDeviceDetail 훅은 주어진 디바이스 ID에 해당하는 디바이스의 상세 정보를
 * API를 통해 가져오는 React Query 훅입니다.
 * @param deviceId - 상세 정보를 가져올 디바이스의 ID
 * @returns React Query의 쿼리 결과 객체
 */
export const useDeviceDetail = (deviceId: number) => {
  return useQuery({
    queryKey: ["device", deviceId],
    queryFn: () => deviceApiService.getDeviceDetail(deviceId),
  });
};

