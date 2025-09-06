import { useParams } from "react-router";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { AdDetail } from "../entities/advertisement/ui/AdDetail";
import { useAdDetail } from "../entities/advertisement/api/useAdDetail";
import { DeviceList } from "../entities/device/ui/DeviceList";
import { JSX } from "react";

/**
 * AdDetailPage 컴포넌트는 특정 광고의 세부 정보와 해당 광고가 표시되고 있는 디바이스 목록을 보여줍니다.
 * @returns {JSX.Element} 광고 세부 정보 페이지를 포함하는 JSX 요소
 */
export const AdDetailPage = (): JSX.Element => {
  const { id } = useParams<{ id: string }>();
  const parsedId = id ? parseInt(id) : null;

  if (parsedId === null || isNaN(parsedId)) {
    return <div>광고 ID가 제공되지 않았습니다.</div>;
  }

  const { adDetail, isLoading, error } = useAdDetail(parsedId);

  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8">{error}</div>;
  }

  if (!adDetail) {
    return (
      <div className="flex justify-center py-8">광고를 찾을 수 없습니다.</div>
    );
  }

  return (
    <div className="flex flex-col">
      <div className="mb-8">
        <TitleTile title="광고 관리" description="광고 업로드 및 스케줄링" />
      </div>
      <div className="mb-2">
        <MainTile title="광고 세부 정보">
          <AdDetail
            ad={adDetail.adsMetadata}
            isLoading={isLoading}
            error={error}
          />
        </MainTile>
      </div>
      <div>
        <MainTile title="광고가 표시되고 있는 디바이스 목록">
          <DeviceList devices={adDetail.devices} />
        </MainTile>
      </div>
    </div>
  );
};
