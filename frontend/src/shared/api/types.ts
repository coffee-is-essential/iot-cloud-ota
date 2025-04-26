/**
 * Generic API response wrapper
 * @template T Type of the data contained in the response
 */
export interface ApiResponse<T> {
  /** The main data payload returned from the API */
  data: T;
}

export interface PaginatedApiResponse<T> {
  /** The main data payload returned from the API */
  data: T[];
  /** Optional metadata for pagination */
  pagination: PaginationMetaDto;
}

export interface PaginationMetaDto {
  /** Current page number */
  page: number;
  /** Total number of items across all pages */
  total_count: number;
  /** Maximum number of items per page */
  limit: number;
  /** Total number of pages available */
  total_page: number;
}

/**
 * Metadata for paginated API responses
 */
export interface PaginationMeta {
  /** Current page number */
  page: number;
  /** Total number of items across all pages */
  totalCount: number;
  /** Maximum number of items per page */
  limit: number;
  /** Total number of pages available */
  totalPage: number;
}
