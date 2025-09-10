/**
 * 기기 등록 폼 데이터 인터페이스
 */
export interface DeviceRegisterFormData {
  regionId: number;
  groupId: number;
}

/**
 * 기기 등록 응답 데이터 인터페이스
 */
export interface DeviceRegisterResponse {
  code: string;
  expiresAt: string;
}

/**
 * 기기 등록 정보 인터페이스
 */
export interface DeviceRegisterInfo {
  code: string;
  expiresAt: Date;
}
