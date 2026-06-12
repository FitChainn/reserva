package com.fitchain.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String run;
    private LocalDate fechaNacimiento;
    private Long entrenadorId;
    private Long establecimientoId;
}
