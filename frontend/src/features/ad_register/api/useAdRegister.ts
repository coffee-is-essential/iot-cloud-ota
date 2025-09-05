import { useMutation, useQueryClient } from "@tanstack/react-query";
import { AdRegisterFormData } from "../model/types";
import { adRegisterApiService } from "./api";

/**
 * 광고 업로드 프로세스 전체를 담당하는 커스텀 훅
 * @description useMutation을 사용하여 광고 등록을 처리하고,
 * 성공 시 광고 목록 쿼리를 무효화하여 자동 리프레시를 유발합니다.
 */
export const useAdRegister = () => {
  const queryClient = useQueryClient();

  // 광고 업로드 프로세스 전체를 처리하는 비동기 함수
  const uploadAd = async (adData: AdRegisterFormData) => {
    if (!adData.originalFile) {
      throw new Error("광고 파일이 없습니다.");
    }

    if (!adData.binaryFile) {
      throw new Error("광고 미리보기 파일이 없습니다.");
    }

    const presignedUrls = await adRegisterApiService.getPresignedUploadUrl(
      adData.title,
    );

    await adRegisterApiService.uploadAdViaPresignedUrl(
      presignedUrls.original.url,
      adData.originalFile,
    );

    await adRegisterApiService.uploadAdViaPresignedUrl(
      presignedUrls.binary.url,
      adData.binaryFile!,
    );

    await adRegisterApiService.uploadAdMetadata(
      adData.title,
      adData.description,
      presignedUrls.original.s3Path,
      presignedUrls.binary.s3Path,
    );
  };

  return useMutation({
    mutationFn: uploadAd,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ads"] });
    },
    onError: (error) => {
      console.error("광고 업로드에 실패했습니다:", error);
    },
  });
};
