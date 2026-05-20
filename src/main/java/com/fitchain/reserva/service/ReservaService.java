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

    public ReservaResponseDTO crear(ReservaRequestDTO requestDTO) {
        log.info("Creando reserva para clienteId {}", requestDTO.getClienteId());

        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());
        HorarioDTO horario = horarioClient.obtenerHorarioPorId(requestDTO.getHorarioId());

        Reserva reserva = new Reserva();
        reserva.setClienteId(requestDTO.getClienteId());
        reserva.setHorarioId(requestDTO.getHorarioId());
        reserva.setActividad(requestDTO.getActividad());
        reserva.setFecha(requestDTO.getFecha());
        reserva.setHora(requestDTO.getHora());
        reserva.setEstado("PENDIENTE");

        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada con id {}", guardada.getId());
        return toResponseDTO(guardada, cliente, horario);
    }

    public List<ReservaResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las reservas");
        return reservaRepository.findAll().stream()
                .map(r -> toResponseDTO(r,
                        clienteClient.obtenerClientePorId(r.getClienteId()),
                        horarioClient.obtenerHorarioPorId(r.getHorarioId())))
                .toList();
    }

    public ReservaResponseDTO obtenerPorId(Long id) {
        log.info("Buscando reserva con id {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva con id " + id + " no encontrada"));
        return toResponseDTO(reserva,
                clienteClient.obtenerClientePorId(reserva.getClienteId()),
                horarioClient.obtenerHorarioPorId(reserva.getHorarioId()));
    }

    public List<ReservaResponseDTO> obtenerPorCliente(Long clienteId) {
        log.info("Buscando reservas del cliente {}", clienteId);
        ClienteDTO cliente = clienteClient.obtenerClientePorId(clienteId);
        return reservaRepository.findByClienteId(clienteId).stream()
                .map(r -> toResponseDTO(r, cliente, horarioClient.obtenerHorarioPorId(r.getHorarioId())))
                .toList();
    }

    public List<ReservaResponseDTO> obtenerPorEstado(String estado) {
        log.info("Buscando reservas con estado {}", estado);
        return reservaRepository.findByEstado(estado).stream()
                .map(r -> toResponseDTO(r,
                        clienteClient.obtenerClientePorId(r.getClienteId()),
                        horarioClient.obtenerHorarioPorId(r.getHorarioId())))
                .toList();
    }

    public ReservaResponseDTO actualizar(Long id, ReservaRequestDTO requestDTO) {
        log.info("Actualizando reserva con id {}", id);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reserva con id " + id + " no encontrada"));

        ClienteDTO cliente = clienteClient.obtenerClientePorId(requestDTO.getClienteId());
        HorarioDTO horario = horarioClient.obtenerHorarioPorId(requestDTO.getHorarioId());

        reserva.setClienteId(requestDTO.getClienteId());
        reserva.setHorarioId(requestDTO.getHorarioId());
        reserva.setActividad(requestDTO.getActividad());
        reserva.setFecha(requestDTO.getFecha());
        reserva.setHora(requestDTO.getHora());

        Reserva actualizada = reservaRepository.save(reserva);
        log.info("Reserva {} actualizada correctamente", id);
        return toResponseDTO(actualizada, cliente, horario);
    }

    public void eliminar(Long id) {
        log.info("Eliminando reserva con id {}", id);
        if (!reservaRepository.existsById(id)) {
            throw new NoSuchElementException("Reserva con id " + id + " no encontrada");
        }
        reservaRepository.deleteById(id);
        log.info("Reserva {} eliminada", id);
    }
}
