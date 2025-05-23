package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MainFrame extends JFrame {
    private HastaManager manager = new HastaManager();

    public MainFrame() {
        setTitle("Hastane Muayene Sıra Sistemi");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Hastane Muayene Sıra Sistemi", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        add(label, BorderLayout.NORTH);

        JButton btnYukle = new JButton("Dosyadan Yükle");
        add(btnYukle, BorderLayout.SOUTH);

        btnYukle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    manager.dosyadanOku("C:\\Users\\dtata\\Desktop\\Hasta1.txt");
                    JOptionPane.showMessageDialog(MainFrame.this, "Hasta verileri başarıyla yüklendi! Toplam: " + manager.tumHastalar.size());
                    MuayeneSimulasyonFrame simFrame = new MuayeneSimulasyonFrame(manager);
                    simFrame.setVisible(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Dosya okunamadı: " + ex.getMessage());
                }
            }
        });
    }
} 