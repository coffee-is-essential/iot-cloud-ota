import { Firmware } from "../model/types";

/**
 * Props for the FirmwareList component
 * @interface FirmwareListProps
 * @property {Firmware[]} firmwares - List of firmware objects
 * @property {boolean} isLoading - Indicates if the data is being loaded
 * @property {string | null} error - Error message if any error occurs
 */
export interface FirmwareListProps {
  firmwares: Firmware[];
  isLoading: boolean;
  error: string | null;
}

/**
 * FirmwareList component displays a list of firmware versions with their upload date and release notes.
 * @param firmwares - List of firmware objects
 * @param isLoading - Boolean indicating if data is being loaded
 * @param error - Error message if any error occurs
 * @returns JSX element representing the firmware list
 */
export const FirmwareList = ({
  firmwares,
  isLoading,
  error,
}: FirmwareListProps) => {
  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8 text-red-500">{error}</div>;
  }

  return (
    <div className="flex flex-col">
      <div className="grid grid-cols-7 gap-4 px-4 py-3 text-sm font-medium text-neutral-600 bg-gray-50 rounded-t-md">
        <div className="col-span-1">버전</div>
        <div className="col-span-2">업로드 날짜</div>
        <div className="col-span-4">릴리즈 노트</div>
      </div>
      {firmwares.length === 0 ? (
        <div className="p-8 text-center">검색 결과가 없습니다.</div>
      ) : (
        <div className="divide-y divide-gray-100">
          {firmwares.map((firmware) => (
            <div
              key={firmware.id}
              className="grid grid-cols-7 gap-4 p-4 transition-colors hover:bg-gray-50"
            >
              <div className="col-span-1 text-sm font-semibold text-neutral-600">
                {firmware.version}
              </div>
              <div className="col-span-2 text-sm text-neutral-600">
                {firmware.createdAt.toLocaleString()}
              </div>
              <div className="col-span-4 text-sm text-neutral-600">
                {firmware.releaseNote}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
