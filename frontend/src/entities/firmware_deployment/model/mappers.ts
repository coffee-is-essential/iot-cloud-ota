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
  timestamp: new Date(dto.timestamp),
});

/**
 * Deployment DTO를 내부 모델로 변환합니다.
 */
export const toFirmwareDeployment = (
  dto: FirmwareDeploymentResponse,
): FirmwareDeployment => ({
  ...dto,
  deployedAt: new Date(dto.deployedAt),
  expiresAt: dto.expiresAt ? new Date(dto.expiresAt) : null,
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
