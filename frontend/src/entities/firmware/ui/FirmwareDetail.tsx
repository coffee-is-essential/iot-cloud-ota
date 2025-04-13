import { Download } from "lucide-react";
import { Button } from "../../../shared/ui/Button";
import { Firmware } from "../model/types";
import { JSX } from "react";
import { LabeledValue } from "../../../shared/ui/LabeledValue";

/**
 * Props interface for the FirmwareDetail component
 * @interface FirmwareDetailProps
 * @property {Firmware | null} firmware - The firmware data to display
 * @property {boolean} isLoading - Indicates if the firmware data is loading
 * @property {string | null} error - Error message if loading firmware failed
 */
export interface FirmwareDetailProps {
  firmware: Firmware | null;
  isLoading: boolean;
  error: string | null;
}

/**
 * Component that displays detailed information about a firmware
 * @component
 * @param {FirmwareDetailProps} props - Component props
 * @returns {JSX.Element} Rendered component
 */
export const FirmwareDetail = ({
  firmware,
  isLoading,
  error,
}: FirmwareDetailProps): JSX.Element => {
  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8">{error}</div>;
  }

  return (
    <div className="flex justify-between">
      <div className="flex flex-col gap-8">
        <LabeledValue
          label="펌웨어 버전"
          value={firmware?.version ?? null}
          size="sm"
        />
        <LabeledValue
          label="파일명"
          value={`${firmware?.version}.ino`}
          size="sm"
        />
        <LabeledValue
          label="업로드 일자"
          value={firmware?.createdAt.toLocaleString() ?? null}
          size="sm"
        />
        <LabeledValue
          label="수정 일자"
          value={firmware?.updatedAt.toLocaleString() ?? null}
          size="sm"
        />
        <div className="self-start">
          <Button
            icon={<Download className="w-4" />}
            title="펌웨어 다운로드"
            // TODO: Implement download functionality
            onClick={() => {}}
            disabled={false}
          />
        </div>
      </div>
      <div className="flex flex-col w-3/5 text-sm font-normal text-neutral-600">
        <div className="mb-2 text-sm font-normal text-neutral-600">
          릴리즈 노트
        </div>
        <div className="flex-1 p-2 overflow-auto text-sm font-normal border border-gray-200 rounded-lg shadow-sm text-neutral-800">
          {firmware?.releaseNote}
        </div>
      </div>
    </div>
  );
};
