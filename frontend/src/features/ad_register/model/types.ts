/**
 * 광고 등록 폼 데이터 인터페이스입니다.
 */
export interface AdRegisterFormData {
  title: string;
  description: string;
  originalFile: File | null;
  binaryFile: File | null;
}

/**
 * 광고 업로드를 위한 S3 사전 서명 URL 응답 인터페이스입니다.
 */
export interface AdUploadPresignedUrls {
  original: {
    url: string;
    s3Path: string;
  };
  binary: {
    url: string;
    s3Path: string;
  };
}
