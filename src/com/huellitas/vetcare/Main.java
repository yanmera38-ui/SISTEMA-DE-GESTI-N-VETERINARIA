package com.huellitas.vetcare;

import com.huellitas.vetcare.ui.VetCareFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VetCareFrame ventana = new VetCareFrame();
                ventana.setVisible(true);
            }
        });
    }
}
