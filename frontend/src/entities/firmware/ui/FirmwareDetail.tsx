import { Download } from "lucide-react";
import { Button } from "../../../shared/ui/Button";
import { Firmware } from "../model/types";
import { JSX } from "react";
import { LabeledValue } from "../../../shared/ui/LabeledValue";
import { firmwareApiService } from "../api/api";

/**
 * Props interface for the FirmwareDetail component
 * @interface FirmwareDetailProps
 * @property {Firmware | null} firmware - The firmware data to display
 * @property {boolean} isLoading - Indicates if the firmware data is loading
 * @property {string | null} error - Error message if loading firmware failed
 */
export interface FirmwareDetailProps {
  firmware: Firmware;
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

  /**
   * 펌웨어 파일 다운로드를 처리하는 함수입니다.
   * @async
   * @return {Promise<void>} - 다운로드 완료 후 아무 값도 반환하지 않습니다.
   */
  const handleDownload = async (): Promise<void> => {
    try {
      // 1. 펌웨어의 Presigned URL을 가져옵니다.
      const presignedUrl =
        await firmwareApiService.getFirmwarePresignedDownloadUrl(
          firmware.version,
          firmware.fileName,
        );

      // 2. Presigned URL을 사용하여 펌웨어 파일을 다운로드합니다.
      const blob = await firmwareApiService.downloadFirmwareFile(presignedUrl);

      // 3. Blob 객체를 사용하여 파일을 다운로드합니다.
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = firmware.fileName;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      alert("펌웨어 다운로드에 실패했습니다. 나중에 다시 시도해주세요.");
      console.error("펌웨어 다운로드 실패:", error);
    }
  };

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
          value={firmware?.fileName ?? "없음"}
          size="sm"
        />
        <LabeledValue
          label="업로드 일자"
          value={firmware?.createdAt.toLocaleString() ?? null}
          size="sm"
        />
        <LabeledValue
          label="수정 일자"
          value={firmware?.modifiedAt.toLocaleString() ?? null}
          size="sm"
        />
        <div className="self-start">
          <Button
            icon={<Download className="w-4" />}
            title="펌웨어 다운로드"
            // TODO: Implement download functionality
            onClick={handleDownload}
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
