import { JSX } from "react";
import { Group } from "../../../entities/group/model/types";

/**
 * Interface for GroupTable component props
 * @interface
 * @property {Group[]} groups - List of groups to display
 * @property {Group[]} selectedGroups - List of selected groups
 * @property {(group: Group) => void} onSelectGroup - Callback function to handle group selection
 * @property {(checked: boolean) => void} onSelectAll - Callback function to handle select all action
 */
export interface GroupTableProps {
  groups: Group[];
  selectedGroups: Group[];
  onSelectGroup: (group: Group) => void;
  onSelectAll: (checked: boolean) => void;
}

/**
 * Group selection table component
 * Provides a table view of groups with checkboxes for selection.
 *
 * @param {GroupTableProps} props - Props for the GroupTable component
 * @returns {JSX.Element} Rendered GroupTable component
 */
export const GroupTable = ({
  groups,
  selectedGroups,
  onSelectGroup,
  onSelectAll,
}: GroupTableProps): JSX.Element => {
  return (
    <div className="mt-4 flex flex-col gap-2">
      <h4 className="text-md font-normal text-neutral-800">그룹 선택</h4>
      <div className="border rounded-md overflow-y-scroll h-80">
        <table className="w-full">
          <thead className="bg-gray-100">
            <tr>
              <th>
                <input
                  type="checkbox"
                  className="px-4 py-2"
                  aria-label="Select all groups"
                  checked={
                    groups.length > 0 && selectedGroups.length === groups.length
                  }
                  onChange={(e) => onSelectAll(e.target.checked)}
                />
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                그룹 ID
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                그룹명
              </th>
              <th className="px-4 py-2 text-left text-sm text-gray-500 font-medium">
                기기 수
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {groups.map((group) => (
              <tr key={group.id} className="hover:bg-gray-50">
                <td className="px-4 py-2 text-center">
                  <input
                    type="checkbox"
                    checked={selectedGroups.some((r) => r.id === group.id)}
                    onChange={() => onSelectGroup(group)}
                    className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                </td>
                <td className="px-4 py-2 text-sm">{group.id}</td>
                <td className="px-4 py-2 text-sm">{group.name}</td>
                <td className="px-4 py-2 text-sm">{group.deviceCount}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
