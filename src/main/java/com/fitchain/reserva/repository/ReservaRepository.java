package com.fitchain.reserva.repository;

import com.fitchain.reserva.model.EstadoReserva;
import com.fitchain.reserva.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByActivoTrue();

    Optional<Reserva> findByIdAndActivoTrue(Long id);

    List<Reserva> findByClienteIdAndActivoTrue(Long clienteId);

    List<Reserva> findByEstadoAndActivoTrue(EstadoReserva estado);
}
