/**
 * Data transfer object for group from API
 */
export interface GroupDto {
  id: string;
  name: string;
  device_count: number;
  created_at: string;
}

/**
 * Domain model for group
 */
export interface Group {
  id: string;
  name: string;
  deviceCount: number;
  createdAt: Date;
}
