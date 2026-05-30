package com.huellitas.vetcare.model;

public class Cita {
    private int id;
    private int mascotaId;
    private String fecha;
    private String hora;
    private String motivo;
    private String estado;

    public Cita(int id, int mascotaId, String fecha, String hora, String motivo, String estado) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
