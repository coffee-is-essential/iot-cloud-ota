import { Region, RegionDto } from "./types";

/**
 * Maps a RegionDto object to a Region object.
 * @param {RegionDto} dto - The data transfer object from the API
 * @return {Region} The transformed region domain model
 */
export const mapRegionDto = (dto: RegionDto): Region => {
  return {
    id: dto.id,
    name: dto.name,
    deviceCount: dto.device_count,
    createdAt: new Date(dto.created_at),
  };
};
