import { JSX, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { RegionApiService } from "../../../entities/region/api/regionApi";
import { GroupApiService } from "../../../entities/group/api/api";
import { useDeviceRegister } from "../api/useDeviceRegister";
import { Region } from "../../../entities/region/model/types";
import { Group } from "../../../entities/group/model/types";
import { DeviceRegisterInfo } from "../model/types";
import { Button } from "../../../shared/ui/Button";
import Countdown from "../../../shared/ui/Countdown";

/**
 * DeviceRegisterForm 컴포넌트의 props
 */
export interface DeviceRegisterFormProps {
  onClose?: () => void;
}

/**
 * 인증 코드와 만료 시간을 표시하는 컴포넌트
 * @param {Object} props - 컴포넌트 props
 * @param {DeviceRegisterInfo} props.registrationInfo - 디바이스 등록 정보 (인증 코드 및 만료 시간)
 * @param {Function} [props.onDone] - 완료 버튼 클릭 시 호출되는 선택적 콜백 함수
 */
const AuthCodeDisplay = ({
  registrationInfo,
  onDone,
}: {
  registrationInfo: DeviceRegisterInfo;
  onDone?: () => void;
}) => (
  <div className="flex flex-col items-center justify-center text-center p-4">
    <h3 className="text-xl font-semibold mb-4">
      아래 인증 코드를 기기에 입력해주세요
    </h3>
    <p className="text-4xl font-bold tracking-widest text-blue-600 bg-gray-100 px-6 py-4 rounded-lg">
      {registrationInfo.code}
    </p>
    <div className="mt-4 text-lg text-red-500">
      <Countdown expiresAt={registrationInfo.expiresAt} />
    </div>
    <div className="mt-8">
      <Button title="완료" variant="primary" onClick={onDone} />
    </div>
  </div>
);

/**
 * DeviceRegisterForm 컴포넌트는 디바이스 등록 폼을 렌더링합니다.
 * @param {Object} props - 컴포넌트 props
 * @param {Function} [props.onClose] - 폼이 닫힐 때 호출되는 선택적 콜백 함수
 * @returns {JSX.Element} 디바이스 등록 폼을 포함하는 JSX 요소
 */
export const DeviceRegisterForm = ({
  onClose,
}: DeviceRegisterFormProps): JSX.Element => {
  const [selectedRegionId, setSelectedRegionId] = useState<
    number | undefined
  >();
  const [selectedGroupId, setSelectedGroupId] = useState<number | undefined>();
  const [registrationInfo, setRegistrationInfo] =
    useState<DeviceRegisterInfo | null>(null);

  const { mutateAsync: registerDevice } = useDeviceRegister();

  const { data: regions, isLoading: isLoadingRegions } = useQuery<Region[]>({
    queryKey: ["regions"],
    queryFn: RegionApiService.getRegions,
  });

  const { data: groups, isLoading: isLoadingGroups } = useQuery<Group[]>({
    queryKey: ["groups"],
    queryFn: GroupApiService.getGroups,
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedRegionId === undefined) {
      toast.error("리전을 선택해주세요.");
      return;
    }
    if (selectedGroupId === undefined) {
      toast.error("그룹을 선택해주세요.");
      return;
    }

    await toast.promise(
      registerDevice({ regionId: selectedRegionId, groupId: selectedGroupId }),
      {
        pending: "디바이스 등록 중...",
        success: {
          render({ data }) {
            setRegistrationInfo(data);
            return "인증 코드가 발급되었습니다.";
          },
        },
        error: "디바이스 등록에 실패했습니다. 다시 시도해주세요.",
      },
    );
  };

  if (registrationInfo) {
    return (
      <AuthCodeDisplay registrationInfo={registrationInfo} onDone={onClose} />
    );
  }

  return (
    <div>
      <h3 className="mb-6 text-xl font-normal">디바이스 등록</h3>
      <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
        <div className="flex flex-col gap-2">
          <label htmlFor="region" className="text-sm text-neutral-600">
            리전
          </label>
          <select
            id="region"
            value={selectedRegionId}
            onChange={(e) => setSelectedRegionId(Number(e.target.value))}
            className="px-2 py-2 text-sm border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            disabled={isLoadingRegions}
            required
          >
            <option value="" disabled selected>
              {isLoadingRegions ? "로딩 중..." : "리전 선택"}
            </option>
            {regions?.map((region) => (
              <option key={region.regionId} value={region.regionId}>
                {region.regionName}
              </option>
            ))}
          </select>
        </div>

        <div className="flex flex-col gap-2">
          <label htmlFor="group" className="text-sm text-neutral-600">
            그룹
          </label>
          <select
            id="group"
            value={selectedGroupId}
            onChange={(e) => setSelectedGroupId(Number(e.target.value))}
            className="px-2 py-2 text-sm border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            disabled={isLoadingGroups}
            required
          >
            <option value="" disabled selected>
              {isLoadingGroups ? "로딩 중..." : "그룹 선택"}
            </option>
            {groups?.map((group) => (
              <option key={group.groupId} value={group.groupId}>
                {String(group.groupName)}
              </option>
            ))}
          </select>
        </div>

        <div className="flex items-center justify-end gap-2 mt-4">
          <Button
            title="취소"
            variant="secondary"
            onClick={onClose}
            type="button"
          />
          <Button title="등록" variant="primary" type="submit" />
        </div>
      </form>
    </div>
  );
};

