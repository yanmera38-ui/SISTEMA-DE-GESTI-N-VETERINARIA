package com.huellitas.vetcare.model;

public class Mascota {
    private final int id;
    private final int clienteId;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;

    public Mascota(int id, int clienteId, String nombre, String especie, String raza, int edad) {
        this.id = id;
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return getId() + " - " + nombre;
    }
}
