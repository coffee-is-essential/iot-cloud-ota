import ReactPaginate from "react-paginate";
import { FirmwareList } from "../entities/firmware/ui/FirmwareList";
import { useFirmwareSearch } from "../features/firmware/api/useFirmwareSearch";
import { SearchBar } from "../shared/ui/SearchBar";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "../shared/ui/Button";
import { useState } from "react";
import ReactModal from "react-modal";
import { FirmwareRegisterForm } from "../features/firmware_register/ui/FirmwareRegister";

export const FirmwarePage = () => {
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
    const newPage = event.selected + 1; // ReactPaginate uses zero-based index
    handlePageChange(newPage);
  };

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
            <ReactPaginate
              previousLabel={<ChevronLeft size={18} />}
              nextLabel={<ChevronRight size={18} />}
              pageCount={pagination.totalPage}
              onPageChange={handlePageClick}
              forcePage={pagination.page - 1}
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
            "bg-white w-1/2 h-2/3 absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 rounded-lg p-6"
          }
        >
          <FirmwareRegisterForm onClose={() => setFwRegisterModalOpen(false)} />
        </ReactModal>
      </div>
    </div>
  );
};
