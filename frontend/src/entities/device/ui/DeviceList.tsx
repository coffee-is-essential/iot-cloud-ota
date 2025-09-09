import { JSX } from "react";
import { DeviceCard } from "./DeviceCard";
import { Device } from "../model/types";

/**
 * DeviceList 컴포넌트 Props
 */
export interface DeviceListProps {
  devices: Device[];
  isLoading: boolean;
  error: string | null;
}

/**
 * DeviceList 컴포넌트는 디바이스 목록을 표시합니다.
 * @param {DeviceListProps} props - 컴포넌트 Props
 * @returns {JSX.Element} 디바이스 목록을 포함하는 JSX 요소
 */
export const DeviceList = ({
  devices,
  isLoading,
  error,
}: DeviceListProps): JSX.Element => {
  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8 text-red-500">{error}</div>;
  }

  return (
    <div>
      {devices.length === 0 ? (
        <div className="p-8 text-center">검색 결과가 없습니다.</div>
      ) : (
        <div className="flex flex-col gap-4">
          {devices.map((device) => (
            <DeviceCard key={device.deviceId} device={device} />
          ))}
        </div>
      )}
    </div>
  );
};
