package com.huellitas.vetcare.persistence;

import com.huellitas.vetcare.model.Cita;
import com.huellitas.vetcare.model.Cliente;
import com.huellitas.vetcare.model.Mascota;
import com.huellitas.vetcare.model.RegistroClinico;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CsvDatabase {
    private File dataDir = new File("data");
    private File clientesFile = new File(dataDir, "clientes.csv");
    private File mascotasFile = new File(dataDir, "mascotas.csv");
    private File citasFile = new File(dataDir, "citas.csv");
    private File registrosFile = new File(dataDir, "historial.csv");

    public static class DataSet {
        private ArrayList<Cliente> clientes;
        private ArrayList<Mascota> mascotas;
        private ArrayList<Cita> citas;
        private ArrayList<RegistroClinico> registros;

        public DataSet(ArrayList<Cliente> clientes, ArrayList<Mascota> mascotas, ArrayList<Cita> citas, ArrayList<RegistroClinico> registros) {
            this.clientes = clientes;
            this.mascotas = mascotas;
            this.citas = citas;
            this.registros = registros;
        }

        public ArrayList<Cliente> getClientes() {
            return clientes;
        }

        public ArrayList<Mascota> getMascotas() {
            return mascotas;
        }

        public ArrayList<Cita> getCitas() {
            return citas;
        }

        public ArrayList<RegistroClinico> getRegistros() {
            return registros;
        }
    }

    public DataSet cargar() throws IOException {
        crearCarpetaDatos();
        return new DataSet(cargarClientes(), cargarMascotas(), cargarCitas(), cargarRegistros());
    }

    public void guardar(ArrayList<Cliente> clientes, ArrayList<Mascota> mascotas, ArrayList<Cita> citas, ArrayList<RegistroClinico> registros) throws IOException {
        crearCarpetaDatos();

        ArrayList<String> lineasClientes = new ArrayList<String>();
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            lineasClientes.add(unir(cliente.getId(), cliente.getNombre(), cliente.getTelefono(), cliente.getDireccion()));
        }
        guardarLineas(clientesFile, lineasClientes);

        ArrayList<String> lineasMascotas = new ArrayList<String>();
        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
            lineasMascotas.add(unir(mascota.getId(), mascota.getClienteId(), mascota.getNombre(), mascota.getEspecie(), mascota.getRaza(), mascota.getEdad()));
        }
        guardarLineas(mascotasFile, lineasMascotas);

        ArrayList<String> lineasCitas = new ArrayList<String>();
        for (int i = 0; i < citas.size(); i++) {
            Cita cita = citas.get(i);
            lineasCitas.add(unir(cita.getId(), cita.getMascotaId(), cita.getFecha(), cita.getHora(), cita.getMotivo(), cita.getEstado()));
        }
        guardarLineas(citasFile, lineasCitas);

        ArrayList<String> lineasRegistros = new ArrayList<String>();
        for (int i = 0; i < registros.size(); i++) {
            RegistroClinico registro = registros.get(i);
            lineasRegistros.add(unir(registro.getId(), registro.getMascotaId(), registro.getFecha(), registro.getDiagnostico(), registro.getTratamiento(), registro.getObservaciones()));
        }
        guardarLineas(registrosFile, lineasRegistros);
    }

    private ArrayList<Cliente> cargarClientes() throws IOException {
        ArrayList<Cliente> clientes = new ArrayList<Cliente>();
        ArrayList<String> lineas = leerLineas(clientesFile);
        for (int i = 0; i < lineas.size(); i++) {
            String[] partes = separar(lineas.get(i));
            if (partes.length >= 4) {
                clientes.add(new Cliente(Integer.parseInt(partes[0]), partes[1], partes[2], partes[3]));
            }
        }
        return clientes;
    }

    private ArrayList<Mascota> cargarMascotas() throws IOException {
        ArrayList<Mascota> mascotas = new ArrayList<Mascota>();
        ArrayList<String> lineas = leerLineas(mascotasFile);
        for (int i = 0; i < lineas.size(); i++) {
            String[] partes = separar(lineas.get(i));
            if (partes.length >= 6) {
                mascotas.add(new Mascota(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), partes[2], partes[3], partes[4], Integer.parseInt(partes[5])));
            }
        }
        return mascotas;
    }

    private ArrayList<Cita> cargarCitas() throws IOException {
        ArrayList<Cita> citas = new ArrayList<Cita>();
        ArrayList<String> lineas = leerLineas(citasFile);
        for (int i = 0; i < lineas.size(); i++) {
            String[] partes = separar(lineas.get(i));
            if (partes.length >= 6) {
                citas.add(new Cita(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), partes[2], partes[3], partes[4], partes[5]));
            } else if (partes.length >= 5) {
                String fecha = partes[2];
                String hora = "";
                int posicionT = partes[2].indexOf("T");
                int posicionEspacio = partes[2].indexOf(" ");
                if (posicionT >= 0) {
                    fecha = partes[2].substring(0, posicionT);
                    hora = partes[2].substring(posicionT + 1);
                } else if (posicionEspacio >= 0) {
                    fecha = partes[2].substring(0, posicionEspacio);
                    hora = partes[2].substring(posicionEspacio + 1);
                }
                citas.add(new Cita(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), fecha, hora, partes[3], partes[4]));
            }
        }
        return citas;
    }

    private ArrayList<RegistroClinico> cargarRegistros() throws IOException {
        ArrayList<RegistroClinico> registros = new ArrayList<RegistroClinico>();
        ArrayList<String> lineas = leerLineas(registrosFile);
        for (int i = 0; i < lineas.size(); i++) {
            String[] partes = separar(lineas.get(i));
            if (partes.length >= 6) {
                registros.add(new RegistroClinico(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), partes[2], partes[3], partes[4], partes[5]));
            }
        }
        return registros;
    }

    private ArrayList<String> leerLineas(File file) throws IOException {
        ArrayList<String> lineas = new ArrayList<String>();
        if (!file.exists()) {
            return lineas;
        }

        Scanner scanner = new Scanner(file, "UTF-8");
        try {
            while (scanner.hasNextLine()) {
                lineas.add(scanner.nextLine());
            }
        } finally {
            scanner.close();
        }
        return lineas;
    }

    private void guardarLineas(File file, ArrayList<String> lineas) throws IOException {
        FileWriter writer = new FileWriter(file);
        try {
            for (int i = 0; i < lineas.size(); i++) {
                writer.write(lineas.get(i));
                writer.write(System.lineSeparator());
            }
        } finally {
            writer.close();
        }
    }

    private void crearCarpetaDatos() {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String unir(Object valor1, Object valor2, Object valor3, Object valor4) {
        return escapar(String.valueOf(valor1)) + "," + escapar(String.valueOf(valor2)) + "," + escapar(String.valueOf(valor3)) + "," + escapar(String.valueOf(valor4));
    }

    private String unir(Object valor1, Object valor2, Object valor3, Object valor4, Object valor5, Object valor6) {
        return escapar(String.valueOf(valor1)) + "," + escapar(String.valueOf(valor2)) + "," + escapar(String.valueOf(valor3)) + "," + escapar(String.valueOf(valor4)) + "," + escapar(String.valueOf(valor5)) + "," + escapar(String.valueOf(valor6));
    }

    private String[] separar(String linea) {
        return linea.split(",", -1);
    }

    private String escapar(String valor) {
        return valor.replace(",", " ").replace(System.lineSeparator(), " ");
    }
}
