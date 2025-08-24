import { FirmwareDeploymentDeviceStatus } from "../model/types";

export interface DeploymentDeviceStatusBadgeProps {
  status: FirmwareDeploymentDeviceStatus["status"];
}

export const DeploymentDeviceStatusBadge = ({
  status,
}: DeploymentDeviceStatusBadgeProps) => {
  let bgColor = "";
  let textColor = "";
  let label = "";

  switch (status) {
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
