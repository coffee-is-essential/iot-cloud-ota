/**
 * 배포 타입
 */
export type DeployCategory = "region" | "group" | "device";

/**
 * 배포 유형을 나타내는 열거형입니다.
 * - REGION: 지역 단위 배포
 * - GROUP: 그룹 단위 배포
 * - DEVICE: 개별 기기 단위 배포
 */
export enum DeploymentType {
  REGION = "REGION",
  GROUP = "GROUP",
  DEVICE = "DEVICE",
}
