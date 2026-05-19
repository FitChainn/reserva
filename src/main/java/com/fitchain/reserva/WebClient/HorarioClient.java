package com.fitchain.reserva.WebClient;

import com.fitchain.reserva.dto.HorarioDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class HorarioClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${horario.service.url}")
    private String horarioServiceUrl;

    public HorarioDTO obtenerHorarioPorId(Long horarioId) {
        log.info("Consultando horario con id {} en microservicio Horario", horarioId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(horarioServiceUrl + "/v1/horarios/" + horarioId)
                    .retrieve()
                    .bodyToMono(HorarioDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Horario con id {} no encontrado", horarioId);
            throw new NoSuchElementException("Horario con id " + horarioId + " no encontrado");
        } catch (Exception e) {
            log.error("Error al conectar con microservicio Horario: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de horarios");
        }
    }
}
