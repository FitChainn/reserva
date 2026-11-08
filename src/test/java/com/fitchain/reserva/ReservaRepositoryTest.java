package com.fitchain.reserva;

import com.fitchain.reserva.model.EstadoReserva;
import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("PRUEBAS UNITARIAS DEL REPOSITORY DE RESERVAS")
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository repo;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void limpiar() {
        repo.deleteAll();
        em.flush();
    }

    private Reserva crear(String actividad, EstadoReserva estado, boolean activo) {
        Reserva r = new Reserva(null, 1L, 1L, actividad, LocalDate.now(), LocalTime.of(9, 0), estado, activo);
        return em.persistAndFlush(r);
    }

    @Test
    @DisplayName("findByActivoTrue NO DEVUELVE LAS RESERVAS BORRADAS")
    void findByActivoTrue() {
        crear("YOGA", EstadoReserva.PENDIENTE, true);
        crear("SPINNING", EstadoReserva.PENDIENTE, false);

        List<Reserva> resultado = repo.findByActivoTrue();

        assertEquals(1, resultado.size());
        assertEquals("YOGA", resultado.get(0).getActividad());
    }

    @Test
    @DisplayName("findByIdAndActivoTrue NO ENCUENTRA UNA RESERVA BORRADA")
    void findByIdAndActivoTrue_borrada() {
        Reserva borrada = crear("YOGA", EstadoReserva.PENDIENTE, false);

        assertTrue(repo.findByIdAndActivoTrue(borrada.getId()).isEmpty());
    }

    @Test
    @DisplayName("findByEstadoAndActivoTrue FILTRA POR ESTADO")
    void findByEstado() {
        crear("YOGA", EstadoReserva.PENDIENTE, true);
        crear("SPINNING", EstadoReserva.CONFIRMADA, true);
        crear("PILATES", EstadoReserva.CONFIRMADA, true);

        assertEquals(2, repo.findByEstadoAndActivoTrue(EstadoReserva.CONFIRMADA).size());
    }
}
