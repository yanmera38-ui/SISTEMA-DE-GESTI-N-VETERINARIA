package com.huellitas.vetcare.persistence;

import com.huellitas.vetcare.model.Cita;
import com.huellitas.vetcare.model.Cliente;
import com.huellitas.vetcare.model.Mascota;
import com.huellitas.vetcare.model.RegistroClinico;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CsvDatabase {
    private final Path dataDir = Paths.get("data");
    private final Path clientesFile = dataDir.resolve("clientes.csv");
    private final Path mascotasFile = dataDir.resolve("mascotas.csv");
    private final Path citasFile = dataDir.resolve("citas.csv");
    private final Path registrosFile = dataDir.resolve("historial.csv");

    public static class DataSet {
        private final List<Cliente> clientes;
        private final List<Mascota> mascotas;
        private final List<Cita> citas;
        private final List<RegistroClinico> registros;

        public DataSet(List<Cliente> clientes, List<Mascota> mascotas, List<Cita> citas, List<RegistroClinico> registros) {
            this.clientes = clientes;
            this.mascotas = mascotas;
            this.citas = citas;
            this.registros = registros;
        }

        public List<Cliente> getClientes() {
            return clientes;
        }

        public List<Mascota> getMascotas() {
            return mascotas;
        }

        public List<Cita> getCitas() {
            return citas;
        }

        public List<RegistroClinico> getRegistros() {
            return registros;
        }
    }

    public DataSet cargar() throws IOException {
        Files.createDirectories(dataDir);
        return new DataSet(cargarClientes(), cargarMascotas(), cargarCitas(), cargarRegistros());
    }

    public void guardar(List<Cliente> clientes, List<Mascota> mascotas, List<Cita> citas, List<RegistroClinico> registros) throws IOException {
        Files.createDirectories(dataDir);
        guardarLineas(clientesFile, clientes.stream()
                .map(cliente -> unir(cliente.getId(), cliente.getNombre(), cliente.getTelefono(), cliente.getDireccion()))
                .collect(Collectors.toList()));
        guardarLineas(mascotasFile, mascotas.stream()
                .map(mascota -> unir(mascota.getId(), mascota.getClienteId(), mascota.getNombre(), mascota.getEspecie(), mascota.getRaza(), mascota.getEdad()))
                .collect(Collectors.toList()));
        guardarLineas(citasFile, citas.stream()
                .map(cita -> unir(cita.getId(), cita.getMascotaId(), cita.getFechaHora(), cita.getMotivo(), cita.getEstado()))
                .collect(Collectors.toList()));
        guardarLineas(registrosFile, registros.stream()
                .map(registro -> unir(registro.getId(), registro.getMascotaId(), registro.getFecha(), registro.getDiagnostico(), registro.getTratamiento(), registro.getObservaciones()))
                .collect(Collectors.toList()));
    }

    private List<Cliente> cargarClientes() throws IOException {
        List<Cliente> clientes = new ArrayList<>();
        for (String linea : leerLineas(clientesFile)) {
            String[] partes = separar(linea);
            if (partes.length >= 4) {
                clientes.add(new Cliente(Integer.parseInt(partes[0]), partes[1], partes[2], partes[3]));
            }
        }
        return clientes;
    }

    private List<Mascota> cargarMascotas() throws IOException {
        List<Mascota> mascotas = new ArrayList<>();
        for (String linea : leerLineas(mascotasFile)) {
            String[] partes = separar(linea);
            if (partes.length >= 6) {
                mascotas.add(new Mascota(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), partes[2], partes[3], partes[4], Integer.parseInt(partes[5])));
            }
        }
        return mascotas;
    }

    private List<Cita> cargarCitas() throws IOException {
        List<Cita> citas = new ArrayList<>();
        for (String linea : leerLineas(citasFile)) {
            String[] partes = separar(linea);
            if (partes.length >= 5) {
                citas.add(new Cita(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), LocalDateTime.parse(partes[2]), partes[3], Cita.Estado.valueOf(partes[4])));
            }
        }
        return citas;
    }

    private List<RegistroClinico> cargarRegistros() throws IOException {
        List<RegistroClinico> registros = new ArrayList<>();
        for (String linea : leerLineas(registrosFile)) {
            String[] partes = separar(linea);
            if (partes.length >= 6) {
                registros.add(new RegistroClinico(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), LocalDate.parse(partes[2]), partes[3], partes[4], partes[5]));
            }
        }
        return registros;
    }

    private List<String> leerLineas(Path file) throws IOException {
        if (!Files.exists(file)) {
            return Collections.emptyList();
        }
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    private void guardarLineas(Path file, List<String> lineas) throws IOException {
        Files.write(file, lineas, StandardCharsets.UTF_8);
    }

    private String unir(Object... valores) {
        List<String> seguros = new ArrayList<>();
        for (Object valor : valores) {
            seguros.add(escapar(String.valueOf(valor)));
        }
        return String.join(",", seguros);
    }

    private String[] separar(String linea) {
        return linea.split(",", -1);
    }

    private String escapar(String valor) {
        return valor.replace(",", " ").replace(System.lineSeparator(), " ");
    }
}
