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
    queryClient.invalidateQueries({ queryKey: ["firmwares"] });
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
