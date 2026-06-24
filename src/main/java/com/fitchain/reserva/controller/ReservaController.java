package com.fitchain.reserva.controller;

import com.fitchain.reserva.assembler.ReservaModelAssembler;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "RESERVAS", description = "GESTION DE LAS RESERVAS")
@Slf4j
@RestController
@RequestMapping("/v1/reservas")
public class ReservaController {

    @Autowired
    private ReservaModelAssembler assembler;

    @Autowired
    private ReservaService reservaService;

    @Operation(summary = "CREAR RESERVA", description = "CREAR UNA NUEVA RESERVA. ACCESO: ADMIN, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "RESERVA CREADA EXITOSAMENTE"),
            @ApiResponse(responseCode = "400", description = "DATOS INVÁLIDOS"),
            @ApiResponse(responseCode = "404", description = "CLIENTE O HORARIO NO ENCONTRADO"),
            @ApiResponse(responseCode = "503", description = "MICROSERVICIO NO DISPONIBLE")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @PostMapping
    public ResponseEntity<EntityModel<ReservaResponseDTO>> crear(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        log.info("POST /v1/reservas - CREAR RESERVA clienteId={}", requestDTO.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(reservaService.crear(requestDTO)));
    }

    @Operation(summary = "OBTENER TODAS LAS RESERVAS", description = "Retorna la lista de todas las reservas. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos suficientes")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponseDTO>>> obtenerTodas() {
        log.info("GET /v1/reservas - LISTAR TODAS");
        List<EntityModel<ReservaResponseDTO>> reservas = reservaService.obtenerTodas().stream()
                .map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(reservas,
                linkTo(methodOn(ReservaController.class).obtenerTodas()).withSelfRel()));
    }

    @Operation(summary = "OBTENER RESERVA POR ID", description = "Retorna una reserva específica por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /v1/reservas/{} - BUSCAR POR ID", id);
        return ResponseEntity.ok(assembler.toModel(reservaService.obtenerPorId(id)));
    }

    @Operation(summary = "OBTENER RESERVAS POR CLIENTE", description = "Retorna todas las reservas de un cliente. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponseDTO>>> obtenerPorCliente(@PathVariable Long clienteId) {
        log.info("GET /v1/reservas/cliente/{} - BUSCAR POR CLIENTE", clienteId);
        List<EntityModel<ReservaResponseDTO>> reservas = reservaService.obtenerPorCliente(clienteId).stream()
                .map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(reservas,
                linkTo(methodOn(ReservaController.class).obtenerPorCliente(clienteId)).withSelfRel()));
    }

    @Operation(summary = "OBTENER RESERVAS POR ESTADO", description = "Retorna reservas filtradas por estado (PENDIENTE, CONFIRMADA, CANCELADA). Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponseDTO>>> obtenerPorEstado(@PathVariable String estado) {
        log.info("GET /v1/reservas/estado/{} - BUSCAR POR ESTADO", estado);
        List<EntityModel<ReservaResponseDTO>> reservas = reservaService.obtenerPorEstado(estado).stream()
                .map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(reservas,
                linkTo(methodOn(ReservaController.class).obtenerPorEstado(estado)).withSelfRel()));
    }

    @Operation(summary = "ACTUALIZAR RESERVA", description = "Actualiza una reserva existente. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO requestDTO) {
        log.info("PUT /v1/reservas/{} - ACTUALIZAR RESERVA", id);
        return ResponseEntity.ok(assembler.toModel(reservaService.actualizar(id, requestDTO)));
    }

    @Operation(summary = "ELIMINAR RESERVA", description = "Elimina una reserva por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponseDTO>> eliminar(@PathVariable Long id) {
        log.info("DELETE /v1/reservas/{} - ELIMINAR RESERVA", id);
        ReservaResponseDTO reserva = reservaService.obtenerPorId(id);
        reservaService.eliminar(id);
        return ResponseEntity.ok(assembler.toModel(reserva));
    }
}
