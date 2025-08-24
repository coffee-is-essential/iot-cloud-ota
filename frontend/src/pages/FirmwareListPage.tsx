import ReactPaginate from "react-paginate";
import { FirmwareList } from "../entities/firmware/ui/FirmwareList";
import { useFirmwareSearch } from "../entities/firmware/api/useFirmwareSearch";
import { SearchBar } from "../shared/ui/SearchBar";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "../shared/ui/Button";
import { useState } from "react";
import ReactModal from "react-modal";
import { FirmwareRegisterForm } from "../features/firmware_register/ui/FirmwareRegister";

export const FirmwareListPage = () => {
  const {
    firmwares,
    isLoading,
    error,
    pagination,
    handleSearch,
    handlePageChange,
  } = useFirmwareSearch();

  const [fwRegisterModalOpen, setFwRegisterModalOpen] = useState(false);

  const handlePageClick = (event: { selected: number }) => {
    const newPage = event.selected + 1;
    handlePageChange(newPage);
  };

  // pagination 객체가 없을 경우를 대비하여 기본값을 설정합니다.
  const pageCount = pagination?.totalPage ?? 0;
  const currentPage = pagination?.page ?? 1;

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

          <div className="flex items-center justify-between mt-6 text-neutral-500">
            {/* Empty div to align pagination & button to the right */}
            <div></div>
            {/* 페이지가 있을 때만 페이지네이션을 렌더링합니다. */}
            {pageCount > 0 && (
              <ReactPaginate
                previousLabel={<ChevronLeft size={18} />}
                nextLabel={<ChevronRight size={18} />}
                pageCount={pageCount}
                onPageChange={handlePageClick}
                forcePage={currentPage - 1} // forcePage는 0부터 시작합니다.
                containerClassName="flex items-center space-x-3"
                pageClassName="hover:text-black"
                pageLinkClassName="text-sm px-2 py-1"
                previousClassName="flex items-center hover:text-black"
                nextClassName="flex items-center hover:text-black"
                activeClassName="text-black font-semibold"
                disabledClassName="opacity-50 cursor-not-allowed"
                breakLabel="..."
                breakClassName="px-2"
              />
            )}
            <Button
              title="펌웨어 등록"
              onClick={() => {
                setFwRegisterModalOpen(true);
              }}
            />
          </div>
        </MainTile>

        <ReactModal
          isOpen={fwRegisterModalOpen}
          onRequestClose={() => setFwRegisterModalOpen(false)}
          overlayClassName={"bg-black bg-opacity-50 fixed inset-0"}
          appElement={document.getElementById("root") || undefined}
          className={
            "bg-white w-1/2 max-w-2xl h-auto absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 rounded-lg p-6 shadow-xl"
          }
        >
          <FirmwareRegisterForm onClose={() => setFwRegisterModalOpen(false)} />
        </ReactModal>
      </div>
    </div>
  );
};
