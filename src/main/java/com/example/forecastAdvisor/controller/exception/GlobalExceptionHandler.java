package com.example.forecastAdvisor.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions specifically thrown by WebClient when an HTTP error response (4xx or 5xx)
     * is received from an external service.
     *
     * @param ex The WebClientResponseException that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with details about the external service error.
     */
    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebClientResponseException(WebClientResponseException ex) {
        System.err.println("WebClientResponseException caught: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());

        HttpStatusCode status = ex.getStatusCode();
        String message = "External service error: " + ex.getStatusText();
        String details = ex.getResponseBodyAsString();

        if (status.is4xxClientError())
            message = "Bad request to external service or resource not found.";
        else if (status.is5xxServerError())
            message = "External service is currently unavailable or encountered an internal error.";

        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, details);
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }

    /**
     * Handles any other unexpected exceptions that might occur in your controllers.
     * This acts as a fallback for unhandled exception types.
     *
     * @param ex The general Exception that was thrown.
     * @return A ResponseEntity containing an ErrorResponse with a generic internal server error message.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        System.err.println("An unexpected error occurred: " + ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred. Please try again later.";
        String details = ex.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, details);
        return Mono.just(new ResponseEntity<>(errorResponse, status));
    }
}