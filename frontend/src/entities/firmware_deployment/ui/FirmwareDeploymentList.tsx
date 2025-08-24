import { Link } from "react-router";
import { FirmwareDeployment } from "../model/types";

/**
 * 배포 상태에 따른 배지 컴포넌트
 * 배포 진행 상태에 따라 다른 색상과 텍스트를 표시합니다.
 * - PENDING: 회색 배경, "대기 중" 텍스트
 * - IN_PROGRESS: 파란색 배경, "진행 중" 텍스트
 * - COMPLETED: 초록색 배경, "완료" 텍스트
 */
const StatusBadge = ({ status }: { status: FirmwareDeployment["status"] }) => {
  const statusStyles = {
    COMPLETED: "bg-green-100 text-green-800",
    IN_PROGRESS: "bg-blue-100 text-blue-800",
    PENDING: "bg-gray-100 text-gray-800",
  };
  const statusText = {
    COMPLETED: "완료",
    IN_PROGRESS: "진행 중",
    PENDING: "대기 중",
  };

  return (
    <span
      className={`px-2 py-1 text-xs font-medium rounded-full whitespace-nowrap ${statusStyles[status]}`}
    >
      {statusText[status]}
    </span>
  );
};

/**
 * 배포 진행률을 시각적으로 표시하는 컴포넌트
 * 성공, 실패, 진행 중인 디바이스 수에 따라 색상으로 구분된 Progress Bar를 렌더링합니다.
 * @param total - 총 디바이스 수
 * @param success - 성공한 디바이스 수
 * @param failed - 실패한 디바이스 수
 * @param inProgress - 진행 중인 디바이스 수
 */
const DeploymentProgressBar = ({
  total,
  success,
  failed,
  inProgress,
}: {
  total: number;
  success: number;
  failed: number;
  inProgress: number;
}) => {
  if (total === 0) {
    return <div className="text-sm text-neutral-500">-</div>;
  }

  // 각 상태의 비율 계산
  const successPercentage = (success / total) * 100;
  const failedPercentage = (failed / total) * 100;
  const inProgressPercentage = (inProgress / total) * 100;

  return (
    <div className="flex items-center gap-3">
      <div className="w-3/4 bg-gray-200 rounded-full h-3 flex overflow-hidden">
        {/* 성공 (초록색) */}
        <div
          className="h-full bg-green-500"
          style={{ width: `${successPercentage}%` }}
          title={`성공: ${success}개`}
        />
        {/* 실패 (빨간색) */}
        <div
          className="h-full bg-red-500"
          style={{ width: `${failedPercentage}%` }}
          title={`실패: ${failed}개`}
        />
        {/* 진행 중 (파란색) */}
        <div
          className="h-full bg-blue-400"
          style={{ width: `${inProgressPercentage}%` }}
          title={`진행 중: ${inProgress}개`}
        />
      </div>
      {/* 진행률 텍스트 */}
      <span className="text-sm font-medium text-neutral-600 min-w-max">
        {success} / {total}
      </span>
    </div>
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
 * 배포 ID, 타입, 대상, 시작 시간, 진행률, 상태 등의 정보를 테이블 형식으로 표시합니다.
 * @param deployments - 펌웨어 배포 목록
 * @param isLoading - 데이터 로딩 상태
 * @param error - 에러 메시지
 * @returns JSX.Element
 */
export const FirmwareDeploymentList = ({
  deployments,
  isLoading,
  error,
}: FirmwareDeploymentListProps) => {
  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }
  if (error) {
    return <div className="flex justify-center py-8 text-red-500">{error}</div>;
  }

  return (
    <div className="border border-gray-200 rounded-md">
      <div className="w-full table border-collapse">
        {/* --- 테이블 헤더 --- */}
        <div className="text-sm font-medium text-neutral-600 bg-gray-50 table-header-group">
          <div className="table-row">
            <div className="p-3 table-cell">배포 ID</div>
            <div className="p-3 table-cell">배포 타입</div>
            <div className="p-3 table-cell">대상</div>
            <div className="p-3 table-cell">시작 시간</div>
            <div className="p-3 table-cell w-1/3">진행률</div>
            <div className="p-3 text-center table-cell">상태</div>
          </div>
        </div>

        {/* --- 테이블 본문 --- */}
        <div className="table-row-group">
          {deployments.length === 0 ? (
            <div className="table-row">
              <div className="p-8 text-center text-neutral-500">
                배포 이력이 없습니다.
              </div>
            </div>
          ) : (
            deployments.map((deployment) => (
              <Link
                to={`/firmware/deployment/${deployment.id}`}
                key={deployment.id}
                className="table-row transition-colors border-b border-gray-100 last:border-b-0 hover:bg-gray-50"
              >
                <div className="p-4 text-sm font-semibold text-neutral-800 table-cell align-middle">
                  #{deployment.id}
                </div>
                <div className="p-4 text-sm text-neutral-600 table-cell align-middle">
                  {deployment.target.type}
                </div>
                <div className="p-4 text-sm text-neutral-600 table-cell align-middle">
                  {deployment.target.name}
                </div>
                <div className="p-4 text-sm text-neutral-600 table-cell align-middle whitespace-nowrap">
                  {deployment.startedAt.toLocaleString()}
                </div>
                <div className="p-4 table-cell align-middle">
                  <DeploymentProgressBar
                    total={deployment.totalDevices}
                    success={deployment.successCount}
                    failed={deployment.failedCount}
                    inProgress={deployment.inProgressCount}
                  />
                </div>
                <div className="p-4 text-center table-cell align-middle">
                  <StatusBadge status={deployment.status} />
                </div>
              </Link>
            ))
          )}
        </div>
      </div>
    </div>
  );
};
