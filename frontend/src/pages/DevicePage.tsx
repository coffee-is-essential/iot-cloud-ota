import { JSX, useState } from "react";
import { useDeviceSearch } from "../entities/device/api/useDeviceSearch";
import { DeviceList } from "../entities/device/ui/DeviceList";
import { SearchBar } from "../shared/ui/SearchBar";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import ReactPaginate from "react-paginate";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { DeviceFilter } from "../features/device_filter/ui/DeviceFilter";
import ReactModal from "react-modal";
import { Button } from "../shared/ui/Button";
import { DeviceRegisterForm } from "../features/device_register/ui/DeviceRegisterForm";

/**
 * DevicePage 컴포넌트는 디바이스 관리 페이지를 렌더링합니다.
 * @returns {JSX.Element} 디바이스 관리 페이지를 포함하는 JSX 요소
 */
export const DevicePage = (): JSX.Element => {
  const {
    devices,
    isLoading,
    error,
    pagination,
    handleSearch,
    handlePageChange,
    handleFilterChange,
  } = useDeviceSearch();

  const [deviceRegisterModalOpen, setDeviceRegisterModalOpen] = useState(false);

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
          title="디바이스 관리"
          description="등록된 디바이스 목록 및 상태"
        />
      </div>
      <div>
        <MainTile
          title="등록된 디바이스"
          rightElement={
            <div className="flex items-center space-x-2">
              <DeviceFilter onFilterChange={handleFilterChange} />
              <SearchBar onSearch={handleSearch} />
            </div>
          }
        >
          <DeviceList devices={devices} isLoading={isLoading} error={error} />

          <div className="flex items-center justify-between mt-6 text-neutral-500">
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
              title="디바이스 등록"
              onClick={() => setDeviceRegisterModalOpen(true)}
            />
          </div>
        </MainTile>

        <ReactModal
          isOpen={deviceRegisterModalOpen}
          onRequestClose={() => setDeviceRegisterModalOpen(false)}
          contentLabel="디바이스 등록 모달"
          className="max-w-lg mx-auto mt-20 bg-white p-6 rounded-lg shadow-lg outline-none"
          overlayClassName="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center"
        >
          <DeviceRegisterForm
            onClose={() => setDeviceRegisterModalOpen(false)}
          />
        </ReactModal>
      </div>
    </div>
  );
};
