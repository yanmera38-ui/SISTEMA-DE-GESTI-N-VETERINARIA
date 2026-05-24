package com.huellitas.vetcare;

import com.huellitas.vetcare.ui.VetCareFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VetCareFrame().setVisible(true));
    }
}
