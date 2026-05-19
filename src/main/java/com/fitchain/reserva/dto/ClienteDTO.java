package com.fitchain.reserva.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String run;
    private LocalDate fechaNacimiento;
    private Long entrenadorId;
    private Long establecimientoId;
}
