import logging
import os
from typing import Callable
from urllib.parse import urlparse

import requests

logger = logging.getLogger(__name__)


class HttpClient:
    """HTTP/HTTPS를 통해 파일을 다운로드하는 클라이언트 클래스."""

    def __init__(
        self,
        download_chunk_size: int,
    ) -> None:
        """HttpClient를 초기화합니다.

        Args:
            download_chunk_size: 파일 다운로드 시 사용할 청크 크기 (바이트).
        """
        self._download_chunk_size = download_chunk_size
        logging.info("HttpClient has been initialized successfully.")

    def download_file_with_progress(
        self,
        signed_url: str,
        total_size: int,
        progress_callback: Callable[[int], None],
        output_dir: str = "downloads/",
        timeout: int = 300,
    ) -> str:
        """
        주어진 URL에서 파일을 스트리밍 방식으로 다운로드하고, 진행 상황을 콜백으로 알립니다.

        Args:
            signed_url: 다운로드할 파일의 URL.
            total_size: 다운로드할 파일의 전체 크기 (바이트).
            progress_callback: 다운로드된 바이트를 인자로 받는 콜백 함수.
            output_dir: 파일을 저장할 디렉토리. 기본값은 "downloads/".
            timeout: 요청 타임아웃 시간(초). 기본값은 300.

        Returns:
            파일이 저장된 전체 경로.

        Raises:
            TimeoutError: 다운로드 시간이 초과될 경우.
            requests.exceptions.RequestException: 네트워크 또는 HTTP 오류 발생 시.
            IOError: 파일 쓰기 중 오류 발생 시.
        """
        try:
            logging.info("Start Download: %s", signed_url)

            # 다운로드 디렉토리가 없으면 생성
            os.makedirs(output_dir, exist_ok=True)

            # URL에서 파일 이름 추출
            parsed_url = urlparse(signed_url)
            filename = os.path.basename(parsed_url.path) or "unknown_file"
            output_path = os.path.join(output_dir, filename)

            with requests.get(signed_url, stream=True, timeout=timeout) as response:
                response.raise_for_status()  # 200 OK가 아니면 예외 발생

                downloaded_bytes = 0

                with open(output_path, "wb") as f:
                    # 100KB 청크 단위로 다운로드
                    for chunk in response.iter_content(
                        chunk_size=self._download_chunk_size
                    ):
                        if chunk:
                            f.write(chunk)
                            downloaded_bytes += len(chunk)
                            progress_callback(downloaded_bytes)

                # 다운로드가 완료된 후 최종 진행 상황(100%)을 보고
                progress_callback(downloaded_bytes)
                logging.info(
                    "Download completed successfully. Saved to %s",
                    output_path,
                )
                return output_path

        except requests.exceptions.Timeout:
            error_message = (
                f"Download from {signed_url} timed out after {timeout} seconds."
            )
            logging.error(error_message)
            raise TimeoutError(error_message)
        except requests.exceptions.RequestException as e:
            logging.error("Request Error occurred during download: %s", e)
            raise
        except IOError as e:
            logging.error("I/O Error occurred while saving the file: %s", e)
            raise
