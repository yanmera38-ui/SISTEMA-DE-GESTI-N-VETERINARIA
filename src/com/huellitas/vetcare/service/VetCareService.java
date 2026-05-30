package com.huellitas.vetcare.service;

import com.huellitas.vetcare.model.Cita;
import com.huellitas.vetcare.model.Cliente;
import com.huellitas.vetcare.model.Mascota;
import com.huellitas.vetcare.model.RegistroClinico;
import com.huellitas.vetcare.persistence.CsvDatabase;

import java.io.IOException;
import java.util.ArrayList;

public class VetCareService {
    private ArrayList<Cliente> clientes = new ArrayList<Cliente>();
    private ArrayList<Mascota> mascotas = new ArrayList<Mascota>();
    private ArrayList<Cita> citas = new ArrayList<Cita>();
    private ArrayList<RegistroClinico> registros = new ArrayList<RegistroClinico>();
    private CsvDatabase database = new CsvDatabase();

    public VetCareService() {
        cargar();
    }

    public Cliente registrarCliente(String nombre, String telefono, String direccion) {
        Cliente cliente = new Cliente(siguienteIdClientes(), validarTexto(nombre, "nombre"), telefono.trim(), direccion.trim());
        clientes.add(cliente);
        guardarSilencioso();
        return cliente;
    }

    public Mascota registrarMascota(int clienteId, String nombre, String especie, String raza, int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa.");
        }
        Mascota mascota = new Mascota(siguienteIdMascotas(), clienteId, validarTexto(nombre, "nombre"), validarTexto(especie, "especie"), raza.trim(), edad);
        mascotas.add(mascota);
        guardarSilencioso();
        return mascota;
    }

    public Cita agendarCita(int mascotaId, String fecha, String hora, String motivo) {
        Cita cita = new Cita(siguienteIdCitas(), mascotaId, validarTexto(fecha, "fecha"), validarTexto(hora, "hora"), validarTexto(motivo, "motivo"), "PROGRAMADA");
        citas.add(cita);
        guardarSilencioso();
        return cita;
    }

    public RegistroClinico agregarRegistroClinico(int mascotaId, String fecha, String diagnostico, String tratamiento, String observaciones) {
        RegistroClinico registro = new RegistroClinico(
                siguienteIdRegistros(),
                mascotaId,
                validarTexto(fecha, "fecha"),
                validarTexto(diagnostico, "diagnostico"),
                tratamiento.trim(),
                observaciones.trim()
        );
        registros.add(registro);
        guardarSilencioso();
        return registro;
    }

    public void actualizarEstadoCita(int citaId, String estado) {
        Cita cita = buscarCita(citaId);
        if (cita == null) {
            throw new IllegalArgumentException("No existe una cita con el ID indicado.");
        }
        cita.setEstado(estado);
        guardarSilencioso();
    }

    public ArrayList<Cliente> getClientes() {
        return new ArrayList<Cliente>(clientes);
    }

    public ArrayList<Mascota> getMascotas() {
        return new ArrayList<Mascota>(mascotas);
    }

    public ArrayList<Cita> getCitas() {
        return new ArrayList<Cita>(citas);
    }

    public ArrayList<RegistroClinico> getRegistros() {
        return new ArrayList<RegistroClinico>(registros);
    }

    public ArrayList<Mascota> getMascotasPorCliente(int clienteId) {
        ArrayList<Mascota> resultado = new ArrayList<Mascota>();
        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
            if (mascota.getClienteId() == clienteId) {
                resultado.add(mascota);
            }
        }
        return resultado;
    }

    public ArrayList<RegistroClinico> getRegistrosPorMascota(int mascotaId) {
        ArrayList<RegistroClinico> resultado = new ArrayList<RegistroClinico>();
        for (int i = 0; i < registros.size(); i++) {
            RegistroClinico registro = registros.get(i);
            if (registro.getMascotaId() == mascotaId) {
                resultado.add(registro);
            }
        }
        return resultado;
    }

    public Cliente buscarCliente(int id) {
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }

    public Mascota buscarMascota(int id) {
        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
            if (mascota.getId() == id) {
                return mascota;
            }
        }
        return null;
    }

    public Cita buscarCita(int id) {
        for (int i = 0; i < citas.size(); i++) {
            Cita cita = citas.get(i);
            if (cita.getId() == id) {
                return cita;
            }
        }
        return null;
    }

    public void guardar() throws IOException {
        database.guardar(clientes, mascotas, citas, registros);
    }

    private void cargar() {
        try {
            CsvDatabase.DataSet dataSet = database.cargar();
            clientes.addAll(dataSet.getClientes());
            mascotas.addAll(dataSet.getMascotas());
            citas.addAll(dataSet.getCitas());
            registros.addAll(dataSet.getRegistros());
        } catch (IOException ex) {
            System.err.println("No fue posible cargar los datos: " + ex.getMessage());
        } catch (RuntimeException ex) {
            System.err.println("No fue posible cargar los datos: " + ex.getMessage());
        }
    }

    private void guardarSilencioso() {
        try {
            guardar();
        } catch (IOException ex) {
            System.err.println("No fue posible guardar los datos: " + ex.getMessage());
        }
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    private int siguienteIdClientes() {
        int mayor = 0;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() > mayor) {
                mayor = clientes.get(i).getId();
            }
        }
        return mayor + 1;
    }

    private int siguienteIdMascotas() {
        int mayor = 0;
        for (int i = 0; i < mascotas.size(); i++) {
            if (mascotas.get(i).getId() > mayor) {
                mayor = mascotas.get(i).getId();
            }
        }
        return mayor + 1;
    }

    private int siguienteIdCitas() {
        int mayor = 0;
        for (int i = 0; i < citas.size(); i++) {
            if (citas.get(i).getId() > mayor) {
                mayor = citas.get(i).getId();
            }
        }
        return mayor + 1;
    }

    private int siguienteIdRegistros() {
        int mayor = 0;
        for (int i = 0; i < registros.size(); i++) {
            if (registros.get(i).getId() > mayor) {
                mayor = registros.get(i).getId();
            }
        }
        return mayor + 1;
    }
}
