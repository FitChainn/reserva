package com.fitchain.reserva.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "EL ID DEL CLIENTE ES OBLIGATORIO")
    @Positive(message = "EL ID DEL CLIENTE DEBE SER MAYOR A CERO")
    private Long clienteId;

    @NotNull(message = "EL ID DEL HORARIO ES OBLIGATORIO")
    @Positive(message = "EL ID DEL HORARIO DEBE SER MAYOR A CERO")
    private Long horarioId;

    @NotBlank(message = "LA ACTIVIDAD ES OBLIGATORIA")
    private String actividad;

    @NotNull(message = "LA FECHA ES OBLIGATORIA")
    @FutureOrPresent(message = "LA FECHA NO PUEDE SER PASADA")
    private LocalDate fecha;

    @NotNull(message = "LA HORA ES OBLIGATORIA")
    private LocalTime hora;
}
