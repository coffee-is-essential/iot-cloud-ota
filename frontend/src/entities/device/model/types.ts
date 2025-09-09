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
