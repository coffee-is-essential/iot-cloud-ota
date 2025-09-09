import { useParams } from "react-router";
import { useDeviceDetail } from "../api/useDeviceDetail";
import { MainTile } from "../../../widgets/layout/ui/MainTile";
import { TitleTile } from "../../../widgets/layout/ui/TitleTile";

/**
 * DeviceDetailView 컴포넌트는 특정 디바이스의 상세 정보를 보여줍니다.
 * 디바이스의 ID를 URL 파라미터에서 가져와 API를 통해 데이터를 로드하고,
 * 디바이스의 상태, 지역, 그룹, 펌웨어 버전, 생성일 및 수정일 등의 정보를 표시합니다.
 * 또한, 디바이스가 표시하고 있는 광고의 썸네일과 제목도 함께 보여줍니다.
 * 디바이스가 마지막으로 활성화된 시간이 5분 이내인 경우 "활성" 상태로 표시하며,
 * 그렇지 않은 경우 "비활성" 상태로 표시합니다.
 */
export const DeviceDetailView = () => {
  const { deviceId } = useParams<{ deviceId: string }>();
  const parsedId = deviceId ? parseInt(deviceId) : null;

  if (parsedId === null || isNaN(parsedId)) {
    return <div>유효하지 않은 디바이스 ID입니다.</div>;
  }

  const { data: device, isLoading, error } = useDeviceDetail(parsedId);

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error.message}</div>;
  }

  if (!device) {
    return <div>Device not found</div>;
  }

  const isActive =
    device.lastActiveAt &&
    new Date().getTime() - new Date(device.lastActiveAt).getTime() <
      5 * 60 * 1000; // 5 minutes

  return (
    <div className="flex flex-col gap-4">
      <div className="col-span-2">
        <TitleTile
          title={`디바이스 상세 정보`}
          description={`디바이스의 상세 정보 및 광고 내역`}
        />
      </div>
      <MainTile title="디바이스 정보">
        <div>
          <div className="flex gap-8">
            <dl className="divide-y divide-gray-200 w-1/2">
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">
                  디바이스 이름
                </dt>
                <dd className="mt-1 text-lg font-semibold text-gray-900 sm:col-span-2 sm:mt-0">
                  {device.deviceName}
                </dd>
              </div>
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">
                  디바이스 ID
                </dt>
                <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">
                  {device.deviceId}
                </dd>
              </div>
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">상태</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">
                  <div className="flex items-center">
                    <span
                      className={`h-2.5 w-2.5 rounded-full ${
                        isActive ? "bg-green-500" : "bg-gray-400"
                      } mr-2`}
                    ></span>
                    <span>{isActive ? "활성" : "비활성"}</span>
                    <span className="ml-4 text-xs text-gray-500">
                      (마지막 활성:{" "}
                      {device.lastActiveAt
                        ? device.lastActiveAt.toLocaleString()
                        : "N/A"}
                      )
                    </span>
                  </div>
                </dd>
              </div>
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">
                  지역 / 그룹
                </dt>
                <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">
                  {device.region.name} / {device.group.name}
                </dd>
              </div>
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">
                  펌웨어 버전
                </dt>
                <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">
                  {device.firmware?.version ?? "알 수 없음"}
                </dd>
              </div>
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">생성일</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">
                  {device.createdAt.toLocaleString()}
                </dd>
              </div>
              <div className="py-4 sm:grid sm:grid-cols-3 sm:gap-4">
                <dt className="text-sm font-medium text-gray-500">수정일</dt>
                <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">
                  {device.modifiedAt.toLocaleString()}
                </dd>
              </div>
            </dl>
            <div className="border border-gray-200 rounded-md text-center p-4 flex-1">
              메트릭 차트 자리
            </div>
          </div>
        </div>
      </MainTile>

      <MainTile title="표시되고 있는 광고">
        <div className="flex justify-between space-x-8">
          {Array.from({ length: 3 }).map((_, index) => {
            const ad = device.advertisements[index];
            return (
              <div
                key={ad ? `ad-${ad.id}` : `placeholder-${index}`}
                className="w-full"
              >
                <div className="w-full aspect-[4/3] rounded-md shadow-md">
                  {ad ? (
                    <img
                      src={ad.originalSignedUrl}
                      alt={ad.title}
                      className="w-full h-full object-cover rounded-md"
                    />
                  ) : (
                    <div className="w-full h-full bg-white border-2 border-dashed border-gray-300 rounded-md" />
                  )}
                </div>
                <div className="text-sm mt-2 text-center text-gray-600 truncate">
                  {ad ? ad.title : "비어있음"}
                </div>
              </div>
            );
          })}
        </div>
      </MainTile>
    </div>
  );
};
