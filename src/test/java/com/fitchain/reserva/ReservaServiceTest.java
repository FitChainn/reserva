package com.fitchain.reserva;

import com.fitchain.reserva.WebClient.ClienteClient;
import com.fitchain.reserva.WebClient.HorarioClient;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import com.fitchain.reserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE RESERVA")
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepo;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private HorarioClient horarioClient;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva r;
    private ClienteDTO clienteDTO;
    private HorarioDTO horarioDTO;
    private ReservaRequestDTO rRequest;

    @BeforeEach
    void setUp(){
        r = new Reserva(1L, 2L, 1L, "YOGA", LocalDate.now(), LocalTime.of(9,0), "PENDIENTE");
        clienteDTO = new ClienteDTO(2L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995,5,10), 3L, 4L);
        horarioDTO = new HorarioDTO(1L, LocalTime.of(9,0), LocalTime.of(10,0));
        rRequest = new ReservaRequestDTO(2L, 1L, "YOGA", LocalDate.now(), LocalTime.of(9,0));
    }

    @Test
    @DisplayName("DEBE CREAR UNA RESERVA")
    void shouldCrearReserva(){
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(horarioClient.obtenerHorarioPorId(1L)).thenReturn(horarioDTO);
        when(reservaRepo.save(any(Reserva.class))).thenReturn(r);

        ReservaResponseDTO result = reservaService.crear(rRequest);

        assertNotNull(result);
        assertEquals("YOGA", result.getActividad());
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals("JUANITO PEREZ", result.getCliente().getNombre());
        assertEquals(1L, result.getHorario().getId());
        verify(reservaRepo, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("DEBE RETORNAR TODAS LAS RESERVAS")
    void shouldReturnTodasLasReservas(){
        Reserva r2 = new Reserva(2L, 2L, 1L, "PILATES", LocalDate.now(), LocalTime.of(10,0), "CONFIRMADA");
        when(reservaRepo.findAll()).thenReturn(List.of(r, r2));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(horarioClient.obtenerHorarioPorId(1L)).thenReturn(horarioDTO);

        List<ReservaResponseDTO> result = reservaService.obtenerTodas();

        assertEquals(2, result.size());
        assertEquals("YOGA", result.get(0).getActividad());
        assertEquals("PILATES", result.get(1).getActividad());
        verify(reservaRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("DEBE RETORNAR UNA RESERVA POR ID")
    void shouldReturnReservaById(){
        when(reservaRepo.findById(1L)).thenReturn(Optional.of(r));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(horarioClient.obtenerHorarioPorId(1L)).thenReturn(horarioDTO);

        ReservaResponseDTO result = reservaService.obtenerPorId(r.getId());

        assertNotNull(result);
        assertEquals("YOGA", result.getActividad());
        assertEquals("JUANITO PEREZ", result.getCliente().getNombre());
        verify(reservaRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION SI RESERVA NO EXISTE POR ID")
    void shouldThrowWhenReservaNotFoundById(){
        when(reservaRepo.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> reservaService.obtenerPorId(99L));

        assertEquals("Reserva con id 99 no encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("DEBE RETORNAR RESERVAS POR CLIENTE")
    void shouldReturnReservasByCliente(){
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(horarioClient.obtenerHorarioPorId(1L)).thenReturn(horarioDTO);
        when(reservaRepo.findByClienteId(2L)).thenReturn(List.of(r));

        List<ReservaResponseDTO> result = reservaService.obtenerPorCliente(2L);

        assertEquals(1, result.size());
        assertEquals("YOGA", result.get(0).getActividad());
        assertEquals(2L, result.get(0).getCliente().getId());
        verify(reservaRepo, times(1)).findByClienteId(2L);
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI CLIENTE NO TIENE RESERVAS")
    void shouldReturnEmptyListWhenClienteHasNoReservas(){
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(reservaRepo.findByClienteId(2L)).thenReturn(List.of());

        List<ReservaResponseDTO> result = reservaService.obtenerPorCliente(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE RETORNAR RESERVAS POR ESTADO")
    void shouldReturnReservasByEstado(){
        when(reservaRepo.findByEstado("PENDIENTE")).thenReturn(List.of(r));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(horarioClient.obtenerHorarioPorId(1L)).thenReturn(horarioDTO);

        List<ReservaResponseDTO> result = reservaService.obtenerPorEstado("PENDIENTE");

        assertEquals(1, result.size());
        assertEquals("PENDIENTE", result.get(0).getEstado());
        verify(reservaRepo, times(1)).findByEstado("PENDIENTE");
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI NO HAY RESERVAS CON ESE ESTADO")
    void shouldReturnEmptyListWhenNoReservasWithEstado(){
        when(reservaRepo.findByEstado("CANCELADA")).thenReturn(List.of());

        List<ReservaResponseDTO> result = reservaService.obtenerPorEstado("CANCELADA");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ACTUALIZAR UNA RESERVA")
    void shouldActualizarReserva(){
        ReservaRequestDTO updateReq = new ReservaRequestDTO(2L, 1L, "PILATES", LocalDate.now(), LocalTime.of(10,0));
        Reserva actualizada = new Reserva(1L, 2L, 1L, "PILATES", LocalDate.now(), LocalTime.of(10,0), "PENDIENTE");

        when(reservaRepo.findById(1L)).thenReturn(Optional.of(r));
        when(clienteClient.obtenerClientePorId(2L)).thenReturn(clienteDTO);
        when(horarioClient.obtenerHorarioPorId(1L)).thenReturn(horarioDTO);
        when(reservaRepo.save(any(Reserva.class))).thenReturn(actualizada);

        ReservaResponseDTO result = reservaService.actualizar(1L, updateReq);

        assertNotNull(result);
        assertEquals("PILATES", result.getActividad());
        verify(reservaRepo, times(1)).findById(1L);
        verify(reservaRepo, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION AL ACTUALIZAR RESERVA QUE NO EXISTE")
    void shouldThrowWhenActualizarReservaNotFound(){
        when(reservaRepo.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> reservaService.actualizar(99L, rRequest));

        assertEquals("Reserva con id 99 no encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UNA RESERVA")
    void shouldEliminarReserva(){
        when(reservaRepo.existsById(1L)).thenReturn(true);

        reservaService.eliminar(1L);

        verify(reservaRepo, times(1)).existsById(1L);
        verify(reservaRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION AL ELIMINAR RESERVA QUE NO EXISTE")
    void shouldThrowWhenEliminarReservaNotFound(){
        when(reservaRepo.existsById(99L)).thenReturn(false);

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> reservaService.eliminar(99L));

        assertEquals("Reserva con id 99 no encontrada", ex.getMessage());
        verify(reservaRepo, never()).deleteById(99L);
    }
}
