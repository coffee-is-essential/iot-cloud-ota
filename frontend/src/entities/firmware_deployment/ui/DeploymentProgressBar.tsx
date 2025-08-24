import { JSX } from "react";

/**
 * 배포 진행 상황을 바 형태로 시각화하는 컴포넌트의 Props 인터페이스
 */
export interface ProgressBarProps {
  total: number;
  succeded: number;
  failed: number;
  inProgress: number;
}

/**
 * 배포 진행 상황을 바 형태로 시각화하는 컴포넌트
 *
 * @param {ProgressBarProps} props - 컴포넌트 Props
 * @returns {JSX.Element} 배포 진행 상황을 나타내는 프로그레스 바 컴포넌트
 */
export const DeploymentProgressBar = ({
  total,
  succeded,
  failed,
  inProgress,
}: ProgressBarProps): JSX.Element => {
  const successWidth = total ? (succeded / total) * 100 : 0;
  const failedWidth = total ? (failed / total) * 100 : 0;
  const inProgressWidth = total ? (inProgress / total) * 100 : 0;

  const overallProgress = total > 0 ? Math.round((succeded / total) * 100) : 0;

  return (
    <div className="flex items-center gap-3">
      <div className="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden">
        <div
          className="h-full bg-green-500 float-left"
          style={{ width: `${successWidth}%` }}
          title={`성공: ${succeded}`}
        ></div>
        <div
          className="h-full bg-blue-500 float-left"
          style={{ width: `${inProgressWidth}%` }}
          title={`진행 중: ${inProgress}`}
        ></div>
        <div
          className="h-full bg-red-500 float-left"
          style={{ width: `${failedWidth}%` }}
          title={`실패: ${failed}`}
        ></div>
      </div>
      <span className="text-sm font-medium text-neutral-600 min-w-max">
        {overallProgress}%
      </span>
    </div>
  );
};
