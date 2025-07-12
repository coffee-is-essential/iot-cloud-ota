import { PaginationMeta } from "../../../shared/api/types";

/**
 * Data transfer object for firmware from API
 */
export interface FirmwareDto {
  id: number;
  version: string;
  release_note: string;
  created_at: string;
  updated_at: string;
  device_count: number;
}

/**
 * Domain model for firmware
 */
export interface Firmware {
  id: number;
  version: string;
  fileName: string;
  releaseNote: string;
  createdAt: Date;
  modifiedAt: Date;
}

export interface PaginatedFirmware {
  items: Firmware[];
  paginationMeta: PaginationMeta;
}
