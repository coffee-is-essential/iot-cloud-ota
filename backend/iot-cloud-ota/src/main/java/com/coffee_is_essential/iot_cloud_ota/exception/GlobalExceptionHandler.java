package com.coffee_is_essential.iot_cloud_ota.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.coffee_is_essential.iot_cloud_ota.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러입니다.
 * 애플리케이션 전반에서 발생하는 예외를 일관되게 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * {@link MethodArgumentNotValidException} 예외가 발생했을 때 클라이언트에 에러 메시지와 상태 코드를 반환합니다.
     *
     * @param e 유효성 검증 실패로 인해 발생한 MethodArgumentNotValidException
     * @return 필드별 에러 메시지와 HTTP 상태 코드를 포함한 에러 응답 DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationError(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errors);

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@link ResponseStatusException} 예외가 발생했을 때 클라이언트에 에러 메시지와 상태 코드를 반환합니다.
     *
     * @param e 처리할 ResponseStatusException 예외
     * @return 예외 메시지와 HTTP 상태 코드를 포함한 에러 응답 DTO
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handleResponseStatusError(ResponseStatusException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getReason());
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(errors);

        return new ResponseEntity<>(errorResponseDto, e.getStatusCode());
    }
}
