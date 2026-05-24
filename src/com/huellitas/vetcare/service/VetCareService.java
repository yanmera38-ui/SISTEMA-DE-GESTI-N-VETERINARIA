package com.huellitas.vetcare.service;

import com.huellitas.vetcare.model.Cita;
import com.huellitas.vetcare.model.Cliente;
import com.huellitas.vetcare.model.Mascota;
import com.huellitas.vetcare.model.RegistroClinico;
import com.huellitas.vetcare.persistence.CsvDatabase;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VetCareService {
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Mascota> mascotas = new ArrayList<>();
    private final List<Cita> citas = new ArrayList<>();
    private final List<RegistroClinico> registros = new ArrayList<>();
    private final CsvDatabase database = new CsvDatabase();

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

    public Cita agendarCita(int mascotaId, LocalDateTime fechaHora, String motivo) {
        Cita cita = new Cita(siguienteIdCitas(), mascotaId, fechaHora, validarTexto(motivo, "motivo"), Cita.Estado.PROGRAMADA);
        citas.add(cita);
        guardarSilencioso();
        return cita;
    }

    public RegistroClinico agregarRegistroClinico(int mascotaId, LocalDate fecha, String diagnostico, String tratamiento, String observaciones) {
        RegistroClinico registro = new RegistroClinico(
                siguienteIdRegistros(),
                mascotaId,
                fecha,
                validarTexto(diagnostico, "diagnostico"),
                tratamiento.trim(),
                observaciones.trim()
        );
        registros.add(registro);
        guardarSilencioso();
        return registro;
    }

    public void actualizarEstadoCita(int citaId, Cita.Estado estado) {
        Cita cita = buscarCita(citaId).orElseThrow(() -> new IllegalArgumentException("No existe una cita con el ID indicado."));
        cita.setEstado(estado);
        guardarSilencioso();
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public List<Mascota> getMascotas() {
        return new ArrayList<>(mascotas);
    }

    public List<Cita> getCitas() {
        return new ArrayList<>(citas);
    }

    public List<RegistroClinico> getRegistros() {
        return new ArrayList<>(registros);
    }

    public List<Mascota> getMascotasPorCliente(int clienteId) {
        return mascotas.stream().filter(mascota -> mascota.getClienteId() == clienteId).collect(Collectors.toList());
    }

    public List<RegistroClinico> getRegistrosPorMascota(int mascotaId) {
        return registros.stream().filter(registro -> registro.getMascotaId() == mascotaId).collect(Collectors.toList());
    }

    public Optional<Cliente> buscarCliente(int id) {
        return clientes.stream().filter(cliente -> cliente.getId() == id).findFirst();
    }

    public Optional<Mascota> buscarMascota(int id) {
        return mascotas.stream().filter(mascota -> mascota.getId() == id).findFirst();
    }

    public Optional<Cita> buscarCita(int id) {
        return citas.stream().filter(cita -> cita.getId() == id).findFirst();
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
        } catch (IOException | RuntimeException ex) {
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
        return siguienteId(clientes.stream().map(Cliente::getId).collect(Collectors.toList()));
    }

    private int siguienteIdMascotas() {
        return siguienteId(mascotas.stream().map(Mascota::getId).collect(Collectors.toList()));
    }

    private int siguienteIdCitas() {
        return siguienteId(citas.stream().map(Cita::getId).collect(Collectors.toList()));
    }

    private int siguienteIdRegistros() {
        return siguienteId(registros.stream().map(RegistroClinico::getId).collect(Collectors.toList()));
    }

    private int siguienteId(List<Integer> ids) {
        return ids.stream().max(Comparator.naturalOrder()).orElse(0) + 1;
    }
}
