import { useParams } from "react-router";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { useFirmwareDeploymentDetail } from "../entities/firmware_deployment/api/useFirmwareDeploymentDetail";
import { LabeledValue } from "../shared/ui/LabeledValue";
import {
  FirmwareDeploymentDetails,
  FirmwareDeploymentDeviceStatus,
} from "../entities/firmware_deployment/model/types";
import { DeploymentStatusBadge } from "../entities/firmware_deployment/ui/DeploymentStatusBadge";
import { DeploymentProgressBar } from "../entities/firmware_deployment/ui/DeploymentProgressBar";
import { DeploymentDeviceStatusBadge } from "../entities/firmware_deployment/ui/DeploymentDeviceStatusBadge";

/**
 * 펌웨어 배포 세부 정보 컴포넌트
 */
const DeploymentInfo = ({
  deployment,
}: {
  deployment: FirmwareDeploymentDetails;
}) => {
  return (
    <div className="flex justify-between">
      <div className="flex flex-col gap-6">
        <LabeledValue
          label="배포 ID"
          value={deployment.id.toString()}
          size="sm"
        />
        <LabeledValue
          label="펌웨어 버전"
          value={deployment.firmware.version}
          size="sm"
        />
        <LabeledValue
          label="시작 일시"
          value={deployment.deployedAt.toLocaleString()}
          size="sm"
        />
        <LabeledValue
          label="만료 일시"
          value={deployment.expiresAt?.toLocaleString() ?? "없음"}
          size="sm"
        />
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium text-neutral-700">상태</span>
          <DeploymentStatusBadge status={deployment.status} />
        </div>
      </div>

      <div className="flex flex-col w-2/5 gap-4">
        <div className="p-4 bg-gray-50 rounded-lg">
          <div className="mb-3 text-sm font-medium text-neutral-700">
            전체 진행률
          </div>
          <DeploymentProgressBar
            total={deployment.totalCount}
            succeeded={deployment.successCount}
            failed={deployment.failedCount}
            inProgress={deployment.inProgressCount}
          />
          <div className="text-xs text-neutral-500">
            {deployment.successCount} / {deployment.totalCount} 디바이스 완료
          </div>
        </div>

        <div className="grid grid-cols-3 gap-3">
          <div className="p-3 text-center bg-green-50 rounded-lg">
            <div className="text-lg font-bold text-green-600">
              {deployment.successCount}
            </div>
            <div className="text-xs text-green-600">성공</div>
          </div>
          <div className="p-3 text-center bg-blue-50 rounded-lg">
            <div className="text-lg font-bold text-blue-600">
              {deployment.inProgressCount}
            </div>
            <div className="text-xs text-blue-600">진행중</div>
          </div>
          <div className="p-3 text-center bg-red-50 rounded-lg">
            <div className="text-lg font-bold text-red-600">
              {deployment.failedCount}
            </div>
            <div className="text-xs text-red-600">실패</div>
          </div>
        </div>
      </div>
    </div>
  );
};

/**
 * 디바이스 상태 목록 컴포넌트
 */
const DeviceStatusList = ({
  devices,
}: {
  devices: FirmwareDeploymentDeviceStatus[];
}) => {
  return (
    <div className="space-y-4">
      {devices.length === 0 ? (
        <div className="py-8 text-center text-gray-500">
          배포 대상 디바이스가 없습니다.
        </div>
      ) : (
        <div className="grid gap-3">
          {devices.map((device) => (
            <div
              key={device.id}
              className="p-4 border border-gray-200 rounded-lg"
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-3">
                  <span className="font-medium text-neutral-800">
                    디바이스 ID: {device.id}
                  </span>
                  <DeploymentDeviceStatusBadge status={device.status} />
                </div>
                <span className="text-sm text-neutral-500">
                  {device.timestamp.toLocaleString()}
                </span>
              </div>

              {device.status === "IN_PROGRESS" && (
                <div className="flex items-center gap-3">
                  <div className="flex-1 bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${device.progress}%` }}
                    ></div>
                  </div>
                  <span className="text-sm font-medium text-neutral-600">
                    {device.progress}%
                  </span>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

/**
 * 펌웨어 배포 세부 정보 페이지 컴포넌트
 * 배포 ID를 URL 파라미터로 받아 해당 배포의 세부 정보를 표시합니다.
 */
export const FirmwareDeploymentDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const parsedId = id ? parseInt(id) : null;

  if (parsedId === null || isNaN(parsedId)) {
    return <div>배포 ID가 제공되지 않았습니다.</div>;
  }

  const { deployment, isLoading, error } =
    useFirmwareDeploymentDetail(parsedId);

  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8 text-red-500">{error}</div>;
  }

  if (!deployment) {
    return (
      <div className="flex justify-center py-8">
        배포 정보를 찾을 수 없습니다.
      </div>
    );
  }

  return (
    <div className="flex flex-col">
      <div className="mb-8">
        <TitleTile
          title="펌웨어 관리"
          description="펌웨어 업로드 및 원격 업데이트"
        />
      </div>

      <div className="mb-6">
        <MainTile title="펌웨어 배포 세부 정보">
          <DeploymentInfo deployment={deployment} />
        </MainTile>
      </div>

      <div>
        <MainTile title="디바이스별 진행 상황">
          <DeviceStatusList devices={deployment.devices} />
        </MainTile>
      </div>
    </div>
  );
};
