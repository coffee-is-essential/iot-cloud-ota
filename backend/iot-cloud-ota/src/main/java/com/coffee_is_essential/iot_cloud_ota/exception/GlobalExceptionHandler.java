package com.coffee_is_essential.iot_cloud_ota.exception;

import com.coffee_is_essential.iot_cloud_ota.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러입니다.
 * 애플리케이션 전반에서 발생하는 예외를 일관되게 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 요청 본문(@RequestBody)에서 유효성 검사(@Valid)가 실패했을 때 발생하는 예외를 처리합니다.
     * 필드별 메시지를 담아 400 BAD_REQUEST 응답을 반환합니다.
     *
     * @param e 유효성 검증 실패로 인해 발생한 MethodArgumentNotValidException
     * @return 필드별 에러 메시지를 포함한 에러 응답 DTO
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
}
