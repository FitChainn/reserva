package com.fitchain.reserva;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitchain.reserva.dto.ReservaResponseDTO;
import com.fitchain.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

@WebMvcTest
public class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void GET_reservaById_returns200() throws Exception{
        // la cagué pq necesito todo el deto completo con los otros dtos? q vienen de los otro microservicios

        //ReservaResponseDTO rDto = new ReservaResponseDTO(1L, "YOGA", LocalDate.now(), LocalTime.of(10, 0), "CONFIRMADA", , );

    }

}
