package com.fitchain.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorarioDTO {
    private Long id;
    private LocalTime horaIniTurno;
    private LocalTime horaFinTurno;
}
