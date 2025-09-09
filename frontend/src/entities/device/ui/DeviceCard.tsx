import { JSX } from "react";
import { Device } from "../model/types";

/**
 * DeviceCard 컴포넌트 Props
 */
export interface DeviceCardProps {
  device: Device;
}

/**
 * DeviceCard 컴포넌트는 개별 디바이스의 정보를 카드 형식으로 표시합니다.
 * @param {DeviceCardProps} props - 컴포넌트 Props
 * @returns {JSX.Element} 디바이스 정보를 포함하는 JSX 요소
 */
export const DeviceCard = ({ device }: DeviceCardProps): JSX.Element => {
  const lastActive = device.lastActiveAt
    ? new Date(device.lastActiveAt).toLocaleString()
    : "N/A";

  return (
    <div className="bg-white border rounded-lg p-4 shadow-sm hover:shadow-lg transition-shadow duration-300 flex items-center space-x-4">
      <div className="flex-shrink-0">
        <img className="h-24 w-24" src="/robot.png" alt="robot" />
      </div>
      <div className="flex-grow">
        <h3 className="text-xl font-semibold text-neutral-700">
          {device.deviceName}
        </h3>
        <p className="text-sm text-neutral-600">{device.regionName}</p>
        <p className="text-sm text-neutral-600">{device.groupName}</p>
      </div>
      <div className="flex-shrink-0 text-right">
        <p className="text-sm font-medium text-neutral-600">마지막 활동 시간</p>
        <p className="text-xs text-neutral-600">{lastActive}</p>
      </div>
    </div>
  );
};
