import { JSX, useEffect, useState } from "react";
import { DeployCategory } from "../model/types";
import { Region } from "../../../entities/region/model/types";
import { Device } from "../../../entities/device/model/types";
import {
  fetchDevices,
  fetchGroups,
  fetchRegions,
  requestFirmwareDeploy,
} from "../api/api";
import { RegionTable } from "./RegionTable";
import { Group } from "../../../entities/group/model/types";
import { GroupTable } from "./GroupTable";
import { DeviceTable } from "./DeviceTable";
import { Firmware } from "../../../entities/firmware/model/types";
import { Button } from "../../../shared/ui/Button";

/**
 * Interface for FirmwareDeploy component props
 * @interface
 * @property {Firmware} firmware - The firmware to deploy
 * @property {() => void} onClose - Callback function to close the deploy modal
 */
export interface FirmwareDeployProps {
  firmware: Firmware;
  onClose: () => void;
}

/**
 * Interface for the summary of selected items for deployment
 * @interface
 * @property {string} message - Summary message to display
 * @property {number} count - Number of selected items
 * @property {string} items - Comma-separated list of selected item names or IDs
 */
interface DeploySummary {
  message: string;
  count: number;
  items: string;
}

/**
 * Firmware deployment component
 * Provides UI for selecting regions, devices, or groups to deploy firmware.
 *
 * @param {FirmwareDeployProps} props - Component properties
 * @return {JSX.Element} Rendered firmware deployment component
 */
export const FirmwareDeploy = ({
  firmware,
  onClose,
}: FirmwareDeployProps): JSX.Element => {
  const deployCategories: DeployCategory[] = ["region", "device", "group"];

  const [deployCategory, setDeployCategory] =
    useState<DeployCategory>("region");

  const [regions, setRegions] = useState<Region[]>([]);
  const [selectedRegions, setSelectedRegions] = useState<Region[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [selectedGroups, setSelectedGroups] = useState<Group[]>([]);
  const [devices, setDevices] = useState<Device[]>([]);
  const [selectedDevices, setSelectedDevices] = useState<Device[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const handleCategoryChange = (category: DeployCategory) => {
    setDeployCategory(category);
  };

  useEffect(() => {
    const loadData = async () => {
      setIsLoading(true);
      setError(null);

      try {
        // TODO: Maybe we can use useMemo here to avoid re-fetching data?
        switch (deployCategory) {
          case "region":
            if (regions.length === 0) {
              const fetchedRegions = await fetchRegions();
              setRegions(fetchedRegions);
            }
            break;
          case "device":
            if (devices.length === 0) {
              const fetchedDevices = await fetchDevices();
              setDevices(fetchedDevices);
            }
            break;
          case "group":
            if (groups.length === 0) {
              const fetchedGroups = await fetchGroups();
              setGroups(fetchedGroups);
            }
            break;
          default:
            throw new Error("Invalid deploy category");
        }
      } catch (error) {
        setError(
          `데이터 로드 중 오류가 발생했습니다: ${error instanceof Error ? error.message : "알 수 없는 오류"}`,
        );
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [deployCategory]);

  const handleRegionSelection = (region: Region) => {
    setSelectedRegions((prev) => {
      if (prev.some((r) => r.regionId === region.regionId)) {
        return prev.filter((r) => r.regionId !== region.regionId);
      }
      return [...prev, region];
    });
  };

  const handleGroupSelection = (group: Group) => {
    setSelectedGroups((prev) => {
      if (prev.some((g) => g.groupId === group.groupId)) {
        return prev.filter((g) => g.groupId !== group.groupId);
      }
      return [...prev, group];
    });
  };

  const handleDeviceSelection = (device: Device) => {
    setSelectedDevices((prev) => {
      if (prev.some((d) => d.deviceId === device.deviceId)) {
        return prev.filter((d) => d.deviceId !== device.deviceId);
      }
      return [...prev, device];
    });
  };

  const handleSelectAllRegions = (checked: boolean) => {
    if (checked) {
      setSelectedRegions([...regions]);
    } else {
      setSelectedRegions([]);
    }
  };

  const handleSelectAllGroups = (checked: boolean) => {
    if (checked) {
      setSelectedGroups([...groups]);
    } else {
      setSelectedGroups([]);
    }
  };

  const handleSelectAllDevices = (checked: boolean) => {
    if (checked) {
      setSelectedDevices([...devices]);
    } else {
      setSelectedDevices([]);
    }
  };

  const getSelectedSummary = (): DeploySummary => {
    switch (deployCategory) {
      case "region":
        return {
          message: "선택된 리전",
          count: selectedRegions.length,
          items: selectedRegions.map((r) => r.regionName).join(", "),
        };
      case "device":
        return {
          message: "선택된 기기",
          count: selectedDevices.length,
          items: selectedDevices.map((d) => d.deviceName).join(", "),
        };
      case "group":
        return {
          message: "선택된 그룹",
          count: selectedGroups.length,
          items: selectedGroups.map((g) => g.groupName).join(", "),
        };
      default:
        return {
          message: "선택된 항목",
          count: 0,
          items: "",
        };
    }
  };

  const handleDeploy = async () => {
    if (
      selectedRegions.length === 0 &&
      selectedGroups.length === 0 &&
      selectedDevices.length === 0
    ) {
      setError("배포할 대상을 하나 이상 선택해주세요.");
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      await requestFirmwareDeploy(
        firmware.id,
        selectedRegions,
        selectedGroups,
        selectedDevices,
      );
      onClose();
    } catch (error) {
      setError(
        `배포 요청 중 오류가 발생했습니다: ${error instanceof Error ? error.message : "알 수 없는 오류"}`,
      );
    } finally {
      setIsLoading(false);
    }
  };

  const summary = getSelectedSummary();

  return (
    <div className="w-full h-full">
      <h3 className="mb-6 text-xl font-normal">펌웨어 배포</h3>
      <div className="flex space-x-2">
        {deployCategories.map((category) => (
          <button
            key={category}
            onClick={() => handleCategoryChange(category)}
            className={`px-4 py-2 rounded-md font-regular transition-colors ${deployCategory === category ? "bg-slate-900 text-white" : "bg-gray-100 text-gray-700 hover:bg-gray-200"} `}
          >
            {category === "region" && "리전별 배포"}
            {category === "device" && "기기별 배포"}
            {category === "group" && "그룹별 배포"}
          </button>
        ))}
      </div>

      {error && (
        <div className="my-4 p-3 bg-red-50 text-red-700 rounded-md">
          {error}
        </div>
      )}

      <form className="mt-6">
        {isLoading ? (
          <div className="flex justify-center items-center h-40">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <div>
            {deployCategory === "region" && (
              <RegionTable
                regions={regions}
                selectedRegions={selectedRegions}
                onSelectRegion={handleRegionSelection}
                onSelectAll={handleSelectAllRegions}
              />
            )}
            {deployCategory === "group" && (
              <GroupTable
                groups={groups}
                selectedGroups={selectedGroups}
                onSelectGroup={handleGroupSelection}
                onSelectAll={handleSelectAllGroups}
              />
            )}
            {deployCategory === "device" && (
              <DeviceTable
                devices={devices}
                selectedDevices={selectedDevices}
                onSelectDevice={handleDeviceSelection}
                onSelectAll={handleSelectAllDevices}
              />
            )}
          </div>
        )}
      </form>

      {/* Deploy Summary */}
      <div className="bg-gray-50 h-32 p-4 mt-6 rounded-md overflow-y-scroll">
        <h4 className="font-normal text-base">배포 요약</h4>
        <br />
        <p className="text-sm">
          펌웨어 버전: <span className="font-medium">{firmware.version}</span>
        </p>
        <p className="text-sm">
          {summary.message}: {summary.count ? summary.items : "없음"}
        </p>
      </div>

      {/* Action Buttons */}
      <div className="flex items-center justify-end gap-2 mt-4">
        <Button title="취소" variant="secondary" onClick={onClose} />
        <Button title="배포" variant="primary" onClick={handleDeploy} />
      </div>
    </div>
  );
};
