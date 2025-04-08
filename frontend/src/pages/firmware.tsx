import { FirmwareList } from "../entities/firmware/ui/FirmwareList";
import { useFirmwareSearch } from "../features/firmware/api/useFirmwareSearch";
import { SearchBar } from "../shared/ui/SearchBar";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";

export const Firmware = () => {
  const { firmwares, isLoading, error, handleSearch } = useFirmwareSearch();

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
          title="사용중인 펌웨어"
          rightElement={<SearchBar onSearch={handleSearch} />}
        >
          <FirmwareList
            firmwares={firmwares}
            isLoading={isLoading}
            error={error}
          />
        </MainTile>
      </div>
    </div>
  );
};
