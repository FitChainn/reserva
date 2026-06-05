package com.fitchain.reserva.controller;

import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "RESERVAS", description = "GESTION DE LAS RESERVAS")
@RestController
@RequestMapping("/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "CREAR RESERVA", description = "Crea una nueva reserva. Acceso: ADMIN, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente o Horario no encontrado"),
            @ApiResponse(responseCode = "503", description = "Microservicio no disponible")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(requestDTO));
    }

    @Operation(summary = "OBTENER TODAS LAS RESERVAS", description = "Retorna la lista de todas las reservas. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    @Operation(summary = "OBTENER RESERVA POR ID", description = "Retorna una reserva específica por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "OBTENER RESERVAS POR CLIENTE", description = "Retorna todas las reservas de un cliente. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerPorCliente(clienteId));
    }

    @Operation(summary = "OBTENER RESERVAS POR ESTADO", description = "Retorna reservas filtradas por estado (PENDIENTE, CONFIRMADA, CANCELADA). Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reservaService.obtenerPorEstado(estado));
    }

    @Operation(summary = "ACTUALIZAR RESERVA", description = "Actualiza una reserva existente. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO requestDTO) {
        return ResponseEntity.ok(reservaService.actualizar(id, requestDTO));
    }

    @Operation(summary = "ELIMINAR RESERVA", description = "Elimina una reserva por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}