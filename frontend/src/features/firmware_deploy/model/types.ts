export type DeployCategory = "region" | "device" | "group";

export interface DeploymentRequest {
  firmwareId: number;
  targetType: DeployCategory;
  targetIds: string[];
}
