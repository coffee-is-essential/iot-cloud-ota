import { http, HttpResponse } from "msw";

const firmwares = [
  {
    id: 1,
    version: "24.04.01",
    release_note: "광고가 표시되지 않는 버그를 수정했습니다.",
    created_at: "2025-03-01T10:00:00",
    device_count: 1023,
  },
  {
    id: 2,
    version: "24.04.02",
    release_note: "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다",
    created_at: "2025-03-02T11:00:00",
    device_count: 547,
  },
  {
    id: 3,
    version: "24.04.03",
    release_note: "연두해요 연두 광고를 업로드하였습니다.",
    created_at: "2025-03-03T12:00:00",
    device_count: 219,
  },
  {
    id: 4,
    version: "24.04.04",
    release_note: "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.",
    created_at: "2025-03-04T13:00:00",
    device_count: 1321,
  },
];

export const handlers = [
  http.get("/api/firmware", ({ request }) => {
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get("page") || "1");
    const limit = parseInt(url.searchParams.get("limit") || "10");

    const startIdx = (page - 1) * limit;
    const endIdx = page * limit;
    const paginatedData = firmwares.slice(startIdx, endIdx);

    return HttpResponse.json({
      data: paginatedData,
      meta: {
        page,
        total_count: firmwares.length,
        limit,
      },
    });
  }),

  http.get("/api/firmware/search", ({ request }) => {
    const url = new URL(request.url);
    const query = url.searchParams.get("query") || "";
    const page = parseInt(url.searchParams.get("page") || "1");
    const limit = parseInt(url.searchParams.get("limit") || "10");
    const startIdx = (page - 1) * limit;
    const endIdx = page * limit;

    const filteredFirmwares = firmwares.filter((firmware) =>
      firmware.version.includes(query)
    );

    const paginatedData = filteredFirmwares.slice(startIdx, endIdx);
    const total_count = filteredFirmwares.length;
    const total_page = Math.ceil(total_count / limit);
    const meta = {
      page,
      total_count,
      limit,
      total_page,
    };
    return HttpResponse.json({
      data: paginatedData,
      meta,
    });
  }),

  http.get("/api/firmware/:id", ({ params }) => {
    const id = parseInt(params.id as string);

    const firmware = firmwares.find((fw) => fw.id === id);

    if (!firmware) {
      return new HttpResponse(null, {
        status: 404,
        statusText: "Firmware not found",
      });
    }

    return HttpResponse.json({
      data: firmware,
    });
  }),
];
