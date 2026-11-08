package com.fitchain.reserva.config;

import com.fitchain.reserva.model.EstadoReserva;
import com.fitchain.reserva.model.Reserva;
import com.fitchain.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ReservaRepository reservaRepository;

    @Override
    public void run(String... args) {
        if (reservaRepository.count() > 0) {
            log.info(">>> DataInitializer: la BD ya tiene datos, se omite la carga inicial.");
            return;
        }

        LocalDate hoy = LocalDate.now();
        reservaRepository.saveAll(List.of(
                new Reserva(null, 1L, 1L, "SPINNING", hoy.plusDays(1), LocalTime.of(10, 0), EstadoReserva.CONFIRMADA, true),
                new Reserva(null, 2L, 1L, "YOGA", hoy.plusDays(2), LocalTime.of(9, 0), EstadoReserva.PENDIENTE, true),
                new Reserva(null, 3L, 2L, "MUSCULACION", hoy.plusDays(1), LocalTime.of(18, 0), EstadoReserva.PENDIENTE, true),
                new Reserva(null, 1L, 2L, "SPINNING", hoy.minusDays(1), LocalTime.of(10, 0), EstadoReserva.CANCELADA, true)
        ));
        log.info(">>> DataInitializer: {} reservas insertadas.", reservaRepository.count());
    }
}
