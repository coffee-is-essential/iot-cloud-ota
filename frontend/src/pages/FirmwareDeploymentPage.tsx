import ReactPaginate from "react-paginate";
import { useFirmwareDeploymentSearch } from "../entities/firmware_deployment/api/useFirmwareDeploymentSearch";
import { FirmwareDeploymentList } from "../entities/firmware_deployment/ui/FirmwareDeploymentList";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { SearchBar } from "../shared/ui/SearchBar";

/**
 * 펌웨어 배포 현황 페이지 컴포넌트
 */
export const FirmwareDeploymentPage = () => {
  const {
    firmwareDeployments,
    pagination,
    isLoading,
    error,
    handleSearch,
    handlePageChange,
  } = useFirmwareDeploymentSearch();

  const handlePageClick = (event: { selected: number }) => {
    const newPage = event.selected + 1;
    handlePageChange(newPage);
  };

  const pageCount = pagination?.totalPage ?? 0;
  const currentPage = pagination?.page ?? 1;

  console.log("pageCount:", pageCount, "currentPage:", currentPage);

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
          title="펌웨어 배포 현황"
          rightElement={<SearchBar onSearch={handleSearch} />}
        >
          <FirmwareDeploymentList
            deployments={firmwareDeployments}
            isLoading={isLoading}
            error={error}
          />

          <div className="flex items-center justify-center mt-6 text-neutral-500">
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
          </div>
        </MainTile>
      </div>
    </div>
  );
};
