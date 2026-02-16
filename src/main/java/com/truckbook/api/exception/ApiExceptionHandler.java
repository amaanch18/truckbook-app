package com.truckbook.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    java.util.Map<String, String> fields = new java.util.LinkedHashMap<>();
    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("Validation failed", fields, request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiError> handleBadRequest(
      BadRequestException ex,
      HttpServletRequest request) {
    if (ex.getFields() != null && !ex.getFields().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiError(ex.getMessage(), ex.getFields(), request.getRequestURI(), OffsetDateTime.now().toString()));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError(ex.getMessage(), request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(
      NotFoundException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError(ex.getMessage(), request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiError> handleConflict(
      ConflictException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError(ex.getMessage(), request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ApiError> handleMissingHeader(
      MissingRequestHeaderException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("X-Org-Id header is required", request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("Invalid path parameter", request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleUnreadable(
      HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("Invalid request body", request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<ApiError> handleTooManyRequests(
      TooManyRequestsException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(new ApiError(ex.getMessage(), request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(BadGatewayException.class)
  public ResponseEntity<ApiError> handleBadGateway(
      BadGatewayException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(new ApiError(ex.getMessage(), request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgument(
      IllegalArgumentException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError(ex.getMessage(), request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiError> handleAuth(
      AuthenticationException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("Unauthorized", request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(
      AccessDeniedException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("Unauthorized", request.getRequestURI(), OffsetDateTime.now().toString()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handleDataIntegrity(
      DataIntegrityViolationException ex,
      HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("Request violates a uniqueness constraint", request.getRequestURI(), OffsetDateTime.now().toString()));
  }
}
