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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class VetCareFrame extends JFrame {
    private VetCareService service = new VetCareService();
    private DefaultTableModel clientesModel = model("ID", "Nombre", "Telefono", "Direccion");
    private DefaultTableModel mascotasModel = model("ID", "Dueno", "Nombre", "Especie", "Raza", "Edad");
    private DefaultTableModel citasModel = model("ID", "Mascota", "Fecha", "Hora", "Motivo", "Estado");
    private DefaultTableModel historialModel = model("ID", "Mascota", "Fecha", "Diagnostico", "Tratamiento", "Observaciones");

    private JComboBox<Cliente> clienteMascotaCombo = new JComboBox<Cliente>();
    private JComboBox<Mascota> mascotaCitaCombo = new JComboBox<Mascota>();
    private JComboBox<Mascota> mascotaHistorialCombo = new JComboBox<Mascota>();
    private JComboBox<String> estadoCitaCombo = new JComboBox<String>(new String[]{"PROGRAMADA", "ATENDIDA", "CANCELADA"});

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
        final JTextField nombre = new JTextField(18);
        final JTextField telefono = new JTextField(18);
        final JTextField direccion = new JTextField(18);
        JButton registrar = new JButton("Registrar cliente");

        JPanel form = formulario();
        agregarCampo(form, 0, "Nombre", nombre);
        agregarCampo(form, 1, "Telefono", telefono);
        agregarCampo(form, 2, "Direccion", direccion);
        agregarControl(form, 3, registrar);

        registrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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
            }
        });

        return panelConTabla(form, new JTable(clientesModel));
    }

    private JPanel crearPanelMascotas() {
        final JTextField nombre = new JTextField(18);
        final JTextField especie = new JTextField(18);
        final JTextField raza = new JTextField(18);
        final JTextField edad = new JTextField(18);
        JButton registrar = new JButton("Registrar mascota");

        JPanel form = formulario();
        agregarCampo(form, 0, "Dueno", clienteMascotaCombo);
        agregarCampo(form, 1, "Nombre", nombre);
        agregarCampo(form, 2, "Especie", especie);
        agregarCampo(form, 3, "Raza", raza);
        agregarCampo(form, 4, "Edad", edad);
        agregarControl(form, 5, registrar);

        registrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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
            }
        });

        return panelConTabla(form, new JTable(mascotasModel));
    }

    private JPanel crearPanelCitas() {
        final JTextField fecha = new JTextField("2026-05-30", 18);
        final JTextField hora = new JTextField("09:00", 18);
        final JTextField motivo = new JTextField(18);
        final JTextField citaId = new JTextField(8);
        JButton agendar = new JButton("Agendar cita");
        JButton actualizar = new JButton("Actualizar estado");

        JPanel form = formulario();
        agregarCampo(form, 0, "Mascota", mascotaCitaCombo);
        agregarCampo(form, 1, "Fecha", fecha);
        agregarCampo(form, 2, "Hora", hora);
        agregarCampo(form, 3, "Motivo", motivo);
        agregarControl(form, 4, agendar);
        agregarCampo(form, 5, "ID cita", citaId);
        agregarCampo(form, 6, "Estado", estadoCitaCombo);
        agregarControl(form, 7, actualizar);

        agendar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Mascota mascota = (Mascota) mascotaCitaCombo.getSelectedItem();
                    if (mascota == null) {
                        throw new IllegalArgumentException("Debe registrar o seleccionar una mascota.");
                    }
                    service.agendarCita(mascota.getId(), fecha.getText(), hora.getText(), motivo.getText());
                    motivo.setText("");
                    refrescarTodo();
                    mensaje("Cita agendada correctamente.");
                } catch (IllegalArgumentException ex) {
                    error(ex.getMessage());
                } catch (Exception ex) {
                    error("No fue posible agendar la cita: " + ex.getMessage());
                }
            }
        });

        actualizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    int id = Integer.parseInt(citaId.getText().trim());
                    String estado = (String) estadoCitaCombo.getSelectedItem();
                    service.actualizarEstadoCita(id, estado);
                    citaId.setText("");
                    refrescarTodo();
                    mensaje("Estado actualizado.");
                } catch (NumberFormatException ex) {
                    error("El ID de la cita debe ser numerico.");
                } catch (Exception ex) {
                    error("No fue posible actualizar la cita: " + ex.getMessage());
                }
            }
        });

        return panelConTabla(form, new JTable(citasModel));
    }

    private JPanel crearPanelHistorial() {
        final JTextField fecha = new JTextField("2026-05-30", 18);
        final JTextField diagnostico = new JTextField(18);
        final JTextField tratamiento = new JTextField(18);
        final JTextArea observaciones = new JTextArea(3, 18);
        JButton guardar = new JButton("Guardar registro");

        JPanel form = formulario();
        agregarCampo(form, 0, "Mascota", mascotaHistorialCombo);
        agregarCampo(form, 1, "Fecha", fecha);
        agregarCampo(form, 2, "Diagnostico", diagnostico);
        agregarCampo(form, 3, "Tratamiento", tratamiento);
        agregarCampo(form, 4, "Observaciones", new JScrollPane(observaciones));
        agregarControl(form, 5, guardar);

        guardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Mascota mascota = (Mascota) mascotaHistorialCombo.getSelectedItem();
                    if (mascota == null) {
                        throw new IllegalArgumentException("Debe registrar o seleccionar una mascota.");
                    }
                    service.agregarRegistroClinico(mascota.getId(), fecha.getText(), diagnostico.getText(), tratamiento.getText(), observaciones.getText());
                    limpiar(diagnostico, tratamiento);
                    observaciones.setText("");
                    refrescarTodo();
                    mensaje("Registro clinico guardado.");
                } catch (IllegalArgumentException ex) {
                    error(ex.getMessage());
                } catch (Exception ex) {
                    error("No fue posible guardar el historial: " + ex.getMessage());
                }
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
        ArrayList<Cliente> clientes = service.getClientes();
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            clientesModel.addRow(new Object[]{cliente.getId(), cliente.getNombre(), cliente.getTelefono(), cliente.getDireccion()});
        }
    }

    private void cargarMascotas() {
        mascotasModel.setRowCount(0);
        ArrayList<Mascota> mascotas = service.getMascotas();
        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
            Cliente cliente = service.buscarCliente(mascota.getClienteId());
            String dueno = "Sin dueno";
            if (cliente != null) {
                dueno = cliente.getNombre();
            }
            mascotasModel.addRow(new Object[]{mascota.getId(), dueno, mascota.getNombre(), mascota.getEspecie(), mascota.getRaza(), mascota.getEdad()});
        }
    }

    private void cargarCitas() {
        citasModel.setRowCount(0);
        ArrayList<Cita> citas = service.getCitas();
        for (int i = 0; i < citas.size(); i++) {
            Cita cita = citas.get(i);
            Mascota mascotaEncontrada = service.buscarMascota(cita.getMascotaId());
            String mascota = "Sin mascota";
            if (mascotaEncontrada != null) {
                mascota = mascotaEncontrada.getNombre();
            }
            citasModel.addRow(new Object[]{cita.getId(), mascota, cita.getFecha(), cita.getHora(), cita.getMotivo(), cita.getEstado()});
        }
    }

    private void cargarHistorial() {
        historialModel.setRowCount(0);
        ArrayList<RegistroClinico> registros = service.getRegistros();
        for (int i = 0; i < registros.size(); i++) {
            RegistroClinico registro = registros.get(i);
            Mascota mascotaEncontrada = service.buscarMascota(registro.getMascotaId());
            String mascota = "Sin mascota";
            if (mascotaEncontrada != null) {
                mascota = mascotaEncontrada.getNombre();
            }
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
        ArrayList<Cliente> clientes = service.getClientes();
        for (int i = 0; i < clientes.size(); i++) {
            clienteMascotaCombo.addItem(clientes.get(i));
        }

        mascotaCitaCombo.removeAllItems();
        mascotaHistorialCombo.removeAllItems();
        ArrayList<Mascota> mascotas = service.getMascotas();
        for (int i = 0; i < mascotas.size(); i++) {
            Mascota mascota = mascotas.get(i);
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
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void limpiar(JTextField... campos) {
        for (int i = 0; i < campos.length; i++) {
            campos[i].setText("");
        }
    }

    private void mensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "VetCare", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
