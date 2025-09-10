import { JSX, useState, useEffect, useRef } from "react";
import { Group } from "../../../entities/group/model/types";
import { Region } from "../../../entities/region/model/types";
import { GroupApiService } from "../../../entities/group/api/api";
import { RegionApiService } from "../../../entities/region/api/regionApi";
import { Filter, X, Plus } from "lucide-react";
import { toast } from "react-toastify";

/**
 * DeviceFilter 컴포넌트 Props
 */
interface DeviceFilterProps {
  onFilterChange: (filters: { regionId?: number; groupId?: number }) => void;
}

/**
 * DeviceFilter 컴포넌트
 * 리전 및 그룹 필터링 기능을 제공합니다.
 */
export const DeviceFilter = ({
  onFilterChange,
}: DeviceFilterProps): JSX.Element => {
  const [regions, setRegions] = useState<Region[]>([]);
  const [groups, setGroups] = useState<Group[]>([]);
  const [selectedRegionId, setSelectedRegionId] = useState<
    number | undefined
  >();
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>();
  const [showRegionFilter, setShowRegionFilter] = useState(false);
  const [showGroupFilter, setShowGroupFilter] = useState(false);
  const regionRef = useRef<HTMLDivElement>(null);
  const groupRef = useRef<HTMLDivElement>(null);

  const [isAddingRegion, setIsAddingRegion] = useState(false);
  const [newRegionName, setNewRegionName] = useState("");
  const [isAddingGroup, setIsAddingGroup] = useState(false);
  const [newGroupName, setNewGroupName] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [regionsData, groupsData] = await Promise.all([
          RegionApiService.getRegions(),
          GroupApiService.getGroups(),
        ]);
        setRegions(regionsData);
        setGroups(groupsData);
      } catch (error) {
        console.error("Error fetching filter data:", error);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        regionRef.current &&
        !regionRef.current.contains(event.target as Node)
      ) {
        setShowRegionFilter(false);
        setIsAddingRegion(false);
        setNewRegionName("");
      }
      if (
        groupRef.current &&
        !groupRef.current.contains(event.target as Node)
      ) {
        setShowGroupFilter(false);
        setIsAddingGroup(false);
        setNewGroupName("");
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleRegionSelect = (regionId?: number) => {
    setSelectedRegionId(regionId);
    onFilterChange({ regionId: regionId, groupId: selectedGroupId });
    setShowRegionFilter(false);
  };

  const handleGroupSelect = (groupId?: number) => {
    setSelectedGroupId(groupId);
    onFilterChange({ regionId: selectedRegionId, groupId: groupId });
    setShowGroupFilter(false);
  };

  const handleRegisterRegion = async () => {
    if (newRegionName.trim() === "") return;
    try {
      const newRegion = await RegionApiService.registerRegion(
        newRegionName.trim(),
      );
      setRegions((prev) => [...prev, newRegion]);
      setNewRegionName("");
      setIsAddingRegion(false);
    } catch (error) {
      console.error(error);
      toast.error("리전 등록에 실패했습니다.");
    }
  };

  const handleRegisterGroup = async () => {
    if (newGroupName.trim() === "") return;
    try {
      const newGroup = await GroupApiService.registerGroup(newGroupName.trim());
      setGroups((prev) => [...prev, newGroup]);
      setNewGroupName("");
      setIsAddingGroup(false);
    } catch (error) {
      console.error(error);
      toast.error("그룹 등록에 실패했습니다.");
    }
  };

  const selectedRegionName = regions.find(
    (r) => r.regionId === selectedRegionId,
  )?.regionName;
  const selectedGroupName = groups.find(
    (g) => g.groupId === selectedGroupId,
  )?.groupName;

  return (
    <div className="flex items-center space-x-2">
      <div className="relative" ref={regionRef}>
        <button
          onClick={() => {
            setShowRegionFilter(!showRegionFilter);
            setShowGroupFilter(false);
          }}
          className="flex items-center space-x-1 px-3 py-1.5 border rounded-md bg-white hover:bg-gray-50 text-sm text-neutral-600"
        >
          <Filter size={14} />
          <span>리전: {selectedRegionName || "전체"}</span>
          {selectedRegionId !== undefined && (
            <X
              size={14}
              onClick={(e) => {
                e.stopPropagation();
                handleRegionSelect();
              }}
            />
          )}
        </button>
        {showRegionFilter && (
          <div className="absolute z-10 mt-2 w-48 bg-white border rounded-md shadow-lg">
            <ul>
              <li
                className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm"
                onClick={() => handleRegionSelect()}
              >
                전체
              </li>
              {regions.map((region) => (
                <li
                  key={region.regionId}
                  className={`px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm ${selectedRegionId === region.regionId ? "font-semibold text-blue-600" : ""}`}
                  onClick={() => handleRegionSelect(region.regionId)}
                >
                  {region.regionName}
                </li>
              ))}
              {isAddingRegion ? (
                <li className="p-2">
                  <input
                    type="text"
                    value={newRegionName}
                    onChange={(e) => setNewRegionName(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") handleRegisterRegion();
                      if (e.key === "Escape") {
                        setIsAddingRegion(false);
                        setNewRegionName("");
                      }
                    }}
                    className="w-full border rounded-md px-2 py-1 text-sm focus:outline-none focus:ring-1 focus:ring-blue-500"
                    placeholder="새 리전 이름"
                    autoFocus
                  />
                </li>
              ) : (
                <li
                  className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm text-blue-600"
                  onClick={() => setIsAddingRegion(true)}
                >
                  <Plus size={14} className="mr-2" />
                  새로 만들기
                </li>
              )}
            </ul>
          </div>
        )}
      </div>

      <div className="relative" ref={groupRef}>
        <button
          onClick={() => {
            setShowGroupFilter(!showGroupFilter);
            setShowRegionFilter(false);
          }}
          className="flex items-center space-x-1 px-3 py-1.5 border rounded-md bg-white hover:bg-gray-50 text-sm text-neutral-600"
        >
          <Filter size={14} />
          <span>그룹: {selectedGroupName || "전체"}</span>
          {selectedGroupId !== undefined && (
            <X
              size={14}
              onClick={(e) => {
                e.stopPropagation();
                handleGroupSelect();
              }}
            />
          )}
        </button>
        {showGroupFilter && (
          <div className="absolute z-10 mt-2 w-48 bg-white border rounded-md shadow-lg">
            <ul>
              <li
                className="px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm"
                onClick={() => handleGroupSelect()}
              >
                전체
              </li>
              {groups.map((group) => (
                <li
                  key={group.groupId}
                  className={`px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm ${selectedGroupId === group.groupId ? "font-semibold text-blue-600" : ""}`}
                  onClick={() => handleGroupSelect(group.groupId)}
                >
                  {group.groupName}
                </li>
              ))}
              {isAddingGroup ? (
                <li className="p-2">
                  <input
                    type="text"
                    value={newGroupName}
                    onChange={(e) => setNewGroupName(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") handleRegisterGroup();
                      if (e.key === "Escape") {
                        setIsAddingGroup(false);
                        setNewGroupName("");
                      }
                    }}
                    className="w-full border rounded-md px-2 py-1 text-sm focus:outline-none focus:ring-1 focus:ring-blue-500"
                    placeholder="새 그룹 이름"
                    autoFocus
                  />
                </li>
              ) : (
                <li
                  className="flex items-center px-4 py-2 hover:bg-gray-100 cursor-pointer text-sm text-blue-600"
                  onClick={() => setIsAddingGroup(true)}
                >
                  <Plus size={14} className="mr-2" />
                  새로 만들기
                </li>
              )}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
};
