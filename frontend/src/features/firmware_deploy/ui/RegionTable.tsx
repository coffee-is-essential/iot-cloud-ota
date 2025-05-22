import { JSX } from "react";
import { Region } from "../../../entities/region/model/types";

/**
 * Interface for RegionTable component props
 * @interface
 * @property {Region[]} regions - List of regions to display
 * @property {Region[]} selectedRegions - List of selected regions
 * @property {(region: Region) => void} onSelectRegion - Callback function to handle region selection
 * @property {(checked: boolean) => void} onSelectAll - Callback function to handle select all action
 */
export interface RegionTableProps {
  regions: Region[];
  selectedRegions: Region[];
  onSelectRegion: (region: Region) => void;
  onSelectAll: (checked: boolean) => void;
}

/**
 * Region selection table component
 * Provides a table view of regions with checkboxes for selection.
 *
 * @param {RegionTableProps} props - Props for the RegionTable component
 * @returns {JSX.Element} Rendered RegionTable component
 */
export const RegionTable = ({
  regions,
  selectedRegions,
  onSelectRegion,
  onSelectAll,
}: RegionTableProps): JSX.Element => {
  return (
    <div className="mt-4 flex flex-col gap-2">
      <h4 className="text-md font-normal text-neutral-800">리전 선택</h4>
      <div className="border rounded-md overflow-y-scroll h-80">
        <table className="w-full">
          <thead className="bg-gray-100">
            <tr>
              <th>
                <input
                  type="checkbox"
                  className="px-4 py-2"
                  aria-label="Select all regions"
                  checked={
                    regions.length > 0 &&
                    selectedRegions.length === regions.length
                  }
                  onChange={(e) => onSelectAll(e.target.checked)}
                />
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                리전 ID
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                리전명
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                기기 수
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {regions.map((region) => (
              <tr key={region.id} className="hover:bg-gray-50">
                <td className="px-4 py-2 text-center">
                  <input
                    type="checkbox"
                    checked={selectedRegions.some((r) => r.id === region.id)}
                    onChange={() => onSelectRegion(region)}
                    className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                </td>
                <td className="px-4 py-2 text-sm">{region.id}</td>
                <td className="px-4 py-2 text-sm">{region.name}</td>
                <td className="px-4 py-2 text-sm">{region.deviceCount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
