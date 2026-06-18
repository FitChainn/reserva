package com.fitchain.reserva;

import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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

    @BeforeEach //idea
    void limpiarBDenMemoria() {
        repo.deleteAll();
        em.flush();
    }

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
    void findById_ShouldReturnReserva() {
        Reserva r = crearReserva(1L, 1L, "YOGA", "PENDIENTE");

        Optional<Reserva> result = repo.findById(r.getId());

        assertTrue(result.isPresent());
        assertEquals("YOGA", result.get().getActividad());
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI RESERVA NO EXISTE")
    void findById_ShouldReturnEmpty() {
        Optional<Reserva> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR TODAS LAS RESERVAS")
    void findAll_ShouldReturnAllReservas() {
        crearReserva(1L, 1L, "YOGA", "PENDIENTE");
        crearReserva(2L, 2L, "PILATES", "CONFIRMADA");

        List<Reserva> lista = repo.findAll();

        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 2);
    }

    @Test
    @DisplayName("DEBE GUARDAR UNA RESERVA")
    void save_ShouldPersistReserva() {
        Reserva r = new Reserva();
        r.setClienteId(1L);
        r.setHorarioId(1L);
        r.setActividad("SPINNING");
        r.setFecha(LocalDate.now());
        r.setHora(LocalTime.of(10, 0));
        r.setEstado("PENDIENTE");

        Reserva saved = repo.save(r);

        assertNotNull(saved.getId());
        assertEquals("SPINNING", saved.getActividad());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR RESERVAS POR CLIENTE")
    void findByClienteId_ShouldReturnReservas() {
        crearReserva(5L, 1L, "YOGA", "PENDIENTE");
        crearReserva(5L, 2L, "PILATES", "CONFIRMADA");

        List<Reserva> result = repo.findByClienteId(5L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> r.getClienteId().equals(5L)));
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI CLIENTE NO TIENE RESERVAS")
    void findByClienteId_ShouldReturnEmpty() {
        List<Reserva> result = repo.findByClienteId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR RESERVAS POR ESTADO")
    void findByEstado_ShouldReturnReservas() {
        crearReserva(1L, 1L, "YOGA", "PENDIENTE");
        crearReserva(2L, 2L, "PILATES", "PENDIENTE");

        List<Reserva> result = repo.findByEstado("PENDIENTE");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(r -> r.getEstado().equals("PENDIENTE")));
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI NO HAY RESERVAS CON ESE ESTADO")
    void findByEstado_ShouldReturnEmpty() {
        List<Reserva> result = repo.findByEstado("CANCELADA");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UNA RESERVA")
    void delete_ShouldRemoveReserva() {
        Reserva r = crearReserva(1L, 1L, "YOGA", "PENDIENTE");
        Long id = r.getId();

        repo.deleteById(id);
        em.flush();

        assertFalse(repo.findById(id).isPresent());
    }
}