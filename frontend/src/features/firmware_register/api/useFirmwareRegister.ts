import { useMutation, useQueryClient } from "@tanstack/react-query";
import { firmwareRegisterApiService } from "../../firmware/api/api";
import { FirmwareRegisterFormData } from "../../../entities/firmware/model/types";

/**
 * 펌웨어 업로드 프로세스 전체를 담당하는 커스텀 훅
 * @description useMutation을 사용하여 펌웨어 등록을 처리하고,
 * 성공 시 펌웨어 목록 쿼리를 무효화하여 자동 리프레시를 유발합니다.
 */
export const useFirmwareRegister = () => {
  const queryClient = useQueryClient();

  const uploadProcess = async (formData: FirmwareRegisterFormData) => {
    if (!formData.file) {
      throw new Error("펌웨어 파일이 없습니다.");
    }

    // 1단계: Presigned URL 요청
    const presignedUrl = await firmwareRegisterApiService.getPresignedUrl(
      formData.version,
      formData.file.name,
    );

    // 2단계: Presigned URL을 사용하여 S3에 파일 업로드
    await firmwareRegisterApiService.uploadFirmwareViaPresignedUrl(
      presignedUrl.url,
      formData.file,
    );

    // 3단계: 서버에 펌웨어 메타데이터 등록
    await firmwareRegisterApiService.uploadFirmwareMetadata({
      version: formData.version,
      releaseNote: formData.releaseNote,
      fileName: formData.file.name,
      s3Path: presignedUrl.s3Path,
    });
  };

  return useMutation({
    mutationFn: uploadProcess,
    onSuccess: () => {
      // mutation이 성공하면 'firmwares' 키로 시작하는 모든 쿼리를 하여 최신 데이터를 다시 가져오도록 합니다.
      // NOTE: useFirmwareSearch 훅에서 사용하는 쿼리 키와 일치시켜야 합니다.
      queryClient.invalidateQueries({ queryKey: ["firmwares"] });
    },
    onError: (error) => {
      console.error("펌웨어 업로드에 실패했습니다:", error);
    },
  });
};
