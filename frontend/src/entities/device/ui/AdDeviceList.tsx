import { JSX } from "react";
import { Device } from "../model/types";

/**
 * AdDeviceList 컴포넌트 Props
 */
interface AdDeviceListProps {
  devices: Device[];
}

/**
 * AdDeviceList 컴포넌트는 기기 목록을 테이블 형식으로 표시합니다.
 * 광고 상세 페이지에서 해당 광고를 사용중인 기기들을 표시하는데 사용됩니다.
 * @param {DeviceListProps} props - 컴포넌트 Props
 * @returns {JSX.Element} 기기 목록을 포함하는 JSX 요소
 */
export const AdDeviceList = ({ devices }: AdDeviceListProps): JSX.Element => {
  if (!devices || devices.length === 0) {
    return (
      <div className="text-center py-4 text-neutral-600 border-t">
        표시할 디바이스가 없습니다.
      </div>
    );
  }

  return (
    <div className="overflow-x-auto border-t">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th
              scope="col"
              className="px-6 py-3 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider"
            >
              기기 ID
            </th>
            <th
              scope="col"
              className="px-6 py-3 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider"
            >
              리전명
            </th>
            <th
              scope="col"
              className="px-6 py-3 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider"
            >
              그룹명
            </th>
            <th
              scope="col"
              className="px-6 py-3 text-left text-xs font-medium text-neutral-600 uppercase tracking-wider"
            >
              기기 이름
            </th>
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-200">
          {devices.map((device) => (
            <tr key={device.deviceId}>
              <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                {device.deviceId}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-neutral-600">
                {device.regionName}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-neutral-600">
                {device.groupName}
              </td>
              <td className="px-6 py-4 whitespace-nowrap text-sm text-neutral-600">
                {device.deviceName}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
