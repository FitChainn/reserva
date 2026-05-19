package com.fitchain.reserva.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HorarioDTO {
    private Long id;
    private LocalTime horaIniTurno;
    private LocalTime horaFinTurno;
}
