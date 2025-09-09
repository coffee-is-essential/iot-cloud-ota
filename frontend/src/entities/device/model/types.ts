import { PaginationMeta } from "../../../shared/api/types";

/**
 * 기기(Device) 정보를 나타내는 인터페이스입니다.
 */
export interface Device {
  deviceId: string;
  deviceName: string;
  regionName: string;
  groupName: string;
  isActive?: boolean;
  lastActiveAt?: Date;
}

/**
 * 페이지네이션된 기기(Device) 목록을 나타내는 인터페이스입니다.
 */
export interface PaginatedDevice {
  items: Device[];
  paginationMeta: PaginationMeta;
}

/**
 * 기기 상세 정보 응답 타입을 정의하는 인터페이스입니다.
 */
export interface DeviceDetailResponse {
  deviceId: number;
  deviceName: string;
  createdAt: string;
  modifiedAt: string;
  lastActiveAt: string | null;
  region: {
    id: number;
    code: string;
    name: string;
  };
  group: {
    id: number;
    code: string;
    name: string;
  };
  firmware: {
    id: number;
    version: string;
  };
  advertisements: {
    id: number;
    title: string;
    deployedAt: string;
    originalSignedUrl: string;
  }[];
}

/**
 * 기기 상세 정보를 나타내는 인터페이스입니다.
 */
export interface DeviceDetail {
  deviceId: number;
  deviceName: string;
  createdAt: Date;
  modifiedAt: Date;
  lastActiveAt: Date | null;
  region: {
    id: number;
    code: string;
    name: string;
  };
  group: {
    id: number;
    code: string;
    name: string;
  };
  firmware: {
    id: number;
    version: string;
  };
  advertisements: {
    id: number;
    title: string;
    deployedAt: Date;
    originalSignedUrl: string;
  }[];
}
