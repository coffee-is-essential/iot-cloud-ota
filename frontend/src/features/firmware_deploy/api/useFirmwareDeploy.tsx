import { useMutation, useQueryClient } from "@tanstack/react-query";
import { requestFirmwareDeploy } from "./api";
import { DeploymentType } from "../model/types";

// 변수 객체의 타입을 정의합니다.
interface DeployFirmwareVariables {
  firmwareId: number;
  deploymentType: DeploymentType;
  regions: any[];
  groups: any[];
  devices: any[];
}

/**
 * 펌웨어 배포 프로세스를 담당하는 커스텀 훅
 * @description useMutation을 사용하여 펌웨어 배포를 처리하고,
 * 성공 시 펌웨어 배포 목록 쿼리를 무효화하여 자동 리프레시를 유발합니다.
 */
export const useFirmwareDeploy = () => {
  const queryClient = useQueryClient();

  const deployFirmware = async ({
    firmwareId,
    deploymentType,
    regions,
    groups,
    devices,
  }: DeployFirmwareVariables) => {
    await requestFirmwareDeploy(
      firmwareId,
      deploymentType,
      regions,
      groups,
      devices,
    );
  };

  return useMutation<void, Error, DeployFirmwareVariables>({
    mutationFn: deployFirmware,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["firmware-deployments"] });
    },
    onError: (error) => {
      console.error("펌웨어 배포에 실패했습니다:", error);
    },
  });
};
