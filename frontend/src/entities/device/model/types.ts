/**
 * Data transfer object for device from API
 */
export interface DeviceDto {
  id: string;
  region_id: string;
  region_name: string;
  group_id: string;
  group_name: string;
  is_active: boolean;
  created_at: string;
}

/**
 * Domain model for device
 */
export interface Device {
  id: string;
  regionId: string;
  regionName: string;
  groupId: string;
  groupName: string;
  isActive: boolean;
  createdAt: Date;
}
