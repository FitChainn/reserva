package com.fitchain.reserva;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE RESERVA")
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepo;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva r;

    @BeforeEach
    void setUp(){
        r = new Reserva();
        r.setId(1L);
        r.setClienteId(2L);
        r.setHorarioId(1L);
        r.setActividad("YOGA");
        r.setFecha(LocalDate.now());
        r.setHora(LocalTime.of(9, 0));
        r.setEstado("PENDIENTE");
    }

    @Test
    @DisplayName("DEBE RETORNAR UNA RESERVA POR ID")
    void shouldReturnReservaById(){
        //GIVEN

        //WHEN
        when(reservaRepo
                .findById(1L))
                .thenReturn
                        (Optional.of(r));


        ReservaResponseDTO dto = reservaService.obtenerPorId(r.getId());
        assertNotNull(dto);
        assertEquals("YOGA", dto.getActividad());
        verify(reservaRepo,times(1)).findById(1L);
    }


}

