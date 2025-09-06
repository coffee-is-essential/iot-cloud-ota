import { PaginationMeta } from "../../../shared/api/types";
import { Device } from "../../device/model/types";

/**
 * 광고 정보를 나타내는 인터페이스
 */
export interface Ad {
  id: number;
  title: string;
  description: string;
  originalSignedUrl: string;
  createdAt: Date;
  modifiedAt: Date;
}

/**
 * 페이징된 광고 목록 응답 인터페이스
 */
export interface PaginatedAds {
  items: Ad[];
  paginationMeta: PaginationMeta;
}

/**
 * 광고 상세 정보와 관련된 기기 목록을 포함하는 인터페이스
 */
export interface AdDetails {
  adsMetadata: Ad;
  devices: Device[];
}
