import { PaginationMeta, PaginationMetaDto } from "./types";

/**
 * Maps a PaginationMetaDto from the API to a PaginationMeta domain model
 * @param dto - The metadata transfer object from the API
 * @returns {PaginationMeta} The transformed metadata domain model
 */
export const mapPaginationMeta = (dto: PaginationMetaDto): PaginationMeta => {
  return {
    page: dto.page,
    totalCount: dto.total_count,
    limit: dto.limit,
    totalPage: dto.total_page,
  };
};
