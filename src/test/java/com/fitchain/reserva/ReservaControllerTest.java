package com.fitchain.reserva;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.fitchain.reserva.config.SecurityConfig;
import com.fitchain.reserva.controller.ReservaController;
import com.fitchain.reserva.filter.RolHeaderFilter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//test: a mano
@WebMvcTest(ReservaController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
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
    void setUp(){
        ClienteDTO cli = new ClienteDTO(1L,"JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995,5,10), 2L, 3L);

        HorarioDTO horario = new HorarioDTO(1L, LocalTime.of(9,0), LocalTime.of(10,0));

        rResponse = new ReservaResponseDTO(1L, "YOGA", LocalDate.of(2025,11,6), LocalTime.of(9,0),"PENDIENTE", cli, horario);
        rRequest = new ReservaRequestDTO(1L, 1L, "YOGA", LocalDate.of(2025,11,6), LocalTime.of(9,0));
    }

    @Test
    void Post_crear201() throws Exception{
        when(reservaService.crear(any(ReservaRequestDTO.class))).thenReturn(rResponse);

        mockMvc.perform(post("/v1/reservas")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rRequest)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.actividad").value("YOGA"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.cliente.nombre").value("JUANITO PEREZ"))
                .andExpect(jsonPath("$.horario.id").value(1L));

    }

    @Test//validacion de campos // error 400
    void POST_validation_crear() throws Exception{
        ReservaRequestDTO reqInvalido = new ReservaRequestDTO();

        mockMvc.perform(post("/v1/reservas")
                .header("X-User-Rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reqInvalido)))
                .andExpect(status().isBadRequest());
    }

    // primr get
    @Test // 200
    void Get_obtenerReservas() throws Exception{
        when(reservaService.obtenerTodas()).thenReturn(List.of(rResponse));

        mockMvc.perform(get("/v1/reservas")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].actividad").value("YOGA"));
    }

    @Test
    void Get_obtenerPorId() throws Exception{
        when(reservaService.obtenerPorId(1L)).thenReturn(rResponse);

        mockMvc.perform(get("/v1/reservas/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.actividad").value("YOGA"))
                .andExpect(jsonPath("$.cliente.run").value("12.123.431-2"));
    }

    @Test
    void Get_obtenerIdNotFound() throws Exception{
        when(reservaService.obtenerPorId(99L)).thenThrow(new NoSuchElementException("RESERVA CON EL ID 99 NO ENCONTRADA"));

        mockMvc.perform(get("/v1/reservas/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO ENCONTRADA"));
    }

    //get con el id del cliente
    @Test // lamayoría debe retornar un ok un 200
    void Get_obtenerPorCliente() throws Exception{
        when(reservaService.obtenerPorCliente(1L)).thenReturn(List.of(rResponse));

        mockMvc.perform(get("/v1/reservas/cliente/1")
                        .header("X-User-Rol", "ADMIN")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].cliente.id").value(1L));
    }

    //obtener por estado
    @Test
    void GET_obtenerPorEstado_returns200() throws Exception {
        when(reservaService.obtenerPorEstado("PENDIENTE")).thenReturn(List.of(rResponse));

        mockMvc.perform(get("/v1/reservas/estado/PENDIENTE")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void PUT_actualizar() throws Exception{
        ReservaResponseDTO actualizado= new ReservaResponseDTO();

        actualizado.setId(1L);
        actualizado.setActividad("PILATES");
        actualizado.setFecha(LocalDate.of(2025,6,15));
        actualizado.setHora(LocalTime.of(10,0));
        actualizado.setEstado("CONFIRMADA");
        actualizado.setCliente(rResponse.getCliente());
        actualizado.setHorario(rResponse.getHorario());

        rRequest.setActividad("PILATES");

        when(reservaService.actualizar(eq(1L), any(ReservaRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/v1/reservas/1")
                .header("X-User-Rol", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rRequest))).andExpect(status().isOk()).andExpect(jsonPath("$.actividad").value("PILATES"))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void PUT_actualizar_noExiste() throws Exception{
        when(reservaService
                .actualizar(eq(99L), any(ReservaRequestDTO.class)))
                .thenThrow(new NoSuchElementException("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"));

        mockMvc.perform(put("/v1/reservas/99")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"));
    }

    @Test // 204
    void DELETE_eliminar() throws Exception{
        mockMvc.perform(delete("/v1/reservas/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(reservaService).eliminar(1L);
    }

    @Test // 404
    void DELETE_eliminar_noExiste() throws Exception{
        doThrow(new NoSuchElementException("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"))
                .when(reservaService).eliminar(99L);

        mockMvc.perform(delete("/v1/reservas/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO HA SIDO ENCONTRADA"));
    }
}
