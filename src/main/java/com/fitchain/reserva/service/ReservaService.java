package com.fitchain.reserva.service;

import com.fitchain.reserva.client.ClienteClient;
import com.fitchain.reserva.client.HorarioClient;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.exception.RecursoNoEncontradoException;
import com.fitchain.reserva.model.EstadoReserva;
import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteClient clienteClient;
    private final HorarioClient horarioClient;

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodas() {
        log.info("LISTANDO TODAS LAS RESERVAS ACTIVAS");
        return reservaRepository.findByActivoTrue().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerPorId(Long id) {
        log.info("BUSCANDO RESERVA CON ID: {}", id);
        return mapToDto(buscarActiva(id));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        log.info("BUSCANDO RESERVAS DEL CLIENTE CON ID: {}", clienteId);
        clienteClient.obtenerPorId(clienteId);
        return reservaRepository.findByClienteIdAndActivoTrue(clienteId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerPorEstado(EstadoReserva estado) {
        log.info("BUSCANDO RESERVAS CON ESTADO: {}", estado);
        return reservaRepository.findByEstadoAndActivoTrue(estado).stream()
                .map(this::mapToDto)
                .toList();
    }

    public ReservaResponseDTO crear(ReservaRequestDTO dto) {
        log.info("CREANDO RESERVA PARA clienteId={}, horarioId={}", dto.getClienteId(), dto.getHorarioId());

        ClienteDTO cliente = clienteClient.obtenerPorId(dto.getClienteId());
        HorarioDTO horario = horarioClient.obtenerPorId(dto.getHorarioId());

        Reserva reserva = new Reserva();
        copiarDatos(dto, reserva);
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Reserva guardada = reservaRepository.save(reserva);
        log.info("RESERVA CREADA CON ID: {}", guardada.getId());
        return toDto(guardada, cliente, horario);
    }

    public ReservaResponseDTO actualizar(Long id, ReservaRequestDTO dto) {
        log.info("ACTUALIZANDO RESERVA CON ID: {}", id);
        Reserva reserva = buscarActiva(id);

        ClienteDTO cliente = clienteClient.obtenerPorId(dto.getClienteId());
        HorarioDTO horario = horarioClient.obtenerPorId(dto.getHorarioId());

        copiarDatos(dto, reserva);
        return toDto(reservaRepository.save(reserva), cliente, horario);
    }

    public ReservaResponseDTO cambiarEstado(Long id, EstadoReserva nuevoEstado) {
        log.info("CAMBIANDO ESTADO DE LA RESERVA {} A {}", id, nuevoEstado);
        Reserva reserva = buscarActiva(id);
        reserva.setEstado(nuevoEstado);
        return mapToDto(reservaRepository.save(reserva));
    }

    public void eliminar(Long id) {
        log.info("ELIMINANDO RESERVA CON ID: {}", id);
        Reserva reserva = buscarActiva(id);
        reserva.setActivo(false);
        reservaRepository.save(reserva);
    }

    private Reserva buscarActiva(Long id) {
        return reservaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
    }

    private void copiarDatos(ReservaRequestDTO dto, Reserva reserva) {
        reserva.setClienteId(dto.getClienteId());
        reserva.setHorarioId(dto.getHorarioId());
        reserva.setActividad(dto.getActividad());
        reserva.setFecha(dto.getFecha());
        reserva.setHora(dto.getHora());
    }

    private ReservaResponseDTO mapToDto(Reserva reserva) {
        ClienteDTO cliente = null;
        HorarioDTO horario = null;
        try {
            cliente = clienteClient.obtenerPorId(reserva.getClienteId());
        } catch (RuntimeException e) {
            log.warn("NO SE PUDO OBTENER EL CLIENTE {}: {}", reserva.getClienteId(), e.getMessage());
        }
        try {
            horario = horarioClient.obtenerPorId(reserva.getHorarioId());
        } catch (RuntimeException e) {
            log.warn("NO SE PUDO OBTENER EL HORARIO {}: {}", reserva.getHorarioId(), e.getMessage());
        }
        return toDto(reserva, cliente, horario);
    }

    private ReservaResponseDTO toDto(Reserva reserva, ClienteDTO cliente, HorarioDTO horario) {
        return new ReservaResponseDTO(
                reserva.getId(),
                reserva.getActividad(),
                reserva.getFecha(),
                reserva.getHora(),
                reserva.getEstado().name(),
                cliente,
                horario
        );
    }
}
