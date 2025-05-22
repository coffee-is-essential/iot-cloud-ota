import { JSX } from "react";
import { Device } from "../../../entities/device/model/types";

/**
 * Interface for DeviceTable component props
 * @interface
 * @property {Device[]} devices - List of devices to display
 * @property {Device[]} selectedDevices - List of selected devices
 * @property {(device: Device) => void} onSelectDevice - Callback function to handle device selection
 * @property {() => void} onSelectAll - Callback function to handle select all action
 */
export interface DeviceTableProps {
  devices: Device[];
  selectedDevices: Device[];
  onSelectDevice: (device: Device) => void;
  onSelectAll: (checked: boolean) => void;
}

/**
 * Device selection table component
 * Provides a table view of devices with checkboxes for selection.
 *
 * @param {DeviceTableProps} props - Props for the DeviceTable component
 * @returns {JSX.Element} Rendered DeviceTable component
 */
export const DeviceTable = ({
  devices,
  selectedDevices,
  onSelectDevice,
  onSelectAll,
}: DeviceTableProps): JSX.Element => {
  return (
    <div className="mt-4 flex flex-col gap-2">
      <h4 className="text-md font-normal text-neutral-800">기기 선택</h4>
      <div className="border rounded-md overflow-y-scroll h-80">
        <table className="w-full">
          <thead className="bg-gray-100">
            <tr>
              <th>
                <input
                  type="checkbox"
                  className="px-4 py-2"
                  checked={selectedDevices.length === devices.length}
                  onChange={(e) => onSelectAll(e.target.checked)}
                />
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                기기 ID
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                리전
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                상태
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {devices.map((device) => (
              <tr key={device.id} className="hover:bg-gray-50">
                <td className="px-4 py-2 text-center">
                  <input
                    type="checkbox"
                    checked={selectedDevices.some((d) => d.id === device.id)}
                    onChange={() => onSelectDevice(device)}
                    className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                </td>
                <td className="px-4 py-2 text-sm">{device.id}</td>
                <td className="px-4 py-2 text-sm">{device.regionName}</td>
                <td className="px-4 py-2 text-sm">
                  {device.isActive ? "활성" : "비활성"}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
