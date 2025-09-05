import { JSX } from "react";
import { Ad } from "../model/types";
import { Link } from "react-router";

/**
 * 광고 목록 컴포넌트 Props
 */
export interface AdListProps {
  ads: Ad[];
  isLoading: boolean;
  error: string | null;
}

/**
 * 개별 광고 카드 컴포넌트
 */
const AdCard = ({ ad }: { ad: Ad }) => {
  return (
    <Link
      to={`/ads/${ad.id}`}
      className="group block border border-gray-200 rounded-lg overflow-hidden hover:bg-gray-50 hover:shadow-md transition-all duration-200"
    >
      {/* 이미지 영역 */}
      <div className="w-full aspect-[4/3] bg-gray-100 overflow-hidden">
        <img
          src={ad.originalSignedUrl}
          alt={ad.title}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
        />
      </div>

      {/* 콘텐츠 영역 */}
      <div className="p-4">
        <div className="font-medium text-neutral-800 group-hover:text-neutral-900 transition-colors line-clamp-2">
          {ad.title}
        </div>
        {ad.createdAt && (
          <div className="mt-2 text-xs text-neutral-400">
            {new Date(ad.createdAt).toLocaleDateString()}
          </div>
        )}
      </div>
    </Link>
  );
};

/**
 * 광고 목록 컴포넌트
 * @param {AdListProps} props - 컴포넌트 props
 * @returns {JSX.Element} 광고 목록 컴포넌트
 */
export const AdList = ({ ads, isLoading, error }: AdListProps): JSX.Element => {
  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="text-neutral-600">로딩 중...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center py-12">
        <div className="text-red-500">{error}</div>
      </div>
    );
  }

  if (ads.length === 0) {
    return (
      <div className="py-12 text-center text-neutral-500">
        등록된 광고가 없습니다.
      </div>
    );
  }

  return (
    <div className="p-4">
      <div className="grid grid-cols-1 sm:grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {ads.map((ad) => (
          <AdCard key={ad.id} ad={ad} />
        ))}
      </div>
    </div>
  );
};
