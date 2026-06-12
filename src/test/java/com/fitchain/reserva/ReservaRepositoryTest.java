package com.fitchain.reserva;

import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("PRUEBAS UNITARIAS DEL REPOSITORY DE RESERVA")
public class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository repo;

    @Autowired
    private TestEntityManager em;

    private Reserva crearReserva(Long clienteId, Long horarioId, String actividad, String estado) {
        Reserva r = new Reserva();
        r.setClienteId(clienteId);
        r.setHorarioId(horarioId);
        r.setActividad(actividad);
        r.setFecha(LocalDate.now());
        r.setHora(LocalTime.of(9, 0));
        r.setEstado(estado);
        return em.persistAndFlush(r);
    }

    @Test
    @DisplayName("DEBE ENCONTRAR UNA RESERVA POR ID")
    void findById_ShouldReturnReserva(){
        Reserva r = crearReserva(2L, 1L, "YOGA", "PENDIENTE");

        Optional<Reserva> result = repo.findById(r.getId());

        assertTrue(result.isPresent());
        assertEquals("YOGA", result.get().getActividad());
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI RESERVA NO EXISTE")
    void findById_ShouldReturnEmpty(){
        Optional<Reserva> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR RESERVAS POR CLIENTE ID")
    void findByClienteId_ShouldReturnReservas(){
        crearReserva(2L, 1L, "YOGA", "PENDIENTE");
        crearReserva(2L, 1L, "PILATES", "CONFIRMADA");
        crearReserva(3L, 1L, "CROSSFIT", "PENDIENTE");

        List<Reserva> result = repo.findByClienteId(2L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI CLIENTE NO TIENE RESERVAS")
    void findByClienteId_ShouldReturnEmpty(){
        List<Reserva> result = repo.findByClienteId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR RESERVAS POR ESTADO")
    void findByEstado_ShouldReturnReservas(){
        crearReserva(2L, 1L, "YOGA", "PENDIENTE");
        crearReserva(3L, 1L, "PILATES", "PENDIENTE");
        crearReserva(4L, 1L, "CROSSFIT", "CONFIRMADA");

        List<Reserva> result = repo.findByEstado("PENDIENTE");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getEstado().equals("PENDIENTE")));
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI NO HAY RESERVAS CON ESE ESTADO")
    void findByEstado_ShouldReturnEmpty(){
        List<Reserva> result = repo.findByEstado("CANCELADA");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE GUARDAR UNA RESERVA")
    void save_ShouldPersistReserva(){
        Reserva r = new Reserva();
        r.setClienteId(2L);
        r.setHorarioId(1L);
        r.setActividad("YOGA");
        r.setFecha(LocalDate.now());
        r.setHora(LocalTime.of(9, 0));
        r.setEstado("PENDIENTE");

        Reserva saved = repo.save(r);

        assertNotNull(saved.getId());
        assertEquals("YOGA", saved.getActividad());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UNA RESERVA")
    void delete_ShouldRemoveReserva(){
        Reserva r = crearReserva(2L, 1L, "YOGA", "PENDIENTE");
        Long id = r.getId();

        repo.deleteById(id);
        em.flush();

        assertFalse(repo.findById(id).isPresent());
    }
}
