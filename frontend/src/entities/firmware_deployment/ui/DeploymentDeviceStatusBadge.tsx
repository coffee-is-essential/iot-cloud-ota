import { JSX } from "react";
import { FirmwareDeploymentDeviceStatus } from "../model/types";

/**
 * 디바이스별 배포 상태에 따른 배지 컴포넌트 Props 인터페이스
 */
export interface DeploymentDeviceStatusBadgeProps {
  status: FirmwareDeploymentDeviceStatus["status"];
}

/**
 * 디바이스별 배포 상태에 따른 배지 컴포넌트
 *
 * @param {DeploymentDeviceStatusBadgeProps} props - 컴포넌트 Props
 * @returns {JSX.Element} 상태에 따른 스타일과 라벨이 적용된 배지 컴포넌트
 */
export const DeploymentDeviceStatusBadge = ({
  status,
}: DeploymentDeviceStatusBadgeProps): JSX.Element => {
  let bgColor = "";
  let textColor = "";
  let label = "";

  switch (status) {
    case "WAITING":
      bgColor = "bg-yellow-100";
      textColor = "text-yellow-800";
      label = "대기 중";
      break;
    case "SUCCESS":
      bgColor = "bg-green-100";
      textColor = "text-green-800";
      label = "완료";
      break;
    case "IN_PROGRESS":
      bgColor = "bg-blue-100";
      textColor = "text-blue-800";
      label = "진행 중";
      break;
    case "FAILED":
      bgColor = "bg-red-100";
      textColor = "text-red-800";
      label = "실패";
      break;
    case "TIMEOUT":
      bgColor = "bg-gray-100";
      textColor = "text-gray-800";
      label = "시간 초과";
      break;
    default:
      bgColor = "bg-gray-100";
      textColor = "text-gray-800";
      label = "알 수 없음";
  }

  return (
    <span
      className={`px-2 py-1 rounded-full text-xs font-medium ${bgColor} ${textColor}`}
    >
      {label}
    </span>
  );
};
