package com.fitchain.reserva.client;

import com.fitchain.reserva.dto.ClienteDTO;
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
public class ClienteClient {

    private final RestClient restClient;

    public ClienteClient(RestClient.Builder builder, @Value("${cliente.service.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    public ClienteDTO obtenerPorId(Long clienteId) {
        log.info("CONSULTANDO CLIENTE CON ID {} EN EL MICROSERVICIO CLIENTE", clienteId);
        try {
            return restClient.get()
                    .uri("/v1/clientes/{id}", clienteId)
                    .retrieve()
                    .body(ClienteDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Cliente", clienteId);
        } catch (RestClientException e) {
            log.error("ERROR AL CONECTAR CON EL MICROSERVICIO CLIENTE: {}", e.getMessage());
            throw new ServicioNoDisponibleException("clientes");
        }
    }
}
