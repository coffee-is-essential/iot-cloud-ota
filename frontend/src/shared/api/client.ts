import axios from "axios";

/**
 * Axios instance configured with base URL from environment variable.
 * This client is used for all API requests in the application.
 *
 * @example
 * import { apiClient } from '@/shared/api/client';
 *
 * // GET request
 * const response = await apiClient.get('/users');
 *
 * // POST request with data
 * const data = await apiClient.post('/users', { name: 'Junwoo' });
 */
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});
