import { Device, DeviceDto } from "./types";

/**
 * Maps a DeviceDto object to a Device object.
 * @param {DeviceDto} dto - The data transfer object from the API
 * @return {Device} The transformed device domain model
 */
export const mapDeviceDto = (dto: DeviceDto): Device => {
  return {
    id: dto.id,
    regionId: dto.region_id,
    regionName: dto.region_name,
    groupId: dto.group_id,
    groupName: dto.group_name,
    isActive: dto.is_active,
    createdAt: new Date(dto.created_at),
  };
};
