package com.fitchain.reserva.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        log.warn(ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));
        log.warn("[VALIDACIÓN] PETICIÓN RECHAZADA. CAMPOS CON ERRORES: {}", campos);

        ResponseEntity<Map<String, Object>> respuesta = buildResponse(HttpStatus.BAD_REQUEST, "ERROR DE VALIDACIÓN");
        respuesta.getBody().put("campos", campos);
        return respuesta;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonInvalido(HttpMessageNotReadableException ex) {
        log.warn("JSON INVÁLIDO: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "EL CUERPO DE LA PETICIÓN NO ES VÁLIDO");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
        log.warn("PARÁMETRO INVÁLIDO: {} = {}", ex.getName(), ex.getValue());
        return buildResponse(HttpStatus.BAD_REQUEST, "EL VALOR '" + ex.getValue() + "' NO ES VÁLIDO PARA " + ex.getName());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflicto(DataIntegrityViolationException ex) {
        log.error("RESTRICCIÓN DE LA BD: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "YA EXISTE UN REGISTRO CON ESOS DATOS");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleSinPermiso(AccessDeniedException ex) {
        log.warn("ACCESO DENEGADO: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "NO TIENES PERMISOS PARA ESTA ACCIÓN");
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> handleServicioCaido(ServicioNoDisponibleException ex) {
        log.error(ex.getMessage());
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("ERROR INESPERADO", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR INTERNO DEL SERVIDOR");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
