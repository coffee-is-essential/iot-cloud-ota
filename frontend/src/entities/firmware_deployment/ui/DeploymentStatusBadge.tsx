import { FirmwareDeployment } from "../model/types";

export interface DeploymentStatusBadgeProps {
  status: FirmwareDeployment["status"];
}

export const DeploymentStatusBadge = ({
  status,
}: DeploymentStatusBadgeProps) => {
  let bgColor = "";
  let textColor = "";
  let label = "";

  switch (status) {
    case "PENDING":
      bgColor = "bg-yellow-100";
      textColor = "text-yellow-800";
      label = "대기 중";
      break;
    case "IN_PROGRESS":
      bgColor = "bg-blue-100";
      textColor = "text-blue-800";
      label = "진행 중";
      break;
    case "COMPLETED":
      bgColor = "bg-green-100";
      textColor = "text-green-800";
      label = "완료";
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
