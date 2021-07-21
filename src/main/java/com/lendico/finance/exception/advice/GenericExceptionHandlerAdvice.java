package com.lendico.finance.exception.advice;

import com.lendico.finance.exception.GenericClientRuntimeException;
import com.lendico.finance.exception.GenericServerRuntimeException;
import com.lendico.finance.model.GenericExceptionResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RestController
@ControllerAdvice
public class GenericExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private static final String URI = "uri=";

    /**
     * Handling MethodArgumentNotValidException
     *
     * @param exception the exception to handle
     * @param request the HttpServletRequest
     * @return the response entity
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        List<String> errorList = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(errorList)
                        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .path(request.getDescription(false).replace(URI, ""))
                        .build()
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling HttpMessageNotReadable
     *
     * @param exception
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(Collections.singletonList(exception.getMessage()))
                        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .path(request.getDescription(false).replace(URI, ""))
                        .build()
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling client runtime exception
     *
     * @param exception the exception to handle
     * @param request the HttpServletRequest
     * @return the response entity
     */
    @ExceptionHandler(GenericClientRuntimeException.class)
    public final ResponseEntity<GenericExceptionResponseDto> handleClientRuntimeException(
            GenericClientRuntimeException exception, HttpServletRequest request) {

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .errors(Collections.singletonList(exception.getMessage()))
                        .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .path(request.getContextPath() + request.getServletPath())
                        .build()
                , HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling server runtime exception
     *
     * @param exception the exception to handle
     * @param request the HttpServletRequest
     * @return the response entity
     */
    @ExceptionHandler(GenericServerRuntimeException.class)
    public final ResponseEntity<GenericExceptionResponseDto> handleServerRuntimeException(
            GenericServerRuntimeException exception, HttpServletRequest request) {

        return new ResponseEntity<>(
                GenericExceptionResponseDto.builder()
                        .timestamp(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .errors(Collections.singletonList(exception.getMessage()))
                        .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path(request.getContextPath() + request.getServletPath())
                        .build()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
