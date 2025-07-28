import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";

export const DevicePage = () => {
  return (
    <div className="flex flex-col">
      <div className="mb-8">
        <TitleTile title="기기 관리" description="기기 목록 및 상태 관리" />
      </div>
      <div>
        <MainTile title="등록된 기기 목록" />
      </div>
    </div>
  );
};
