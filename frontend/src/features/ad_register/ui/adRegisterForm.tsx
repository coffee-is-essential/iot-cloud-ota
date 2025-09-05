import React, { JSX, useRef, useState } from "react";
import { AdRegisterFormData } from "../model/types";
import { useAdRegister } from "../api/useAdRegister";
import { toast } from "react-toastify";
import { FileDigit, Image, SquareArrowOutUpRight, X } from "lucide-react";

/**
 * 광고 업로드 폼 컴포넌트 Props 인터페이스
 */
export interface AdRegisterFormProps {
  onClose?: () => void;
}

/**
 * 광고 업로드 폼 컴포넌트
 * @param {AdRegisterFormProps} props - 컴포넌트 props
 * @returns {JSX.Element} 광고 업로드 폼 컴포넌트
 */
export const AdRegisterForm = ({
  onClose,
}: AdRegisterFormProps): JSX.Element => {
  const [adData, setAdData] = useState<AdRegisterFormData>({
    title: "",
    description: "",
    originalFile: null,
    binaryFile: null,
  });

  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const originalFileInputRef = useRef<HTMLInputElement>(null);
  const binaryFileInputRef = useRef<HTMLInputElement>(null);

  const { mutateAsync: uploadAd } = useAdRegister();

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files && event.target.files.length > 0) {
      const file = event.target.files[0];

      if (file.size > 10 * 1024 * 1024) {
        toast.error("파일 크기는 10MB를 초과할 수 없습니다.");
        return;
      }

      if (event.target.name === "originalFile") {
        // 기존 프리뷰 URL 정리
        if (previewUrl) {
          URL.revokeObjectURL(previewUrl);
          setPreviewUrl(null);
        }

        setAdData((prev) => ({ ...prev, originalFile: file }));

        // 프리뷰 URL 생성
        if (file.type.startsWith("image/") || file.type.startsWith("video/")) {
          const url = URL.createObjectURL(file);
          setPreviewUrl(url);
        }
      } else if (event.target.name === "binaryFile") {
        setAdData((prev) => ({ ...prev, binaryFile: file }));
      }
    }
  };

  const handleOriginalFileClick = () => {
    originalFileInputRef.current?.click();
  };

  const handleBinaryFileClick = () => {
    binaryFileInputRef.current?.click();
  };

  const handleCancel = () => {
    // 프리뷰 URL 정리
    if (previewUrl) {
      URL.revokeObjectURL(previewUrl);
    }
    if (onClose) onClose();
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!adData.title.trim()) {
      toast.error("광고 제목을 입력해주세요.");
      return;
    }

    if (!adData.originalFile) {
      toast.error("광고 파일을 선택해주세요.");
      return;
    }

    if (!adData.binaryFile) {
      toast.error("광고 바이너리 파일을 선택해주세요.");
      return;
    }

    if (onClose) {
      onClose();
    }

    await toast.promise(uploadAd(adData), {
      pending: "광고 업로드 중...",
      success: "광고가 성공적으로 업로드되었습니다!",
      error: "광고 업로드에 실패했습니다.",
    });

    if (previewUrl) {
      URL.revokeObjectURL(previewUrl);
    }
  };

  const removeOriginalFile = () => {
    if (previewUrl) {
      URL.revokeObjectURL(previewUrl);
      setPreviewUrl(null);
    }
    setAdData((prev) => ({ ...prev, originalFile: null }));
    if (originalFileInputRef.current) {
      originalFileInputRef.current.value = "";
    }
  };

  const removeBinaryFile = () => {
    setAdData((prev) => ({ ...prev, binaryFile: null }));
    if (binaryFileInputRef.current) {
      binaryFileInputRef.current.value = "";
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm">
      <div className="border-b border-gray-200 pb-4 mb-6">
        <h3 className="text-xl font-semibold text-gray-900">광고 등록</h3>
        <p className="text-sm text-gray-500 mt-1">새로운 광고를 등록해주세요</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* 제목 입력 */}
        <div className="space-y-2">
          <label className="block text-sm text-neutral-600">
            광고 제목 <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={adData.title}
            onChange={(e) =>
              setAdData((prev) => ({ ...prev, title: e.target.value }))
            }
            className="w-full text-sm px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
            placeholder="광고 제목을 입력하세요"
            required
          />
        </div>

        {/* 설명 입력 */}
        <div className="space-y-2">
          <label className="block text-sm text-neutral-600">광고 설명</label>
          <textarea
            value={adData.description}
            onChange={(e) =>
              setAdData((prev) => ({ ...prev, description: e.target.value }))
            }
            className="w-full px-3 py-2 text-sm border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors resize-vertical"
            placeholder="광고에 대한 상세한 설명을 입력하세요"
            rows={4}
          />
        </div>

        {/* 광고 파일 업로드 */}
        <div className="space-y-3">
          <label className="block text-sm text-neutral-600">
            광고 파일 <span className="text-red-500">*</span>
            <span className="text-xs text-gray-500 ml-2">(최대 10MB)</span>
          </label>

          <div className="flex flex-col gap-3">
            {adData.originalFile ? (
              <div className="flex items-center justify-between px-4 py-3 border border-gray-300 rounded-md bg-gray-50">
                <div className="flex items-center gap-3">
                  <Image size={20} className="text-gray-400" />
                  <span className="text-sm text-neutral-600">
                    {adData.originalFile.name}
                  </span>
                </div>
                <button
                  type="button"
                  onClick={removeOriginalFile}
                  className="text-gray-400 hover:text-red-500 focus:outline-none focus:text-red-500 transition-colors"
                >
                  <X size={20} />
                </button>
              </div>
            ) : (
              <button
                type="button"
                onClick={handleOriginalFileClick}
                className="flex items-center justify-center px-4 py-3 border-2 border-dashed border-gray-300 rounded-md hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              >
                <div className="text-center">
                  <Image size={32} className="mx-auto text-gray-400" />
                  <span className="mt-2 block text-sm text-neutral-600">
                    이미지 선택
                  </span>
                  <span className="text-xs text-gray-500">
                    .png .jpg .jpeg 파일 지원
                  </span>
                </div>
              </button>
            )}

            <input
              type="file"
              name="originalFile"
              ref={originalFileInputRef}
              onChange={handleFileChange}
              className="hidden"
              accept="image/*,video/*"
            />

            {/* 프리뷰 영역 */}
            {previewUrl && (
              <div className="bg-gray-50 rounded-md p-4 border border-gray-200">
                <div className="flex justify-between items-center mb-3">
                  <span className="text-sm text-neutral-600">미리보기</span>
                </div>

                {adData.originalFile?.type.startsWith("image/") ? (
                  <img
                    src={previewUrl}
                    alt="광고 미리보기"
                    className="max-w-full h-auto max-h-48 rounded-md mx-auto block"
                  />
                ) : adData.originalFile?.type.startsWith("video/") ? (
                  <video
                    src={previewUrl}
                    controls
                    className="max-w-full h-auto max-h-48 rounded-md mx-auto block"
                  />
                ) : null}
              </div>
            )}
          </div>
        </div>

        {/* 바이너리 파일 업로드 */}
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <label className="block text-sm text-neutral-600">
              광고 바이너리 파일 <span className="text-red-500">*</span>
              <span className="text-xs text-gray-500 ml-2">(최대 10MB)</span>
            </label>

            <a
              href="https://lvgl.io/tools/imageconverter"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center gap-1 px-3 py-1 text-xs text-blue-600 bg-blue-50 rounded-full hover:bg-blue-100 transition-colors"
            >
              <SquareArrowOutUpRight size={14} />
              바이너리 변환 도구
            </a>
          </div>

          {adData.binaryFile ? (
            <div className="flex items-center justify-between px-4 py-3 border border-gray-300 rounded-md bg-gray-50">
              <div className="flex items-center gap-3">
                <FileDigit size={20} className="text-gray-400" />
                <span className="text-sm text-neutral-600">
                  {adData.binaryFile.name}
                </span>
              </div>
              <button
                type="button"
                onClick={removeBinaryFile}
                className="text-gray-400 hover:text-red-500 focus:outline-none focus:text-red-500 transition-colors"
              >
                <X size={20} />
              </button>
            </div>
          ) : (
            <button
              type="button"
              onClick={handleBinaryFileClick}
              className="flex w-full items-center justify-center px-4 py-3 border-2 border-dashed border-gray-300 rounded-md hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <div className="text-center">
                <FileDigit size={32} className="mx-auto text-gray-400" />
                <span className="mt-2 block text-sm text-neutral-600">
                  바이너리 파일 선택
                </span>
                <span className="text-xs text-gray-500">.bin 파일 지원</span>
              </div>
            </button>
          )}

          <input
            type="file"
            name="binaryFile"
            ref={binaryFileInputRef}
            onChange={handleFileChange}
            className="hidden"
            accept="*/*"
          />
        </div>

        {/* 버튼 영역 */}
        <div className="flex justify-end gap-3 pt-6 border-t border-gray-200">
          <button
            type="button"
            onClick={handleCancel}
            className="px-4 py-2 text-sm text-neutral-600 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
          >
            취소
          </button>
          <button
            type="submit"
            className="px-4 py-2 text-sm text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
          >
            등록하기
          </button>
        </div>
      </form>
    </div>
  );
};
