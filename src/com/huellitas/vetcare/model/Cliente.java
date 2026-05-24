package com.huellitas.vetcare.model;

public class Cliente extends Persona {
    private String direccion;

    public Cliente(int id, String nombre, String telefono, String direccion) {
        super(id, nombre, telefono);
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return getId() + " - " + getNombre();
    }
}
