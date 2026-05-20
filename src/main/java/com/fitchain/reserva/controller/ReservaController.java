package com.fitchain.reserva.controller;

import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(reservaService.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO requestDTO) {
        return ResponseEntity.ok(reservaService.actualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
