import { Group, GroupDto } from "./types";

/**
 * Maps a GroupDto object to a Group object.
 * @param {GroupDto} dto - The data transfer object from the API
 * @return {Group} The transformed group domain model
 */
export const mapGroupDto = (dto: GroupDto): Group => {
  return {
    id: dto.id,
    name: dto.name,
    deviceCount: dto.device_count,
    createdAt: new Date(dto.created_at),
  };
};
