import {
  FirmwareDeployment,
  FirmwareDeploymentDetails,
  FirmwareDeploymentDetailsResponse,
  FirmwareDeploymentDeviceStatus,
  FirmwareDeploymentDeviceStatusResponse,
  FirmwareDeploymentResponse,
} from "./types";

/**
 * DeviceStatus DTO를 내부 모델로 변환합니다.
 */
export const toFirmwareDeploymentDeviceStatus = (
  dto: FirmwareDeploymentDeviceStatusResponse,
): FirmwareDeploymentDeviceStatus => ({
  ...dto,
  lastUpdatedAt: new Date(dto.lastUpdatedAt),
});

/**
 * Deployment DTO를 내부 모델로 변환합니다.
 */
export const toFirmwareDeployment = (
  dto: FirmwareDeploymentResponse,
): FirmwareDeployment => ({
  ...dto,
  startedAt: new Date(dto.startedAt),
  expiredAt: dto.expiredAt ? new Date(dto.expiredAt) : null,
});

/**
 * DeploymentDetails DTO를 내부 모델로 변환합니다.
 */
export const toFirmwareDeploymentDetails = (
  dto: FirmwareDeploymentDetailsResponse,
): FirmwareDeploymentDetails => ({
  ...toFirmwareDeployment(dto),
  devices: dto.devices.map(toFirmwareDeploymentDeviceStatus),
});
