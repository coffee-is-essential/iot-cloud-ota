export interface PaginatedApiResponse<T> {
  /** The main data payload returned from the API */
  items: T[];
  /** Optional metadata for pagination */
  paginationMeta: PaginationMeta;
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
