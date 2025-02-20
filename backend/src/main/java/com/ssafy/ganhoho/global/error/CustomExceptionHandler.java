package com.ssafy.ganhoho.global.error;

import com.ssafy.ganhoho.global.constant.ErrorCode;
import com.ssafy.ganhoho.global.error.CustomException;
import com.ssafy.ganhoho.global.error.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("커스텀 예외 발생: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(error, e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        log.error("만료된 JWT 토큰: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.EXPIRED_ACCESS_TOKEN);
        return new ResponseEntity<>(error, ErrorCode.EXPIRED_ACCESS_TOKEN.getHttpStatus());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex) {
        log.error("유효하지 않은 JWT 서명: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_ACCESS_TOKEN);
        return new ResponseEntity<>(error, ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex) {
        log.error("잘못된 JWT 토큰: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_ACCESS_TOKEN);
        return new ResponseEntity<>(error, ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus());
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        log.error("지원되지 않는 JWT 토큰: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_ACCESS_TOKEN);
        return new ResponseEntity<>(error, ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus());
    }

    // 기존 예외 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("유효하지 않은 파라미터 타입: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.INVALID_REQUEST_PARAMETERS);
        return new ResponseEntity<>(error, ErrorCode.INVALID_REQUEST_PARAMETERS.getHttpStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("잘못된 요청 형식: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        if (ex.getMessage().contains("WorkType")) {
            errorCode = ErrorCode.INVALID_REQUEST_DATA;
        }

        ErrorResponse error = new ErrorResponse(errorCode);
        return new ResponseEntity<>(error, errorCode.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("유효성 검증 실패: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.MISSING_REQUIRED_FIELDS);
        return new ResponseEntity<>(error, ErrorCode.MISSING_REQUIRED_FIELDS.getHttpStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        log.error("필수 파라미터 누락: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.MISSING_REQUIRED_FIELDS);
        return new ResponseEntity<>(error, ErrorCode.MISSING_REQUIRED_FIELDS.getHttpStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("지원하지 않는 HTTP 메소드: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.BAD_REQUEST);
        return new ResponseEntity<>(error, ErrorCode.BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex) {
        log.error("존재하지 않는 API 엔드포인트: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.NOT_FOUND);
        return new ResponseEntity<>(error, ErrorCode.NOT_FOUND.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("접근 권한 없음: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.ACCES_DENIED);
        return new ResponseEntity<>(error, ErrorCode.ACCES_DENIED.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        log.error("처리되지 않은 예외 발생: ", ex);
        ErrorResponse error = new ErrorResponse(ErrorCode.SERVER_ERROR);
        return new ResponseEntity<>(error, ErrorCode.SERVER_ERROR.getHttpStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("인증 실패: {}", ex.getMessage());
        ErrorCode errorCode;

        if (ex.getMessage().contains("Full authentication is required")) {
            errorCode = ErrorCode.UNAUTHORIZED;
        } else if (ex.getMessage().contains("Bad credentials")) {
            errorCode = ErrorCode.INVALID_ACCESS_TOKEN;
        } else {
            errorCode = ErrorCode.UNAUTHORIZED;
        }

        ErrorResponse error = new ErrorResponse(errorCode);
        return new ResponseEntity<>(error, errorCode.getHttpStatus());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        log.error("불충분한 인증: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(ErrorCode.UNAUTHORIZED);
        return new ResponseEntity<>(error, ErrorCode.UNAUTHORIZED.getHttpStatus());
    }

} 