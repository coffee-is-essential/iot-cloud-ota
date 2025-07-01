import os
import json
import dataclasses
import contextlib
import logging
from typing import Any, Iterator

import pymysql

logger = logging.getLogger(__name__)


class Config:
    """환경 변수에서 데이터베이스 설정을 읽고 검증하는 클래스입니다.

    Attributes:
        DB_HOST (str): 데이터베이스 호스트 주소.
        DB_PORT (int): 데이터베이스 포트 번호.
        DB_USER (str): 데이터베이스 사용자명.
        DB_PASSWORD (str): 데이터베이스 비밀번호.
        DB_NAME (str): 데이터베이스 이름.
    """

    DB_HOST = os.getenv("DB_HOST", "")
    DB_PORT = int(os.getenv("DB_PORT", 3306))
    DB_USER = os.getenv("DB_USER", "")
    DB_PASSWORD = os.getenv("DB_PASSWORD", "")
    DB_NAME = os.getenv("DB_NAME", "")

    def validate(self) -> None:
        """설정 값이 모두 올바르게 입력되었는지 검증합니다.

        Raises:
            ValueError: 필수 환경변수가 누락된 경우 발생합니다.
        """
        if not self.DB_HOST:
            raise ValueError("DB_HOST is not set")
        if not self.DB_USER:
            raise ValueError("DB_USER is not set")
        if not self.DB_PASSWORD:
            raise ValueError("DB_PASSWORD is not set")
        if not self.DB_NAME:
            raise ValueError("DB_NAME is not set")
        if not self.DB_PORT:
            raise ValueError("DB_PORT is not set")


@dataclasses.dataclass
class FirmwareMetadata:
    """펌웨어 메타데이터 정보를 저장하는 데이터 클래스입니다.

    Attributes:
        version (str): 펌웨어 버전.
        release_note (str): 릴리즈 노트.
        file_key (str): 펌웨어 파일의 URL 또는 키.
    """

    version: str
    release_note: str
    file_key: str

    @classmethod
    def from_dict(cls, data: dict[str, str]) -> 'FirmwareMetadata':
        """딕셔너리로부터 FirmwareMetadata 인스턴스를 생성합니다.

        Args:
            data: 펌웨어 메타데이터를 담은 딕셔너리.

        Returns:
            생성된 FirmwareMetadata 인스턴스.
        """
        return cls(
            version=data['version'],
            release_note=data['release_note'],
            file_key=data['file_key'],
        )


class DatabaseClient:
    """데이터베이스 연동 및 펌웨어 메타데이터 관리를 위한 클라이언트 클래스입니다."""

    def __init__(
        self,
        host: str,
        port: int,
        username: str,
        password: str,
        database: str,
    ) -> None:
        """DatabaseClient 인스턴스를 초기화합니다.

        Args:
            host: 데이터베이스 호스트 주소.
            port: 데이터베이스 포트 번호.
            username: 데이터베이스 사용자명.
            password: 데이터베이스 비밀번호.
            database: 데이터베이스 이름.
        """
        self._host = host
        self._port = port
        self._username = username
        self._password = password
        self._database = database

    @contextlib.contextmanager
    def _get_connection(self) -> Iterator[pymysql.connections.Connection]:
        """데이터베이스 연결을 위한 컨텍스트 매니저입니다.

        연결을 안전하게 생성 및 종료하며, 예외 발생 시에도 연결을 닫습니다.

        Yields:
            pymysql.connections.Connection: 활성화된 데이터베이스 연결 객체.

        Raises:
            Exception: 데이터베이스 연결 중 오류가 발생하면 예외를 다시 발생시킵니다.
        """
        connection = None
        try:
            connection = pymysql.connect(
                host=self._host,
                port=self._port,
                user=self._username,
                password=self._password,
                db=self._database,
            )
            yield connection
        except Exception as e:
            raise e
        finally:
            if connection:
                connection.close()

    def insert_firmware_metadata(
        self,
        firmware_metadata: FirmwareMetadata,
    ) -> None:
        """펌웨어 메타데이터를 데이터베이스에 삽입합니다.

        Args:
            firmware_metadata: 삽입할 펌웨어 메타데이터 객체.

        Raises:
            Exception: 데이터베이스 작업 중 오류가 발생하면 예외를 발생시킵니다.
        """
        try:
            with self._get_connection() as connection:
                with connection.cursor() as cursor:
                    query = f"""
                        INSERT INTO {self._database}.Firmware(version, release_note, file_key)
                        VALUES (%s, %s, %s)
                    """
                    cursor.execute(query, (
                        firmware_metadata.version,
                        firmware_metadata.release_note,
                        firmware_metadata.file_key,
                    ))
                connection.commit()
        except Exception as e:
            raise e


def lambda_handler(event: dict[str, Any], context: Any) -> dict[str, Any]:
    """AWS Lambda 핸들러 함수 진입 지점입니다.

    이벤트로 전달된 요청에서 펌웨어 메타데이터를 추출하고, 데이터베이스에 저장합니다.

    Args:
        event: Lambda 이벤트 객체.
        context: Lambda 실행 컨텍스트.

    Returns:
        HTTP 상태 코드와 메시지가 포함된 응답 딕셔너리.
    """
    config = Config()
    try:
        config.validate()
    except ValueError as e:
        logger.error(f"Configuration validation failed: {e}")
        return {"statusCode": 500, "body": str(e)}

    db_client = DatabaseClient(
        host=config.DB_HOST,
        port=config.DB_PORT,
        username=config.DB_USER,
        password=config.DB_PASSWORD,
        database=config.DB_NAME,
    )

    try:
        body_dict = json.loads(event['body'])
        firmware_metadata = FirmwareMetadata.from_dict(body_dict)
    except KeyError as e:
        logger.error(f"Missing required field in request body: {e}")
        return {"statusCode": 400, "body": "Missing required field"}

    try:
        db_client.insert_firmware_metadata(firmware_metadata)
    except Exception as e:
        logger.error(f"Database operation failed: {e}")
        return {"statusCode": 500, "body": "Internal server error"}

    return {
        "statusCode": 200,
        "body": "Firmware metadata inserted successfully"
    }
