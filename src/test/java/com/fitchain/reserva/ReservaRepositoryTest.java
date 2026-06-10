package com.fitchain.reserva;

import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest

public class ReservaRepositoryTest {
    @Autowired
    private ReservaRepository repo;

    @Autowired
    private TestEntityManager em; // helper jpa


    @Test
    void findById_ShouldReturnReserva(){
        Reserva r = new Reserva();
        r.setId(1L); r.setActividad("YOGA");
        em.persistAndFlush(r);

        Optional<Reserva> result = repo.findById(r.getId());
        assertTrue(result.isPresent());
        assertEquals("YOGA", result.get().getActividad());
    }

}
