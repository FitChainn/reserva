package com.fitchain.reserva.client;

import com.fitchain.reserva.dto.HorarioDTO;
import com.fitchain.reserva.exception.RecursoNoEncontradoException;
import com.fitchain.reserva.exception.ServicioNoDisponibleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class HorarioClient {

    private final RestClient restClient;

    public HorarioClient(RestClient.Builder builder, @Value("${horario.service.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    public HorarioDTO obtenerPorId(Long horarioId) {
        log.info("CONSULTANDO HORARIO CON ID {} EN EL MICROSERVICIO HORARIO", horarioId);
        try {
            return restClient.get()
                    .uri("/v1/horarios/{id}", horarioId)
                    .retrieve()
                    .body(HorarioDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Horario", horarioId);
        } catch (RestClientException e) {
            log.error("ERROR AL CONECTAR CON EL MICROSERVICIO HORARIO: {}", e.getMessage());
            throw new ServicioNoDisponibleException("horarios");
        }
    }
}
