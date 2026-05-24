package com.huellitas.vetcare.model;

import java.time.LocalDate;

public class RegistroClinico {
    private final int id;
    private final int mascotaId;
    private LocalDate fecha;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;

    public RegistroClinico(int id, int mascotaId, LocalDate fecha, String diagnostico, String tratamiento, String observaciones) {
        this.id = id;
        this.mascotaId = mascotaId;
        this.fecha = fecha;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public int getMascotaId() {
        return mascotaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
