package com.fitchain.reserva.service;

import com.fitchain.reserva.WebClient.ClienteClient;
import com.fitchain.reserva.WebClient.HorarioClient;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteClient clienteClient;
    private final HorarioClient horarioClient;

    private ReservaResponseDTO toResponseDTO(Reserva reserva, ClienteDTO cliente, HorarioDTO horario) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setActividad(reserva.getActividad());
        dto.setFecha(reserva.getFecha());
        dto.setHora(reserva.getHora());
        dto.setEstado(reserva.getEstado());
        dto.setCliente(cliente);
        dto.setHorario(horario);
        return dto;
    }

    private ClienteDTO obtenerCliente(Long clienteId) {
        try {
            ClienteDTO cliente = clienteClient.obtenerClientePorId(clienteId);
            log.info("CLIENTE CON ID {} VALIDADO CORRECTAMENTE", clienteId);
            return cliente;
        } catch (Exception e) {
            log.warn("NO SE PUDO OBTENER EL CLIENTE CON ID {}: {}", clienteId, e.getMessage());
            throw e;
        }
    }

    private HorarioDTO obtenerHorario(Long horarioId) {
        try {
            HorarioDTO horario = horarioClient.obtenerHorarioPorId(horarioId);
            log.info("HORARIO CON ID {} VALIDADO CORRECTAMENTE", horarioId);
            return horario;
        } catch (Exception e) {
            log.warn("NO SE PUDO OBTENER EL HORARIO CON ID {}: {}", horarioId, e.getMessage());
            throw e;
        }
    }

    public ReservaResponseDTO crear(ReservaRequestDTO requestDTO) {
        log.info("CREANDO RESERVA PARA clienteId={}, horarioId={}", requestDTO.getClienteId(), requestDTO.getHorarioId());

        ClienteDTO cliente = obtenerCliente(requestDTO.getClienteId());
        HorarioDTO horario = obtenerHorario(requestDTO.getHorarioId());

        Reserva reserva = new Reserva();
        reserva.setClienteId(requestDTO.getClienteId());
        reserva.setHorarioId(requestDTO.getHorarioId());
        reserva.setActividad(requestDTO.getActividad());
        reserva.setFecha(requestDTO.getFecha());
        reserva.setHora(requestDTO.getHora());
        reserva.setEstado("PENDIENTE");

        Reserva guardada = reservaRepository.save(reserva);
        log.info("RESERVA CREADA EXITOSAMENTE CON ID: {}", guardada.getId());
        return toResponseDTO(guardada, cliente, horario);
    }

    public List<ReservaResponseDTO> obtenerTodas() {
        log.info("LISTANDO TODAS LAS RESERVAS");
        return reservaRepository.findAll().stream()
                .map(r -> toResponseDTO(r,
                        clienteClient.obtenerClientePorId(r.getClienteId()),
                        horarioClient.obtenerHorarioPorId(r.getHorarioId())))
                .toList();
    }

    public ReservaResponseDTO obtenerPorId(Long id) {
        log.info("BUSCANDO RESERVA CON ID: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("RESERVA CON EL ID " + id + " NO ENCONTRADA"));
        log.info("RESERVA CON ID {} ENCONTRADA", id);
        return toResponseDTO(reserva,
                clienteClient.obtenerClientePorId(reserva.getClienteId()),
                horarioClient.obtenerHorarioPorId(reserva.getHorarioId()));
    }

    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        log.info("BUSCANDO RESERVAS DEL CLIENTE CON ID: {}", clienteId);
        ClienteDTO cliente = obtenerCliente(clienteId);
        return reservaRepository.findByClienteId(clienteId).stream()
                .map(r -> toResponseDTO(r, cliente, horarioClient.obtenerHorarioPorId(r.getHorarioId())))
                .toList();
    }

    public List<ReservaResponseDTO> obtenerPorEstado(String estado) {
        log.info("BUSCANDO RESERVAS CON ESTADO: {}", estado);
        return reservaRepository.findByEstado(estado).stream()
                .map(r -> toResponseDTO(r,
                        clienteClient.obtenerClientePorId(r.getClienteId()),
                        horarioClient.obtenerHorarioPorId(r.getHorarioId())))
                .toList();
    }

    public ReservaResponseDTO actualizar(Long id, ReservaRequestDTO requestDTO) {
        log.info("ACTUALIZANDO RESERVA CON ID: {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("RESERVA CON EL ID " + id + " NO HA SIDO ENCONTRADA"));

        ClienteDTO cliente = obtenerCliente(requestDTO.getClienteId());
        HorarioDTO horario = obtenerHorario(requestDTO.getHorarioId());

        reserva.setClienteId(requestDTO.getClienteId());
        reserva.setHorarioId(requestDTO.getHorarioId());
        reserva.setActividad(requestDTO.getActividad());
        reserva.setFecha(requestDTO.getFecha());
        reserva.setHora(requestDTO.getHora());

        Reserva actualizada = reservaRepository.save(reserva);
        log.info("RESERVA CON ID {} ACTUALIZADA EXITOSAMENTE", id);
        return toResponseDTO(actualizada, cliente, horario);
    }

    public void eliminar(Long id) {
        log.info("ELIMINANDO RESERVA CON ID: {}", id);
        if (!reservaRepository.existsById(id)) {
            throw new NoSuchElementException("RESERVA CON EL ID " + id + " NO HA SIDO ENCONTRADA");
        }
        reservaRepository.deleteById(id);
        log.info("RESERVA CON ID {} ELIMINADA EXITOSAMENTE", id);
    }
}
