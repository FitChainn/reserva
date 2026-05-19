package com.fitchain.reserva.WebClient;

import com.fitchain.reserva.dto.ClienteDTO;
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
public class ClienteClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${cliente.service.url}")
    private String clienteServiceUrl;

    public ClienteDTO obtenerClientePorId(Long clienteId) {
        log.info("Consultando cliente con id {} en microservicio Cliente", clienteId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(clienteServiceUrl + "/v1/clientes/" + clienteId)
                    .retrieve()
                    .bodyToMono(ClienteDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Cliente con id {} no encontrado", clienteId);
            throw new NoSuchElementException("Cliente con id " + clienteId + " no encontrado");
        } catch (Exception e) {
            log.error("Error al conectar con microservicio Cliente: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de clientes");
        }
    }
}
