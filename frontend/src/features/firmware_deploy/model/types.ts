export type DeployCategory = "region" | "device" | "group";

/**
 * Domain model for firmware deployment request
 */
export interface DeploymentRequest {
  firmwareId: number;
  targetType: DeployCategory;
  targetIds: string[];
}
