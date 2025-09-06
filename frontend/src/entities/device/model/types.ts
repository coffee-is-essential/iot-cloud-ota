/**
 * 기기(Device) 정보를 나타내는 인터페이스입니다.
 */
export interface Device {
  deviceId: string;
  deviceName: string;
  regionName: string;
  groupName: string;
  isActive?: boolean;
}
