import { JSX, useEffect, useState } from "react";
import { DeployCategory, DeploymentType } from "../model/types";
import { Region } from "../../../entities/region/model/types";
import { Device } from "../../../entities/device/model/types";
import { fetchDevices, fetchGroups, fetchRegions } from "../api/api";
import { RegionTable } from "./RegionTable";
import { Group } from "../../../entities/group/model/types";
import { GroupTable } from "./GroupTable";
import { DeviceTable } from "./DeviceTable";
import { Ad } from "../../../entities/advertisement/model/types";
import { Button } from "../../../shared/ui/Button";
import { toast } from "react-toastify";
import { useAdDeploy } from "../api/useAdDeploy";

/**
 * AdDeploy 컴포넌트의 props
 */
export interface AdDeployProps {
  ads: Ad[];
  onClose: () => void;
}

/**
 * 선택된 항목에 대한 요약 정보를 나타내는 인터페이스
 */
interface DeploySummary {
  message: string;
  count: number;
  items: string;
}

/**
 * 광고 배포 컴포넌트
 * 리전, 기기, 그룹별로 광고를 배포할 수 있는 UI를 제공
 *
 * @param {AdDeployProps} props - 컴포넌트 props
 * @return {JSX.Element} 렌더링된 광고 배포 컴포넌트
 */
export const AdDeploy = ({ ads, onClose }: AdDeployProps): JSX.Element => {
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

  const { mutateAsync: deployAds } = useAdDeploy();

  const handleCategoryChange = (category: DeployCategory) => {
    setSelectedRegions([]);
    setSelectedGroups([]);
    setSelectedDevices([]);
    setDeployCategory(category);
  };

  useEffect(() => {
    const loadData = async () => {
      setIsLoading(true);
      setError(null);

      try {
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
          `데이터 로드 중 오류가 발생했습니다: ${
            error instanceof Error ? error.message : "알 수 없는 오류"
          }`,
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
    setSelectedRegions(checked ? [...regions] : []);
  };

  const handleSelectAllGroups = (checked: boolean) => {
    setSelectedGroups(checked ? [...groups] : []);
  };

  const handleSelectAllDevices = (checked: boolean) => {
    setSelectedDevices(checked ? [...devices] : []);
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
        return { message: "선택된 항목", count: 0, items: "" };
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
      const deploymentType =
        deployCategory === "region"
          ? DeploymentType.REGION
          : deployCategory === "group"
            ? DeploymentType.GROUP
            : DeploymentType.DEVICE;

      if (onClose) {
        onClose();
      }

      await toast.promise(
        deployAds({
          adIds: ads.map((ad) => ad.id),
          deploymentType,
          regions: selectedRegions,
          groups: selectedGroups,
          devices: selectedDevices,
        }),
        {
          pending: "배포 요청 중...",
          success: "배포 요청이 성공적으로 접수되었습니다.",
          error: "배포 요청 중 오류가 발생했습니다.",
        },
      );
    } catch (error) {
      setError(
        `배포 요청 중 오류가 발생했습니다: ${
          error instanceof Error ? error.message : "알 수 없는 오류"
        }`,
      );
    } finally {
      setIsLoading(false);
    }
  };

  const summary = getSelectedSummary();

  return (
    <div className="w-full h-full">
      <div className="mb-6 text-xl font-normal">광고 배포</div>

      {/* 선택된 광고 미리보기 */}
      <div className="mb-12 bg-gray-50 rounded-lg p-4">
        <div className="flex justify-center space-x-4">
          {Array.from({ length: 3 }).map((_, index) => {
            const ad = ads[index];
            return (
              // NOTE: 광고 ID와 div의 key가 중복되는 걸 방지하기 위해 ad-, placeholder- 와 같이 설정
              <div
                key={ad ? `ad-${ad.id}` : `placeholder-${index}`}
                className="w-48"
              >
                <div className="w-full aspect-[4/3] rounded-md shadow-md">
                  {ad ? (
                    <img
                      src={ad.originalSignedUrl}
                      alt={ad.title}
                      className="w-full h-full object-cover rounded-md"
                    />
                  ) : (
                    <div className="w-full h-full bg-white border-2 border-dashed border-gray-300 rounded-md" />
                  )}
                </div>
                <div className="text-sm mt-2 text-center text-gray-600 truncate">
                  {ad ? ad.title : "비어있음"}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* 배포 대상 선택 */}
      <div>
        <div className="text-lg font-medium">배포 대상 선택</div>
        <div className="flex space-x-2 mt-4">
          {deployCategories.map((category) => (
            <button
              key={category}
              onClick={() => handleCategoryChange(category)}
              className={`px-4 py-2 rounded-md font-regular transition-colors ${
                deployCategory === category
                  ? "bg-slate-900 text-white"
                  : "bg-gray-100 text-gray-700 hover:bg-gray-200"
              }`}
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

        <form className="mt-2">
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
      </div>

      {/* 배포 요약 */}
      <div className="bg-gray-50 h-32 p-4 mt-8 rounded-md overflow-y-scroll">
        <div className="font-normal text-base">배포 요약</div>
        <br />
        <p className="text-sm">
          광고:{" "}
          <span className="font-medium">
            {ads.map((ad) => ad.title).join(", ")}
          </span>
        </p>
        <p className="text-sm">
          {summary.message}: {summary.count ? summary.items : "없음"}
        </p>
      </div>

      <div className="flex items-center justify-end gap-2 mt-4">
        <Button title="취소" variant="secondary" onClick={onClose} />
        <Button title="배포" variant="primary" onClick={handleDeploy} />
      </div>
    </div>
  );
};
