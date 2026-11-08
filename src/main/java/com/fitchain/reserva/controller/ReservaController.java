package com.fitchain.reserva.controller;

import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.model.EstadoReserva;
import com.fitchain.reserva.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "RESERVAS", description = "GESTIÓN DE LAS RESERVAS")
@Slf4j
@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(summary = "LISTAR RESERVAS", description = "Lista todas las reservas activas. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        log.info("GET /api/v1/reservas");
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    @Operation(summary = "OBTENER RESERVA POR ID", description = "Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/v1/reservas/{}", id);
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @Operation(summary = "RESERVAS DE UN CLIENTE", description = "Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        log.info("GET /api/v1/reservas/cliente/{}", clienteId);
        return ResponseEntity.ok(reservaService.obtenerPorCliente(clienteId));
    }

    @Operation(summary = "RESERVAS POR ESTADO", description = "PENDIENTE, CONFIRMADA o CANCELADA. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorEstado(@PathVariable EstadoReserva estado) {
        log.info("GET /api/v1/reservas/estado/{}", estado);
        return ResponseEntity.ok(reservaService.obtenerPorEstado(estado));
    }

    @Operation(summary = "CREAR RESERVA", description = "Acceso: ADMIN, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente u horario no encontrado"),
            @ApiResponse(responseCode = "503", description = "Microservicio de cliente u horario no disponible")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        log.info("POST /api/v1/reservas clienteId={}", dto.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
    }

    @Operation(summary = "ACTUALIZAR RESERVA", description = "Reemplaza todos los datos de la reserva. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO dto) {
        log.info("PUT /api/v1/reservas/{}", id);
        return ResponseEntity.ok(reservaService.actualizar(id, dto));
    }

    @Operation(summary = "CAMBIAR ESTADO", description = "Ej: /api/v1/reservas/1/estado?estado=CONFIRMADA. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReservaResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam EstadoReserva estado) {
        log.info("PATCH /api/v1/reservas/{}/estado -> {}", id, estado);
        return ResponseEntity.ok(reservaService.cambiarEstado(id, estado));
    }

    @Operation(summary = "ELIMINAR RESERVA", description = "Borrado lógico. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva eliminada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/v1/reservas/{}", id);
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
