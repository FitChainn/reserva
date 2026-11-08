package com.fitchain.reserva;

import com.fitchain.reserva.config.SecurityConfig;
import com.fitchain.reserva.controller.ReservaController;
import com.fitchain.reserva.dto.ClienteDTO;
import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.dto.ReservaRequestDTO;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.exception.RecursoNoEncontradoException;
import com.fitchain.reserva.filter.RolHeaderFilter;
import com.fitchain.reserva.model.EstadoReserva;
import com.fitchain.reserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
@DisplayName("PRUEBAS UNITARIAS DEL CONTROLLER DE RESERVAS")
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservaService reservaService;

    private ReservaResponseDTO respuesta;
    private ReservaRequestDTO request;

    @BeforeEach
    void setUp() {
        ClienteDTO cliente = new ClienteDTO(1L, "JUANITO PEREZ", "12.123.431-2", LocalDate.of(1995, 5, 10), 2L, 3L);
        HorarioDTO horario = new HorarioDTO(1L, LocalTime.of(9, 0), LocalTime.of(10, 0));
        LocalDate manana = LocalDate.now().plusDays(1);

        respuesta = new ReservaResponseDTO(1L, "YOGA", manana, LocalTime.of(9, 0), "PENDIENTE", cliente, horario);
        request = new ReservaRequestDTO(1L, 1L, "YOGA", manana, LocalTime.of(9, 0));
    }

    @Test
    @DisplayName("GET: ADMIN PUEDE LISTAR LAS RESERVAS (200)")
    void listar_admin() throws Exception {
        when(reservaService.obtenerTodas()).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/api/v1/reservas").header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].actividad").value("YOGA"));
    }

    @Test
    @DisplayName("GET: SIN ROL RESPONDE 403 (YA NO ENTRA COMO ADMIN)")
    void listar_sinRol() throws Exception {
        mockMvc.perform(get("/api/v1/reservas"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").value("NO TIENES PERMISOS PARA ESTA ACCIÓN"));

        verify(reservaService, never()).obtenerTodas();
    }

    @Test
    @DisplayName("GET: CLIENTE NO PUEDE LISTAR TODAS LAS RESERVAS (403)")
    void listar_cliente() throws Exception {
        mockMvc.perform(get("/api/v1/reservas").header("X-User-Rol", "CLIENTE"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST: CREA UNA RESERVA (201)")
    void crear() throws Exception {
        when(reservaService.crear(any(ReservaRequestDTO.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/reservas")
                        .header("X-User-Rol", "CLIENTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.cliente.nombre").value("JUANITO PEREZ"));
    }

    @Test
    @DisplayName("POST: DATOS INVÁLIDOS RESPONDE 400 CON LOS CAMPOS QUE FALLARON")
    void crear_invalido() throws Exception {
        mockMvc.perform(post("/api/v1/reservas")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.clienteId").value("EL ID DEL CLIENTE ES OBLIGATORIO"))
                .andExpect(jsonPath("$.campos.actividad").value("LA ACTIVIDAD ES OBLIGATORIA"));

        verify(reservaService, never()).crear(any());
    }

    @Test
    @DisplayName("GET /{id}: RESERVA QUE NO EXISTE RESPONDE 404")
    void obtenerPorId_noExiste() throws Exception {
        when(reservaService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Reserva", 99L));

        mockMvc.perform(get("/api/v1/reservas/99").header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("RESERVA CON EL ID 99 NO ENCONTRADO"));
    }

    @Test
    @DisplayName("GET /estado/{estado}: ESTADO INVENTADO RESPONDE 400")
    void obtenerPorEstado_invalido() throws Exception {
        mockMvc.perform(get("/api/v1/reservas/estado/INVENTADO").header("X-User-Rol", "ADMIN"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT: ACTUALIZA UNA RESERVA (200)")
    void actualizar() throws Exception {
        when(reservaService.actualizar(eq(1L), any(ReservaRequestDTO.class))).thenReturn(respuesta);

        mockMvc.perform(put("/api/v1/reservas/1")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actividad").value("YOGA"));
    }

    @Test
    @DisplayName("PATCH: CAMBIA EL ESTADO (200)")
    void cambiarEstado() throws Exception {
        respuesta.setEstado("CONFIRMADA");
        when(reservaService.cambiarEstado(1L, EstadoReserva.CONFIRMADA)).thenReturn(respuesta);

        mockMvc.perform(patch("/api/v1/reservas/1/estado")
                        .param("estado", "CONFIRMADA")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    @DisplayName("DELETE: ELIMINA UNA RESERVA (204)")
    void eliminar() throws Exception {
        mockMvc.perform(delete("/api/v1/reservas/1").header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(reservaService).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE: RESERVA QUE NO EXISTE RESPONDE 404")
    void eliminar_noExiste() throws Exception {
        doThrow(new RecursoNoEncontradoException("Reserva", 99L)).when(reservaService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/reservas/99").header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound());
    }
}
