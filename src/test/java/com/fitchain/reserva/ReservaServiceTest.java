package com.fitchain.reserva;

import com.fitchain.reserva.client.ClienteClient;
import com.fitchain.reserva.client.HorarioClient;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.exception.RecursoNoEncontradoException;
import com.fitchain.reserva.exception.ServicioNoDisponibleException;
import com.fitchain.reserva.model.EstadoReserva;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE RESERVAS")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private HorarioClient horarioClient;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private ClienteDTO cliente;
    private HorarioDTO horario;

    @BeforeEach
    void setUp() {
        reserva = new Reserva(1L, 1L, 1L, "YOGA", LocalDate.now().plusDays(1), LocalTime.of(9, 0), EstadoReserva.PENDIENTE, true);
        cliente = new ClienteDTO(1L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
        horario = new HorarioDTO(1L, LocalTime.of(9, 0), LocalTime.of(10, 0));
    }

    @Test
    @DisplayName("CREAR: TODA RESERVA NUEVA QUEDA PENDIENTE")
    void crear_quedaPendiente() {
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, 1L, "YOGA", LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        when(clienteClient.obtenerPorId(1L)).thenReturn(cliente);
        when(horarioClient.obtenerPorId(1L)).thenReturn(horario);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> {
            Reserva r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReservaResponseDTO resultado = reservaService.crear(dto);

        assertEquals(10L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("JUANITO PEREZ", resultado.getCliente().getNombre());
    }

    @Test
    @DisplayName("CREAR: SI EL CLIENTE NO EXISTE, NO SE GUARDA NADA")
    void crear_clienteNoExiste() {
        ReservaRequestDTO dto = new ReservaRequestDTO(99L, 1L, "YOGA", LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        when(clienteClient.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Cliente", 99L));

        assertThrows(RecursoNoEncontradoException.class, () -> reservaService.crear(dto));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("OBTENER POR ID: SI NO EXISTE LANZA RecursoNoEncontradoException")
    void obtenerPorId_noExiste() {
        when(reservaRepository.findByIdAndActivoTrue(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> reservaService.obtenerPorId(99L));
        assertEquals("RESERVA CON EL ID 99 NO ENCONTRADO", ex.getMessage());
    }

    @Test
    @DisplayName("LISTAR: SI EL MICROSERVICIO DE CLIENTES ESTÁ CAÍDO, LA LISTA SALE IGUAL")
    void listar_clienteCaido() {
        when(reservaRepository.findByActivoTrue()).thenReturn(List.of(reserva));
        when(clienteClient.obtenerPorId(1L)).thenThrow(new ServicioNoDisponibleException("clientes"));
        when(horarioClient.obtenerPorId(1L)).thenReturn(horario);

        List<ReservaResponseDTO> resultado = reservaService.obtenerTodas();

        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).getCliente());
        assertNotNull(resultado.get(0).getHorario());
    }

    @Test
    @DisplayName("CAMBIAR ESTADO: ACTUALIZA SOLO EL ESTADO")
    void cambiarEstado() {
        when(reservaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        ReservaResponseDTO resultado = reservaService.cambiarEstado(1L, EstadoReserva.CONFIRMADA);

        assertEquals("CONFIRMADA", resultado.getEstado());
        assertEquals("YOGA", resultado.getActividad());
    }

    @Test
    @DisplayName("ELIMINAR: ES BORRADO LÓGICO (activo = false), NO BORRA LA FILA")
    void eliminar_borradoLogico() {
        when(reservaRepository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(reserva));

        reservaService.eliminar(1L);

        assertFalse(reserva.isActivo());
        verify(reservaRepository).save(reserva);
        verify(reservaRepository, never()).deleteById(any());
    }
}
