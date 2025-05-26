/**
 * Data transfer object for region from API
 */
export interface RegionDto {
  id: string;
  name: string;
  device_count: number;
  created_at: string;
}

/**
 * Domain model for Region
 */
export interface Region {
  id: string;
  name: string;
  deviceCount: number;
  createdAt: Date;
}
