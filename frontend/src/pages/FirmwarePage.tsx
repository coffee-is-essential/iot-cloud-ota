import ReactPaginate from "react-paginate";
import { FirmwareList } from "../entities/firmware/ui/FirmwareList";
import { useFirmwareSearch } from "../features/firmware/api/useFirmwareSearch";
import { SearchBar } from "../shared/ui/SearchBar";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { ChevronLeft, ChevronRight } from "lucide-react";

export const FirmwarePage = () => {
  const {
    firmwares,
    isLoading,
    error,
    pagination,
    handleSearch,
    handlePageChange,
  } = useFirmwareSearch();

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

          <div className="flex justify-center mt-6 text-neutral-500">
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
          </div>
        </MainTile>
      </div>
    </div>
  );
};
