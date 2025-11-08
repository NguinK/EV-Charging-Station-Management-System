package com.evcharging.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        if (ex.getResourceName() != null) {
            Map<String, Object> resourceDetails = new LinkedHashMap<>();
            resourceDetails.put("resource", ex.getResourceName());
            resourceDetails.put("field", ex.getFieldName());
            resourceDetails.put("value", ex.getFieldValue());
            body.put("details", resourceDetails);
        }

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    //Handle BusinessException
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", ex.getHttpStatus().value());
        body.put("error", ex.getHttpStatus().getReasonPhrase());
        body.put("message", ex.getMessage());

        if (ex.getErrorCode() != null) {
            body.put("errorCode", ex.getErrorCode());
        }

        if (ex.getDetails() != null) {
            body.put("details", ex.getDetails());
        }

        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    //Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        body.put("message", "Validation failed for one or more fields");
        body.put("errors", errors);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    //Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle method argument type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Type Mismatch");
        body.put("message", String.format("Parameter '%s' should be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"));
        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle missing request parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, WebRequest request) {

        log.warn("Missing parameter: {}", ex.getParameterName());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Missing Parameter");
        body.put("message", String.format("Required parameter '%s' is missing", ex.getParameterName()));
        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle malformed JSON requests
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Malformed JSON");
        body.put("message", "Request body is not readable or contains invalid JSON");
        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Handle HTTP method not supported
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {

        log.warn("Method not supported: {}", ex.getMethod());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        body.put("error", "Method Not Allowed");
        body.put("message", String.format("Method '%s' is not supported for this endpoint", ex.getMethod()));

        if (ex.getSupportedHttpMethods() != null) {
            body.put("supportedMethods", ex.getSupportedHttpMethods());
        }

        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle 404 - endpoint not found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {

        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Endpoint Not Found");
        body.put("message", String.format("No endpoint found for %s %s", ex.getHttpMethod(), ex.getRequestURL()));
        body.put("path", getPath(request));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    //Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Log the full exception for debugging
        ex.printStackTrace();

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to extract path
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
