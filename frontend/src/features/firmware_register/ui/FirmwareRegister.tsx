import { useRef, useState } from "react";
import { Button } from "../../../shared/ui/Button";
import { FileText, Upload } from "lucide-react";
import { JSX } from "@emotion/react/jsx-runtime";
import { FirmwareRegisterFormData } from "../../../entities/firmware/model/types";
import { firmwareRegisterApiService } from "../api/api";

/**
 * Interface for FirmwareRegisterForm component props
 * @interface
 * @property {Function} onClose - Optional callback function to be called when the form is closed
 */
export interface FirmwareRegisterFormProps {
  onClose?: () => void;
}

/**
 * File upload placeholder component
 * @param {Function} onclick - Function to be called when the placeholder is clicked
 * @returns {JSX.Element} Rendered file upload placeholder component
 */
const FileUploadPlaceHolder = ({
  onClick,
}: {
  onClick: () => void;
}): JSX.Element => (
  <div
    onClick={onClick}
    className="flex flex-col items-center justify-center w-full h-32 text-sm text-blue-900 border-2 border-dashed rounded-md cursor-pointer hover:border-blue-500 hover:text-blue-900"
  >
    <Upload size={24} className="mb-2 text-blue-900" />
    <p className="text-sm text-blue-900">펌웨어 파일을 선택하세요.</p>
    <p className="text-xs text-neutral-500">
      {/* TODO: 펌웨어 확장자가 결정되면 아래의 확장자를 업데이트 해야 합니다. */}
      <span className="font-semibold">*.bin</span> 파일 (최대 100MB)
    </p>
  </div>
);

/**
 * Selected file preview component
 * @param {File} file - The selected file to be previewed
 * @param {Function} onRemove - Handler function to remove the selected file
 */
const FilePreview = ({
  file,
  onRemove,
}: {
  file: File;
  onRemove: () => void;
}): JSX.Element => (
  <div className="flex items-center justify-center w-full h-32 gap-2 px-2 text-sm border rounded-md cursor-pointer text-neutral-800 hover:border-blue-500 hover:text-blue-500">
    <FileText size={20} className="text-blue-900" />
    <p>{file.name}</p>
    <button onClick={onRemove} className="text-red-500">
      X
    </button>
  </div>
);

/**
 * Firmware registration form component
 * Provides a form for users to input firmware version, release notes, and upload a firmware file.
 *
 * @param {Function} onClose - Optional callback function to be called when the form is closed
 * @returns {JSX.Element} Rendered firmware registration form component
 */
export const FirmwareRegisterForm = ({
  onClose,
}: FirmwareRegisterFormProps): JSX.Element => {
  const [formData, setFormData] = useState<FirmwareRegisterFormData>({
    version: "",
    releaseNote: "",
    file: null,
  });

  const fileInputRef = useRef<HTMLInputElement>(null);

  /**
   * File input change handler
   * Validates the file type and size before setting it in the form data.
   *
   * @param {React.ChangeEvent<HTMLInputElement>} event - The change event from the file input
   */
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];

      // TODO: 펌웨어 파일 형식이 정해지고 나면 확장자 체크 추가

      if (file.size > 100 * 1024 * 1024) {
        alert("파일 크기는 100MB를 초과할 수 없습니다.");
        return;
      }

      setFormData((prev) => ({ ...prev, file: file }));
    }
  };

  /**
   * Opens the file selection dialog
   */
  const handleFileClick = () => {
    fileInputRef.current?.click();
  };

  /**
   * Removes the selected file from the form data
   */
  const handleRemoveFile = () => {
    setFormData((prev) => ({ ...prev, file: null }));
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  /**
   * Resets form data and optionally closes the form
   */
  const handleReset = () => {
    setFormData({
      version: "",
      releaseNote: "",
      file: null,
    });
    if (fileInputRef.current) fileInputRef.current.value = "";
    if (onClose) onClose();
  };

  /**
   * Form submission handler
   * Validates the form data and, if valid, makes an API call.
   * Resets the form upon successful submission.
   *
   * @param event - The form submission event
   */
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    // TODO: Implement toast message not alert for better UX

    if (!formData.version) {
      alert("펌웨어 버전을 입력하세요.");
      return;
    }
    if (!formData.releaseNote) {
      alert("릴리즈 노트를 입력하세요.");
      return;
    }
    if (!formData.file) {
      alert("펌웨어 파일을 선택하세요.");
      return;
    }

    // TODO: 아래의 API 호출을 비동기로 처리하고, 토스트 메시지로 성공/실패 알림을 구현하면 UX가 개선될 것입니다.
    try {
      // Step 1: 파일 업로드를 위해 Presigned URL을 가져옵니다.
      const presignedUrl = await firmwareRegisterApiService.getPresignedUrl(
        formData.version,
        formData.file.name,
      );

      // Step 2: Presigned URL을 사용하여 파일을 S3에 업로드합니다.
      await firmwareRegisterApiService.uploadFirmwareViaPresignedUrl(
        presignedUrl.url,
        formData.file,
      );

      // Step 3: 펌웨어 메타데이터를 등록합니다.
      await firmwareRegisterApiService.uploadFirmwareMetadata({
        version: formData.version,
        releaseNote: formData.releaseNote,
        fileName: formData.file.name,
        s3Path: presignedUrl.s3Path,
      });
    } catch (error) {
      console.error("펌웨어 등록 중 오류 발생:", error);
      alert("펌웨어 등록에 실패했습니다. 다시 시도해주세요.");
      return;
    } finally {
      // Reset the form after successful submission
      handleReset();
    }
  };

  return (
    <div>
      <h3 className="mb-6 text-xl font-normal">펌웨어 등록</h3>
      <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
        {/* Version input field */}
        <div className="flex flex-col gap-2">
          <label htmlFor="firmwareVersion" className="text-sm text-neutral-600">
            펌웨어 버전
          </label>
          <input
            type="text"
            id="firmwareVersion"
            placeholder="예: 24.04.1"
            onChange={(e) =>
              setFormData((prev) => ({ ...prev, version: e.target.value }))
            }
            className="px-2 py-2 text-sm border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Release Note input field */}
        <div className="flex flex-col gap-2">
          <label
            htmlFor="firmwareReleaseNote"
            className="text-sm text-neutral-600"
          >
            릴리즈 노트
          </label>
          <textarea
            id="firmwareReleaseNote"
            rows={8}
            placeholder="변경사항을 입력하세요."
            onChange={(e) =>
              setFormData((prev) => ({ ...prev, releaseNote: e.target.value }))
            }
            className="px-2 py-2 text-sm border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Firmware file input field */}
        <div className="flex flex-col gap-2">
          <label htmlFor="firmwareFile" className="text-sm text-neutral-600">
            펌웨어 파일
          </label>
          <input
            ref={fileInputRef}
            type="file"
            id="firmwareFile"
            className="hidden"
            onChange={handleFileChange}
          />

          {!formData.file ? (
            // If no file is selected, show the placeholder
            <FileUploadPlaceHolder onClick={handleFileClick} />
          ) : (
            // If a file is selected, show the file preview
            <FilePreview file={formData.file} onRemove={handleRemoveFile} />
          )}
        </div>

        {/* Submit button */}
        <div className="flex items-center justify-end gap-2 mt-4">
          <Button title="취소" variant="secondary" onClick={handleReset} />
          <Button title="등록" variant="primary" type="submit" />
        </div>
      </form>
    </div>
  );
};
