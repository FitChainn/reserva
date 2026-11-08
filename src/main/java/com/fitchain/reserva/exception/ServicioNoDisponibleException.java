package com.fitchain.reserva.exception;

public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String servicio) {
        super("NO SE PUDO CONECTAR CON EL MICROSERVICIO DE " + servicio.toUpperCase());
    }
}
