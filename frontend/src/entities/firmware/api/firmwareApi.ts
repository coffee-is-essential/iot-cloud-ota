import { Firmware, PaginatedFirmware } from "../model/types";
import { apiClient } from "../../../shared/api/client";
import { PaginatedApiResponse } from "../../../shared/api/types";

/**
 * 펌웨어 관련 API 요청을 처리하는 서비스
 * @namespace firmwareApiService
 */
export const firmwareApiService = {
  /**
   * 펌웨어 목록을 조회합니다.
   * @async
   * @param {number} [page=1] - 조회할 페이지 번호 (기본값: 1)
   * @param {number} [limit=10] - 페이지 당 항목 수 (기본값: 10)
   * @param {string} [query] - 검색어 (선택)
   * @returns {Promise<PaginatedFirmware>} - 페이징된 펌웨어 목록을 반환합니다.
   * @example
   * const data = await firmwareApiService.getFirmwares(1, 10, '검색어');
   */
  getFirmwares: async (
    page: number = 1,
    limit: number = 10,
    query?: string,
  ): Promise<PaginatedFirmware> => {
    const { data } = await apiClient.get<PaginatedApiResponse<Firmware>>(
      `/api/firmwares/metadata`,
      {
        params: {
          page: page,
          limit: limit,
          query: query,
        },
      },
    );
    return {
      items: data.items.map((item) => ({
        ...item,
        createdAt: new Date(item.createdAt),
        modifiedAt: new Date(item.modifiedAt),
      })),
      paginationMeta: data.paginationMeta,
    };
  },

  /**
   * 특정 ID의 펌웨어 정보를 조회합니다.
   * @async
   * @param {number} id - 조회할 펌웨어의 고유 ID
   * @returns {Promise<Firmware>} - 해당 펌웨어 정보를 반환합니다.
   * @example
   * const firmware = await firmwareApiService.getFirmware(1);
   */
  getFirmware: async (id: number): Promise<Firmware> => {
    const { data } = await apiClient.get<Firmware>(
      `/api/firmwares/metadata/${id}`,
    );
    return {
      ...data,
      createdAt: new Date(data.createdAt),
      modifiedAt: new Date(data.modifiedAt),
    };
  },

  /**
   * 새로운 펌웨어를 등록합니다.
   * @async
   * @param {string} version - 등록할 펌웨어 버전
   * @param {string} releaseNote - 펌웨어 릴리즈 노트
   * @param {File} file - 업로드할 펌웨어 파일
   * @returns {Promise<boolean>} - 등록 성공 시 true, 실패 시 false를 반환합니다.
   * @throws API 호출 실패 시 에러를 콘솔에 출력합니다.
   * @example
   * // 펌웨어 등록 예시
   * await firmwareApiService.register('1.0', '최초 릴리즈', myFirmwareFile);
   */
  register: async (
    version: string,
    releaseNote: string,
    file: File,
  ): Promise<boolean> => {
    try {
      const formData = new FormData();
      formData.append("version", version);
      formData.append("release_note", releaseNote);
      formData.append("file", file);

      const response = await apiClient.post(
        "/api/firmware/register",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
      );

      return response.status === 200;
    } catch (error) {
      console.error("펌웨어 등록에 실패했습니다:", error);
      return false;
    }
  },
};
