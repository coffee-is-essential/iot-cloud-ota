import ReactPaginate from "react-paginate";
import { useAdSearch } from "../entities/advertisement/api/useAdSearch";
import { AdList } from "../entities/advertisement/ui/AdList";
import { SearchBar } from "../shared/ui/SearchBar";
import { MainTile } from "../widgets/layout/ui/MainTile";
import { TitleTile } from "../widgets/layout/ui/TitleTile";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "../shared/ui/Button";
import { JSX, useState } from "react";
import ReactModal from "react-modal";
import { AdRegisterForm } from "../features/ad_register/ui/adRegisterForm";
import { Ad } from "../entities/advertisement/model/types";
import { toast } from "react-toastify";
import { AdDeploy } from "../features/ad_deploy/ui/AdDeploy";

/**
 * 광고 목록 페이지 컴포넌트
 * @returns {JSX.Element} 광고 목록 페이지 컴포넌트
 */
export const AdListPage = (): JSX.Element => {
  const { ads, isLoading, error, pagination, handleSearch, handlePageChange } =
    useAdSearch();

  const [adRegisterModalOpen, setAdRegisterModalOpen] = useState(false);
  const [isSelectionMode, setIsSelectionMode] = useState(false);
  const [selectedAds, setSelectedAds] = useState<Ad[]>([]);
  const [adDeployModalOpen, setAdDeployModalOpen] = useState(false);

  const handleAdSelect = (ad: Ad) => {
    setSelectedAds((prevSelected) => {
      if (prevSelected.some((a) => a.id === ad.id)) {
        return prevSelected.filter((a) => a.id !== ad.id);
      }
      if (prevSelected.length < 3) {
        return [...prevSelected, ad];
      }
      toast.warn("최대 3개의 광고만 선택할 수 있습니다.");
      return prevSelected;
    });
  };

  const handlePageClick = (event: { selected: number }) => {
    const newPage = event.selected + 1;
    handlePageChange(newPage);
  };

  const pageCount = pagination?.totalPage ?? 0;
  const currentPage = pagination?.page ?? 1;

  const handleCloseDeployModal = () => {
    setAdDeployModalOpen(false);
    setIsSelectionMode(false);
    setSelectedAds([]);
  };

  return (
    <div className="flex flex-col">
      <div className="mb-8">
        <TitleTile title="광고 관리" description="광고 업로드 및 스케줄링" />
      </div>
      <div>
        <MainTile
          title="업로드 된 광고"
          rightElement={<SearchBar onSearch={handleSearch} />}
        >
          <AdList
            ads={ads}
            isLoading={isLoading}
            error={error}
            isSelectionMode={isSelectionMode}
            selectedAds={selectedAds}
            onAdSelect={handleAdSelect}
          />

          <div className="flex items-center justify-between mt-6 text-neutral-500">
            {/* Empty div to align pagination & button to the right */}
            <div></div>
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
            <div className="flex items-center gap-2">
              {isSelectionMode ? (
                <>
                  <Button
                    title="취소"
                    variant="secondary"
                    onClick={() => {
                      setIsSelectionMode(false);
                      setSelectedAds([]);
                    }}
                  />
                  <Button
                    title={`배포하기 (${selectedAds.length}/3)`}
                    disabled={selectedAds.length === 0}
                    onClick={() => {
                      setAdDeployModalOpen(true);
                    }}
                  />
                </>
              ) : (
                <>
                  <Button
                    title="광고 등록"
                    onClick={() => {
                      setAdRegisterModalOpen(true);
                    }}
                  />
                  <Button
                    title="배포하기"
                    variant="secondary"
                    onClick={() => setIsSelectionMode(true)}
                  />
                </>
              )}
            </div>
          </div>
        </MainTile>

        <ReactModal
          isOpen={adRegisterModalOpen}
          onRequestClose={() => setAdRegisterModalOpen(false)}
          overlayClassName={"bg-black bg-opacity-50 fixed inset-0 z-50"}
          appElement={document.getElementById("root") || undefined}
          className={
            "bg-white w-1/2 max-w-2xl h-auto max-h-[80vh] overflow-auto absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 rounded-lg p-6 shadow-xl"
          }
        >
          <AdRegisterForm onClose={() => setAdRegisterModalOpen(false)} />
        </ReactModal>

        <ReactModal
          isOpen={adDeployModalOpen}
          onRequestClose={handleCloseDeployModal}
          overlayClassName={"bg-black bg-opacity-50 fixed inset-0 z-50"}
          appElement={document.getElementById("root") || undefined}
          className={
            "bg-white w-3/4 max-w-4xl h-auto max-h-[80vh] overflow-auto absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 rounded-lg p-6 shadow-xl"
          }
        >
          <AdDeploy ads={selectedAds} onClose={handleCloseDeployModal} />
        </ReactModal>
      </div>
    </div>
  );
};
