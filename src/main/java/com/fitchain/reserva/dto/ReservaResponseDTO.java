package com.fitchain.reserva.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaResponseDTO {

    private Long id;
    private String actividad;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;

    private ClienteDTO cliente;
    private HorarioDTO horario;
}
