/**
 * Generic API response wrapper
 * @template T Type of the data contained in the response
 */
export interface ApiReponse<T> {
  /** The main data payload returned from the API */
  data: T;
  /** Optional metadata  */
  meta?: PaginationMeta;
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
}
