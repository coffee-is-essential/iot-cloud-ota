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
 * Metadata for pagination from API
 */
export interface MetaDto {
  page: number;
  total_count: number;
  limit: number;
}

/**
 * Domain model for firmware
 */
export interface Firmware {
  id: number;
  version: string;
  releaseNote: string;
  createdAt: Date;
  updatedAt: Date;
  deviceCount: number;
}

/**
 * Domain model for pagination metadata
 */
export interface Meta {
  page: number;
  totalCount: number;
}
