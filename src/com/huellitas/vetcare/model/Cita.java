package com.huellitas.vetcare.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cita {
    public enum Estado {
        PROGRAMADA,
        ATENDIDA,
        CANCELADA
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int id;
    private final int mascotaId;
    private LocalDateTime fechaHora;
    private String motivo;
    private Estado estado;

    public Cita(int id, int mascotaId, LocalDateTime fechaHora, String motivo, Estado estado) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getFechaFormateada() {
        return fechaHora.format(FORMATTER);
    }
}
