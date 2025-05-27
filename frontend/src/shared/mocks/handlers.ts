import { http, HttpResponse } from "msw";

const firmwares = [
  {
    id: 1,
    version: "24.04.01",
    release_note: "광고가 표시되지 않는 버그를 수정했습니다.",
    created_at: "2025-03-01T10:00:00",
    updated_at: "2025-03-01T11:00:00",
    device_count: 1023,
  },
  {
    id: 2,
    version: "24.04.02",
    release_note: "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다",
    created_at: "2025-03-02T11:00:00",
    updated_at: "2025-03-02T12:00:00",
    device_count: 547,
  },
  {
    id: 3,
    version: "24.04.03",
    release_note: "연두해요 연두 광고를 업로드하였습니다.",
    created_at: "2025-03-03T12:00:00",
    updated_at: "2025-03-02T13:00:00",
    device_count: 219,
  },
  {
    id: 4,
    version: "24.04.04",
    release_note: "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.",
    created_at: "2025-03-04T13:00:00",
    updated_at: "2025-03-04T14:00:00",
    device_count: 1321,
  },
  {
    id: 5,
    version: "24.04.05",
    release_note: "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다",
    created_at: "2025-03-05T14:00:00",
    updated_at: "2025-03-05T15:00:00",
    device_count: 547,
  },
  {
    id: 6,
    version: "24.04.06",
    release_note: "연두해요 연두 광고를 업로드하였습니다.",
    created_at: "2025-03-06T15:00:00",
    updated_at: "2025-03-06T16:00:00",
    device_count: 219,
  },
  {
    id: 7,
    version: "24.04.07",
    release_note: "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.",
    created_at: "2025-03-07T16:00:00",
    updated_at: "2025-03-07T17:00:00",
    device_count: 1321,
  },
  {
    id: 8,
    version: "24.04.08",
    release_note: "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다",
    created_at: "2025-03-08T17:00:00",
    updated_at: "2025-03-08T18:00:00",
    device_count: 547,
  },
  {
    id: 9,
    version: "24.04.09",
    release_note: "연두해요 연두 광고를 업로드하였습니다.",
    created_at: "2025-03-09T18:00:00",
    updated_at: "2025-03-09T19:00:00",
    device_count: 219,
  },
  {
    id: 10,
    version: "24.04.10",
    release_note: "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.",
    created_at: "2025-03-10T19:00:00",
    updated_at: "2025-03-10T20:00:00",
    device_count: 1321,
  },
  {
    id: 11,
    version: "24.04.11",
    release_note: "원두 선택 버튼이 클릭되지 않는 버그를 수정했습니다",
    created_at: "2025-03-11T20:00:00",
    updated_at: "2025-03-11T21:00:00",
    device_count: 547,
  },
  {
    id: 12,
    version: "24.04.12",
    release_note: "연두해요 연두 광고를 업로드하였습니다.",
    created_at: "2025-03-12T21:00:00",
    updated_at: "2025-03-12T22:00:00",
    device_count: 219,
  },
  {
    id: 13,
    version: "24.04.13",
    release_note: "동원참치 캔 광고가 표시되지 않는 버그를 수정하였습니다.",
    created_at: "2025-03-13T22:00:00",
    updated_at: "2025-03-13T23:00:00",
    device_count: 1321,
  },
];

const regions = [
  {
    id: "us-west-1",
    name: "미국 북부 캘리포니아",
    device_count: 392,
    created_at: "2025-03-01T10:00:00",
  },
  {
    id: "us-east-1",
    name: "미국 북부 버지니아",
    device_count: 243,
    created_at: "2025-03-02T11:00:00",
  },
  {
    id: "ap-south-1",
    name: "인도 뭄바이",
    device_count: 102,
    created_at: "2025-03-03T12:00:00",
  },
  {
    id: "ap-northeast-2",
    name: "서울",
    device_count: 523,
    created_at: "2025-03-04T13:00:00",
  },
  {
    id: "ap-northeast-3",
    name: "오사카",
    device_count: 291,
    created_at: "2025-03-05T14:00:00",
  },
  {
    id: "ap-west-1",
    name: "인도",
    device_count: 230,
    created_at: "2025-03-06T15:00:00",
  },
  {
    id: "ap-southeast-3",
    name: "자카르타",
    device_count: 193,
    created_at: "2025-03-07T16:00:00",
  },
];

const groups = [
  {
    id: "dmeowk-203",
    name: "서울 모수",
    device_count: 4,
    created_at: "2025-03-01T10:00:00",
  },
  {
    id: "wmeotc-391",
    name: "부산 스타벅스 광안리점",
    device_count: 2,
    created_at: "2025-03-02T11:00:00",
  },
  {
    id: "wjrpvo-100",
    name: "오꾸닭 신림점",
    device_count: 1,
    created_at: "2025-03-03T12:00:00",
  },
  {
    id: "woeprz-009",
    name: "네이버 판교 본사",
    device_count: 8,
    created_at: "2025-03-04T13:00:00",
  },
];

const devices = [
  {
    id: "bartooler-001",
    region_id: "ap-northeast-2",
    region_name: "서울",
    group_id: "dmeowk-203",
    group: "서울 모수",
    is_active: true,
    created_at: "2025-03-01T10:00:00",
  },
  {
    id: "bartooler-002",
    region_id: "ap-northeast-2",
    region_name: "서울",
    group_id: "wmeotc-391",
    group: "부산 스타벅스 광안리점",
    is_active: true,
    created_at: "2025-03-02T11:00:00",
  },
  {
    id: "bartooler-003",
    region_id: "ap-northeast-2",
    region_name: "서울",
    group_id: "dmeowk-203",
    group: "서울 모수",
    is_active: false,
    created_at: "2025-03-03T12:00:00",
  },
  {
    id: "bartooler-004",
    region_id: "ap-northeast-2",
    region_name: "서울",
    group_id: "woeprz-009",
    group: "네이버 판교 본사",
    is_active: true,
    created_at: "2025-03-04T13:00:00",
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

    const total_page = Math.ceil(firmwares.length / limit);
    const total_count = firmwares.length;
    const meta = {
      page,
      total_count,
      limit,
      total_page,
    };

    return HttpResponse.json({
      data: paginatedData,
      pagination: meta,
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
      firmware.version.includes(query),
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
      pagination: meta,
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

  http.get("/api/regions", () => {
    return HttpResponse.json({
      data: regions,
    });
  }),

  http.get("/api/groups", () => {
    return HttpResponse.json({
      data: groups,
    });
  }),

  http.get("/api/devices", () => {
    return HttpResponse.json({
      data: devices,
    });
  }),
];
