import { Firmware, FirmwareDto, Meta, MetaDto } from "./types";

/**
 * Maps a FirmwareDto from the API to a Firmware domain model
 * @param {FirmwareDto} dto  - The data transfer object from the API
 * @returns {Firmware} The transformed firmware domain model
 * @returns
 */
export const mapFirmwareDto = (dto: FirmwareDto): Firmware => {
  return {
    id: dto.id,
    version: dto.version,
    releaseNote: dto.release_note,
    createdAt: new Date(dto.created_at),
    updatedAt: new Date(dto.updated_at),
    deviceCount: dto.device_count,
  };
};

/**
 * Maps a MetaDto from the API to a Meta domain model
 * @param {MetaDto} dto - The metadata transfer object from the API
 * @returns {Meta} The transformed metadata domain model
 */
export const mapMeta = (dto: MetaDto): Meta => {
  return {
    page: dto.page,
    totalCount: dto.total_count,
  };
};
