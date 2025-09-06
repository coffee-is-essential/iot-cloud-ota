import { JSX } from "react";
import { LabeledValue } from "../../../shared/ui/LabeledValue";
import { Ad } from "../model/types";

/**
 * 광고 세부 정보를 표시하는 컴포넌트 Props
 */
export interface AdDetailProps {
  ad: Ad | null;
  isLoading: boolean;
  error: string | null;
}

/**
 * AdDetail 컴포넌트는 광고의 세부 정보를 표시합니다.
 * @param {AdDetailProps} props - 컴포넌트 Props
 * @returns {JSX.Element} 광고 세부 정보를 포함하는 JSX 요소
 */
export const AdDetail = ({
  ad,
  isLoading,
  error,
}: AdDetailProps): JSX.Element => {
  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!ad) {
    return <div>No ad details available.</div>;
  }

  return (
    <div className="flex justify-between gap-8">
      <div className="flex flex-col gap-8 w-1/2">
        <LabeledValue label="광고 ID" value={ad.id.toString()} size="sm" />
        <LabeledValue label="광고 이름" value={ad.title ?? "없음"} size="sm" />
        <LabeledValue
          label="광고 설명"
          value={ad.description ?? "없음"}
          size="sm"
        />
        <LabeledValue
          label="생성 날짜"
          value={ad.createdAt.toLocaleString()}
          size="sm"
        />
        <LabeledValue
          label="수정 날짜"
          value={ad.modifiedAt.toLocaleString()}
          size="sm"
        />
      </div>
      <div className="w-1/2">
        <img
          src={ad.originalSignedUrl}
          alt={ad.title ?? "광고 이미지"}
          className="w-full h-auto object-cover aspect-[4/3] rounded-lg border border-gray-200"
        />
      </div>
    </div>
  );
};
