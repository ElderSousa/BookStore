package com.bookstore.jpa.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        log.warn("Recurso não encontrado {}", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {

            errors.put(error.getField(), error.getDefaultMessage());
        });

        log.warn("Falha na validação dos argumentos: {}", errors);

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleBusinessEntity(BusinessRuleException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error", ex.getMessage());

        log.warn("Regra de negócio violada {}.", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("error, Violação de integridade dos dados: ", ex.getMostSpecificCause().getMessage());

        log.warn("Violação de integridade dos dados: {}", ex.getMessage());   
    
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // (Opcional) Um "pega-tudo" para outros erros de banco
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleDataAccessException(DataAccessException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Erro interno no servidor ao acessar o banco de dados.");

        log.error("Erro inesperado no acesso ao banco: ", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
