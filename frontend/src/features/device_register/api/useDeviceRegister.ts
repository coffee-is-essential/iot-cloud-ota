import { useMutation } from "@tanstack/react-query";
import { deviceRegisterApiService } from "./api";
import { DeviceRegisterFormData } from "../model/types";

/**
 * 디바이스 등록을 처리하는 커스텀 훅
 * @description useMutation을 사용하여 디바이스 등록을 처리합니다.
 */
export const useDeviceRegister = () => {
  return useMutation({
    mutationFn: ({ regionId, groupId }: DeviceRegisterFormData) =>
      deviceRegisterApiService.registerDevice(regionId, groupId),
  });
};
