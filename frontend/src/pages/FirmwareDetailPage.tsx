import { useParams } from "react-router";
import { useFirmwareDetail } from "../features/firmware/api/useFirmwareDetail";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { FirmwareDetail } from "../entities/firmware/ui/FirmwareDetail";
import { Button } from "../shared/ui/Button";

/**
 * FirmwareDetailPage component
 *
 * Renders a page displaying detailed information about a specific firmware.
 * Uses the route parameter 'id' to fetch the firmware details.
 *
 * @component
 * @returns {JSX.Element} The rendered firmware detail page
 */
export const FirmwareDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const parseId = id ? parseInt(id) : null;

  const { firmware, isLoading, error } = useFirmwareDetail(parseId);

  return (
    <div className="flex flex-col">
      <div className="mb-8">
        <TitleTile
          title="펌웨어 관리"
          description="펌웨어 업로드 및 원격 업데이트"
        />
      </div>
      <div>
        <MainTile
          title="펌웨어 세부 정보"
          rightElement={
            <Button
              title="배포하기"
              onClick={() => {
                console.log("배포하기 클릭");
              }}
            />
          }
        >
          <FirmwareDetail
            firmware={firmware}
            isLoading={isLoading}
            error={error}
          />
        </MainTile>
      </div>
    </div>
  );
};
