import { PaginationMeta } from "../../../shared/api/types";
import { Firmware } from "../../firmware/model/types";

/**
 * 펌웨어 배포 대상 인터페이스
 */
export interface FirmwareDeploymentTarget {
  type: "REGION" | "GROUP" | "DEVICE";
  id: number;
  name: string;
}

// --- DTO: API로부터 받는 순수 데이터 타입 ---

/**
 * 개별 디바이스 상태 응답 (DTO)
 */
export interface FirmwareDeploymentDeviceStatusResponse {
  id: number;
  status: "SUCCESS" | "IN_PROGRESS" | "FAILED";
  progress: number;
  lastUpdatedAt: string;
}

/**
 * 펌웨어 배포 정보 응답 (DTO)
 */
export interface FirmwareDeploymentResponse {
  id: number;
  firmware: Firmware;
  target: FirmwareDeploymentTarget;
  totalDevices: number;
  successCount: number;
  inProgressCount: number;
  failedCount: number;
  status: "PENDING" | "IN_PROGRESS" | "COMPLETED";
  startedAt: string;
  expiredAt: string | null;
}

/**
 * 펌웨어 배포 상세 정보 응답 (DTO)
 */
export interface FirmwareDeploymentDetailsResponse
  extends FirmwareDeploymentResponse {
  devices: FirmwareDeploymentDeviceStatusResponse[];
}

// --- 앱 내부 사용 모델: DTO의 날짜(string)를 Date 객체로 변환한 타입 ---

/**
 * 개별 디바이스 상태 (앱 내부 모델)
 * DTO의 날짜 문자열을 Date 객체로 변환
 */
export type FirmwareDeploymentDeviceStatus = {
  id: number;
  status: "SUCCESS" | "IN_PROGRESS" | "FAILED" | "TIMEOUT";
  progress: number;
  lastUpdatedAt: Date;
};

/**
 * 펌웨어 배포 정보 (앱 내부 모델)
 * DTO의 날짜 문자열을 Date 객체로 변환
 */
export type FirmwareDeployment = {
  id: number;
  firmware: Firmware;
  target: FirmwareDeploymentTarget;
  totalDevices: number;
  successCount: number;
  inProgressCount: number;
  failedCount: number;
  status: "PENDING" | "IN_PROGRESS" | "COMPLETED";
  startedAt: Date;
  expiredAt: Date | null;
};

/**
 * 펌웨어 배포 상세 정보 (앱 내부 모델)
 */
export type FirmwareDeploymentDetails = FirmwareDeployment & {
  devices: FirmwareDeploymentDeviceStatus[];
};

/**
 * 페이징된 펌웨어 배포 목록 (앱 내부 모델)
 */
export interface PaginatedFirmwareDeployments {
  items: FirmwareDeployment[];
  paginationMeta: PaginationMeta;
}
