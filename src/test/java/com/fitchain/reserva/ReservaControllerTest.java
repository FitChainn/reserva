package com.fitchain.reserva;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitchain.reserva.config.SecurityConfig;
import com.fitchain.reserva.controller.ReservaController;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.filter.RolHeaderFilter;
import com.fitchain.reserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
@DisplayName("PRUEBAS UNITARIAS DEL CONTROLLER DE RESERVAS")
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservaResponseDTO rResponse;
    private ReservaRequestDTO rRequest;

    @BeforeEach
    void setUp() {
        ClienteDTO cli = new ClienteDTO(1L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
        HorarioDTO horario = new HorarioDTO(1L, LocalTime.of(9, 0), LocalTime.of(10, 0));

        rResponse = new ReservaResponseDTO(1L, "YOGA", LocalDate.of(2025, 11, 6), LocalTime.of(9, 0), "PENDIENTE", cli, horario);
        rRequest = new ReservaRequestDTO(1L, 1L, "YOGA", LocalDate.of(2025, 11, 6), LocalTime.of(9, 0));
    }

    @Test
    @DisplayName("DEBE RETORNAR TODAS LAS RESERVAS")
    void GET_obtenerReservas() throws Exception {
        when(reservaService.obtenerTodas()).thenReturn(List.of(rResponse));

        mockMvc.perform(get("/v1/reservas")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].actividad").value("YOGA"));
    }

    @Test
    @DisplayName("DEBE CREAR UNA RESERVA (201)")
    void POST_crear201() throws Exception {
        when(reservaService.crear(any(ReservaRequestDTO.class))).thenReturn(rResponse);

        mockMvc.perform(post("/v1/reservas")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.actividad").value("YOGA"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.cliente.nombre").value("JUANITO PEREZ"))
                .andExpect(jsonPath("$.horario.id").value(1L));
    }

    @Test
    @DisplayName("DEBE RETORNAR ERROR 400 AL CREAR RESERVA CON DATOS INVALIDOS")
    void POST_validation_crear() throws Exception {
        ReservaRequestDTO reqInvalido = new ReservaRequestDTO();

        mockMvc.perform(post("/v1/reservas")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DEBE OBTENER UNA RESERVA POR ID")
    void GET_obtenerPorId() throws Exception {
        when(reservaService.obtenerPorId(1L)).thenReturn(rResponse);

        mockMvc.perform(get("/v1/reservas/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.actividad").value("YOGA"))
                .andExpect(jsonPath("$.cliente.run").value("12.123.431-2"));
    }

    @Test
    @DisplayName("DEBE RETORNAR 404 SI RESERVA NO EXISTE")
    void GET_obtenerIdNotFound() throws Exception {
        when(reservaService.obtenerPorId(99L)).thenThrow(new NoSuchElementException("RESERVA CON EL ID 99 NO ENCONTRADA"));

        mockMvc.perform(get("/v1/reservas/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO ENCONTRADA"));
    }

    @Test
    @DisplayName("DEBE OBTENER RESERVAS POR CLIENTE")
    void GET_obtenerPorCliente() throws Exception {
        when(reservaService.obtenerPorCliente(1L)).thenReturn(List.of(rResponse));

        mockMvc.perform(get("/v1/reservas/cliente/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cliente.id").value(1L));
    }

    @Test
    @DisplayName("DEBE OBTENER RESERVAS POR ESTADO")
    void GET_obtenerPorEstado() throws Exception {
        when(reservaService.obtenerPorEstado("PENDIENTE")).thenReturn(List.of(rResponse));

        mockMvc.perform(get("/v1/reservas/estado/PENDIENTE")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("DEBE ACTUALIZAR UNA RESERVA")
    void PUT_actualizar() throws Exception {
        ReservaResponseDTO actualizado = new ReservaResponseDTO();
        actualizado.setId(1L);
        actualizado.setActividad("PILATES");
        actualizado.setFecha(LocalDate.of(2025, 6, 15));
        actualizado.setHora(LocalTime.of(10, 0));
        actualizado.setEstado("CONFIRMADA");
        actualizado.setCliente(rResponse.getCliente());
        actualizado.setHorario(rResponse.getHorario());

        rRequest.setActividad("PILATES");

        when(reservaService.actualizar(eq(1L), any(ReservaRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/v1/reservas/1")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actividad").value("PILATES"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    @DisplayName("DEBE RETORNAR 404 AL ACTUALIZAR RESERVA QUE NO EXISTE")
    void PUT_actualizar_noExiste() throws Exception {
        when(reservaService.actualizar(eq(99L), any(ReservaRequestDTO.class)))
                .thenThrow(new NoSuchElementException("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"));

        mockMvc.perform(put("/v1/reservas/99")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"));
    }

    @Test
    @DisplayName("DEBE ELIMINAR UNA RESERVA")
    void DELETE_eliminar() throws Exception {
        doNothing().when(reservaService).eliminar(1L);

        mockMvc.perform(delete("/v1/reservas/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(reservaService, times(1)).eliminar(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR 404 AL ELIMINAR RESERVA QUE NO EXISTE")
    void DELETE_eliminar_noExiste() throws Exception {
        doThrow(new NoSuchElementException("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"))
                .when(reservaService).eliminar(99L);

        mockMvc.perform(delete("/v1/reservas/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"));
    }
}