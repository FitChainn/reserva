package com.fitchain.reserva.config;

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
        if (reservaRepository.count() == 0) {
            log.info("Cargando datos de prueba para Reserva...");

            LocalDate hoy = LocalDate.now();

            Reserva r1 = new Reserva();
            r1.setClienteId(1L);
            r1.setHorarioId(1L);
            r1.setActividad("SPINNING");
            r1.setFecha(hoy.plusDays(1));
            r1.setHora(LocalTime.of(10, 0));
            r1.setEstado("CONFIRMADA");

            Reserva r2 = new Reserva();
            r2.setClienteId(2L);
            r2.setHorarioId(1L);
            r2.setActividad("YOGA");
            r2.setFecha(hoy.plusDays(2));
            r2.setHora(LocalTime.of(9, 0));
            r2.setEstado("PENDIENTE");

            Reserva r3 = new Reserva();
            r3.setClienteId(3L);
            r3.setHorarioId(2L);
            r3.setActividad("MUSCULACION");
            r3.setFecha(hoy.plusDays(1));
            r3.setHora(LocalTime.of(18, 0));
            r3.setEstado("PENDIENTE");

            Reserva r4 = new Reserva();
            r4.setClienteId(1L);
            r4.setHorarioId(2L);
            r4.setActividad("SPINNING");
            r4.setFecha(hoy.minusDays(1));
            r4.setHora(LocalTime.of(10, 0));
            r4.setEstado("CANCELADA");

            reservaRepository.saveAll(List.of(r1, r2, r3, r4));
            log.info("Datos de prueba cargados: {} reservas", reservaRepository.count());
        } else {
            log.info("Ya existen datos en la base de datos, omitiendo inicialización");
        }
    }
}
