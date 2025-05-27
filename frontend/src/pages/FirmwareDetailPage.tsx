import { useParams } from "react-router";
import { useFirmwareDetail } from "../features/firmware/api/useFirmwareDetail";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { FirmwareDetail } from "../entities/firmware/ui/FirmwareDetail";
import { Button } from "../shared/ui/Button";
import ReactModal from "react-modal";
import { useState } from "react";
import { FirmwareDeploy } from "../features/firmware_deploy/ui/FirmwareDeploy";

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

  if (parseId === null || isNaN(parseId)) {
    return <div>Firmware ID is not provided.</div>;
  }

  const [fwDeployModalOpen, setFwDeployModalOpen] = useState(false);
  const { firmware, isLoading, error } = useFirmwareDetail(parseId);

  if (isLoading) {
    return <div className="flex justify-center py-8">로딩 중...</div>;
  }

  if (error) {
    return <div className="flex justify-center py-8">{error}</div>;
  }

  if (!firmware) {
    return (
      <div className="flex justify-center py-8">펌웨어를 찾을 수 없습니다.</div>
    );
  }

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
                setFwDeployModalOpen(true);
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

        <ReactModal
          isOpen={fwDeployModalOpen}
          onRequestClose={() => setFwDeployModalOpen(false)}
          overlayClassName={"bg-black bg-opacity-50 fixed inset-0"}
          appElement={document.getElementById("root") || undefined}
          className={
            "bg-white w-1/2 absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 rounded-lg p-6"
          }
        >
          <FirmwareDeploy
            firmware={firmware}
            onClose={() => setFwDeployModalOpen(false)}
          />
        </ReactModal>
      </div>
    </div>
  );
};
