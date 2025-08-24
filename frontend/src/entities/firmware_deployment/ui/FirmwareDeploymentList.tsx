import { Link } from "react-router";
import { FirmwareDeployment } from "../model/types";
import { DeploymentStatusBadge } from "./DeploymentStatusBadge";
import { DeploymentProgressBar } from "./DeploymentProgressBar";
import { JSX } from "react";

/**
 * 개별 배포 카드 컴포넌트
 */
const DeploymentCard = ({ deployment }: { deployment: FirmwareDeployment }) => {
  return (
    <Link
      to={`/firmware/deployment/${deployment.id}`}
      className="block p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
    >
      {/* 헤더: 배포 ID와 상태 */}
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-3">
          <span className="text-sm font-semibold text-neutral-800">
            배포 #{deployment.id}
          </span>
          <DeploymentStatusBadge status={deployment.status} />
        </div>
        <span className="text-sm text-neutral-500">
          {deployment.startedAt.toLocaleString()}
        </span>
      </div>

      {/* 배포 정보와 진행률을 한 줄로 */}
      <div className="flex items-center justify-between mb-2">
        <div className="flex items-center gap-6">
          <div>
            <span className="text-xs text-neutral-500">타입: </span>
            <span className="text-sm text-neutral-800">
              {deployment.target.type}
            </span>
          </div>
          <div>
            <span className="text-xs text-neutral-500">대상: </span>
            <span className="text-sm text-neutral-800">
              {deployment.target.name}
            </span>
          </div>
        </div>

        {/* 상태 요약을 오른쪽에 간단히 */}
        <div className="flex items-center gap-3 text-xs">
          <span className="text-green-600">성공 {deployment.successCount}</span>
          <span className="text-blue-600">
            진행 {deployment.inProgressCount}
          </span>
          <span className="text-red-600">실패 {deployment.failedCount}</span>
        </div>
      </div>

      {/* 진행률 */}
      <div>
        <DeploymentProgressBar
          total={deployment.totalDevices}
          succeded={deployment.successCount}
          failed={deployment.failedCount}
          inProgress={deployment.inProgressCount}
        />
      </div>
    </Link>
  );
};

/**
 * 펌웨어 배포 목록 컴포넌트 Props 인터페이스
 */
export interface FirmwareDeploymentListProps {
  deployments: FirmwareDeployment[];
  isLoading: boolean;
  error: string | null;
}

/**
 * 펌웨어 배포 목록 컴포넌트
 * 배포 ID, 타입, 대상, 시작 시간, 진행률, 상태 등의 정보를 카드 형식으로 표시합니다.
 * @param deployments - 펌웨어 배포 목록
 * @param isLoading - 데이터 로딩 상태
 * @param error - 에러 메시지
 * @returns {JSX.Element} 펌웨어 배포 목록 컴포넌트
 */
export const FirmwareDeploymentList = ({
  deployments,
  isLoading,
  error,
}: FirmwareDeploymentListProps): JSX.Element => {
  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8 text-red-500">{error}</div>;
  }

  if (deployments.length === 0) {
    return (
      <div className="py-8 text-center text-neutral-500">
        배포 이력이 없습니다.
      </div>
    );
  }

  return (
    <div className="grid gap-4">
      {deployments.map((deployment) => (
        <DeploymentCard key={deployment.id} deployment={deployment} />
      ))}
    </div>
  );
};
