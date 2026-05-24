package com.huellitas.vetcare.ui;

import com.huellitas.vetcare.model.Cita;
import com.huellitas.vetcare.model.Cliente;
import com.huellitas.vetcare.model.Mascota;
import com.huellitas.vetcare.model.RegistroClinico;
import com.huellitas.vetcare.service.VetCareService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VetCareFrame extends JFrame {
    private static final DateTimeFormatter CITA_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final VetCareService service = new VetCareService();
    private final DefaultTableModel clientesModel = model("ID", "Nombre", "Telefono", "Direccion");
    private final DefaultTableModel mascotasModel = model("ID", "Dueno", "Nombre", "Especie", "Raza", "Edad");
    private final DefaultTableModel citasModel = model("ID", "Mascota", "Fecha y hora", "Motivo", "Estado");
    private final DefaultTableModel historialModel = model("ID", "Mascota", "Fecha", "Diagnostico", "Tratamiento", "Observaciones");

    private final JComboBox<Cliente> clienteMascotaCombo = new JComboBox<>();
    private final JComboBox<Mascota> mascotaCitaCombo = new JComboBox<>();
    private final JComboBox<Mascota> mascotaHistorialCombo = new JComboBox<>();
    private final JComboBox<Cita.Estado> estadoCitaCombo = new JComboBox<>(Cita.Estado.values());

    public VetCareFrame() {
        setTitle("VetCare - Clinica Veterinaria Huellitas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 680);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", crearPanelClientes());
        tabs.addTab("Mascotas", crearPanelMascotas());
        tabs.addTab("Citas", crearPanelCitas());
        tabs.addTab("Historial clinico", crearPanelHistorial());
        add(tabs, BorderLayout.CENTER);

        refrescarTodo();
    }

    private JPanel crearPanelClientes() {
        JTextField nombre = new JTextField(18);
        JTextField telefono = new JTextField(18);
        JTextField direccion = new JTextField(18);
        JButton registrar = new JButton("Registrar cliente");

        JPanel form = formulario();
        agregarCampo(form, 0, "Nombre", nombre);
        agregarCampo(form, 1, "Telefono", telefono);
        agregarCampo(form, 2, "Direccion", direccion);
        agregarControl(form, 3, registrar);

        registrar.addActionListener(event -> {
            try {
                service.registrarCliente(nombre.getText(), telefono.getText(), direccion.getText());
                limpiar(nombre, telefono, direccion);
                refrescarTodo();
                mensaje("Cliente registrado correctamente.");
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            } catch (Exception ex) {
                error("No fue posible registrar el cliente: " + ex.getMessage());
            }
        });

        return panelConTabla(form, new JTable(clientesModel));
    }

    private JPanel crearPanelMascotas() {
        JTextField nombre = new JTextField(18);
        JTextField especie = new JTextField(18);
        JTextField raza = new JTextField(18);
        JTextField edad = new JTextField(18);
        JButton registrar = new JButton("Registrar mascota");

        JPanel form = formulario();
        agregarCampo(form, 0, "Dueno", clienteMascotaCombo);
        agregarCampo(form, 1, "Nombre", nombre);
        agregarCampo(form, 2, "Especie", especie);
        agregarCampo(form, 3, "Raza", raza);
        agregarCampo(form, 4, "Edad", edad);
        agregarControl(form, 5, registrar);

        registrar.addActionListener(event -> {
            try {
                Cliente cliente = (Cliente) clienteMascotaCombo.getSelectedItem();
                if (cliente == null) {
                    throw new IllegalArgumentException("Debe registrar o seleccionar un dueno.");
                }
                int edadValor = Integer.parseInt(edad.getText().trim());
                service.registrarMascota(cliente.getId(), nombre.getText(), especie.getText(), raza.getText(), edadValor);
                limpiar(nombre, especie, raza, edad);
                refrescarTodo();
                mensaje("Mascota registrada correctamente.");
            } catch (NumberFormatException ex) {
                error("La edad debe ser un numero entero.");
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            } catch (Exception ex) {
                error("No fue posible registrar la mascota: " + ex.getMessage());
            }
        });

        return panelConTabla(form, new JTable(mascotasModel));
    }

    private JPanel crearPanelCitas() {
        JTextField fecha = new JTextField("2026-05-23 09:00", 18);
        JTextField motivo = new JTextField(18);
        JTextField citaId = new JTextField(8);
        JButton agendar = new JButton("Agendar cita");
        JButton actualizar = new JButton("Actualizar estado");

        JPanel form = formulario();
        agregarCampo(form, 0, "Mascota", mascotaCitaCombo);
        agregarCampo(form, 1, "Fecha y hora", fecha);
        agregarCampo(form, 2, "Motivo", motivo);
        agregarControl(form, 3, agendar);
        agregarCampo(form, 4, "ID cita", citaId);
        agregarCampo(form, 5, "Estado", estadoCitaCombo);
        agregarControl(form, 6, actualizar);

        agendar.addActionListener(event -> {
            try {
                Mascota mascota = (Mascota) mascotaCitaCombo.getSelectedItem();
                if (mascota == null) {
                    throw new IllegalArgumentException("Debe registrar o seleccionar una mascota.");
                }
                LocalDateTime fechaHora = LocalDateTime.parse(fecha.getText().trim(), CITA_FORMATTER);
                service.agendarCita(mascota.getId(), fechaHora, motivo.getText());
                motivo.setText("");
                refrescarTodo();
                mensaje("Cita agendada correctamente.");
            } catch (java.time.format.DateTimeParseException ex) {
                error("Use el formato de fecha yyyy-MM-dd HH:mm.");
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            } catch (Exception ex) {
                error("No fue posible agendar la cita: " + ex.getMessage());
            }
        });

        actualizar.addActionListener(event -> {
            try {
                int id = Integer.parseInt(citaId.getText().trim());
                Cita.Estado estado = (Cita.Estado) estadoCitaCombo.getSelectedItem();
                service.actualizarEstadoCita(id, estado);
                citaId.setText("");
                refrescarTodo();
                mensaje("Estado actualizado.");
            } catch (NumberFormatException ex) {
                error("El ID de la cita debe ser numerico.");
            } catch (Exception ex) {
                error("No fue posible actualizar la cita: " + ex.getMessage());
            }
        });

        return panelConTabla(form, new JTable(citasModel));
    }

    private JPanel crearPanelHistorial() {
        JTextField fecha = new JTextField(LocalDate.now().toString(), 18);
        JTextField diagnostico = new JTextField(18);
        JTextField tratamiento = new JTextField(18);
        JTextArea observaciones = new JTextArea(3, 18);
        JButton guardar = new JButton("Guardar registro");

        JPanel form = formulario();
        agregarCampo(form, 0, "Mascota", mascotaHistorialCombo);
        agregarCampo(form, 1, "Fecha", fecha);
        agregarCampo(form, 2, "Diagnostico", diagnostico);
        agregarCampo(form, 3, "Tratamiento", tratamiento);
        agregarCampo(form, 4, "Observaciones", new JScrollPane(observaciones));
        agregarControl(form, 5, guardar);

        guardar.addActionListener(event -> {
            try {
                Mascota mascota = (Mascota) mascotaHistorialCombo.getSelectedItem();
                if (mascota == null) {
                    throw new IllegalArgumentException("Debe registrar o seleccionar una mascota.");
                }
                LocalDate fechaRegistro = LocalDate.parse(fecha.getText().trim());
                service.agregarRegistroClinico(mascota.getId(), fechaRegistro, diagnostico.getText(), tratamiento.getText(), observaciones.getText());
                limpiar(diagnostico, tratamiento);
                observaciones.setText("");
                refrescarTodo();
                mensaje("Registro clinico guardado.");
            } catch (java.time.format.DateTimeParseException ex) {
                error("Use el formato de fecha yyyy-MM-dd.");
            } catch (IllegalArgumentException ex) {
                error(ex.getMessage());
            } catch (Exception ex) {
                error("No fue posible guardar el historial: " + ex.getMessage());
            }
        });

        return panelConTabla(form, new JTable(historialModel));
    }

    private void refrescarTodo() {
        cargarClientes();
        cargarMascotas();
        cargarCitas();
        cargarHistorial();
        cargarCombos();
    }

    private void cargarClientes() {
        clientesModel.setRowCount(0);
        for (Cliente cliente : service.getClientes()) {
            clientesModel.addRow(new Object[]{cliente.getId(), cliente.getNombre(), cliente.getTelefono(), cliente.getDireccion()});
        }
    }

    private void cargarMascotas() {
        mascotasModel.setRowCount(0);
        for (Mascota mascota : service.getMascotas()) {
            String dueno = service.buscarCliente(mascota.getClienteId()).map(Cliente::getNombre).orElse("Sin dueno");
            mascotasModel.addRow(new Object[]{mascota.getId(), dueno, mascota.getNombre(), mascota.getEspecie(), mascota.getRaza(), mascota.getEdad()});
        }
    }

    private void cargarCitas() {
        citasModel.setRowCount(0);
        for (Cita cita : service.getCitas()) {
            String mascota = service.buscarMascota(cita.getMascotaId()).map(Mascota::getNombre).orElse("Sin mascota");
            citasModel.addRow(new Object[]{cita.getId(), mascota, cita.getFechaFormateada(), cita.getMotivo(), cita.getEstado()});
        }
    }

    private void cargarHistorial() {
        historialModel.setRowCount(0);
        for (RegistroClinico registro : service.getRegistros()) {
            String mascota = service.buscarMascota(registro.getMascotaId()).map(Mascota::getNombre).orElse("Sin mascota");
            historialModel.addRow(new Object[]{
                    registro.getId(),
                    mascota,
                    registro.getFecha(),
                    registro.getDiagnostico(),
                    registro.getTratamiento(),
                    registro.getObservaciones()
            });
        }
    }

    private void cargarCombos() {
        clienteMascotaCombo.removeAllItems();
        for (Cliente cliente : service.getClientes()) {
            clienteMascotaCombo.addItem(cliente);
        }
        mascotaCitaCombo.removeAllItems();
        mascotaHistorialCombo.removeAllItems();
        for (Mascota mascota : service.getMascotas()) {
            mascotaCitaCombo.addItem(mascota);
            mascotaHistorialCombo.addItem(mascota);
        }
    }

    private JPanel panelConTabla(JPanel form, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        table.setFillsViewportHeight(true);
        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel formulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Registro"));
        return panel;
    }

    private void agregarCampo(JPanel panel, int fila, String etiqueta, java.awt.Component campo) {
        GridBagConstraints label = constraints(0, fila);
        label.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(etiqueta + ":"), label);

        GridBagConstraints input = constraints(1, fila);
        input.weightx = 1;
        input.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, input);
    }

    private void agregarControl(JPanel panel, int fila, JButton boton) {
        GridBagConstraints c = constraints(1, fila);
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(boton, c);
    }

    private GridBagConstraints constraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
        c.insets = new Insets(4, 6, 4, 6);
        return c;
    }

    private DefaultTableModel model(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void limpiar(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "VetCare", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
