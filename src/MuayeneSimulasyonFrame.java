package src;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MuayeneSimulasyonFrame extends JFrame {
    private JLabel saatLabel;
    private JTextArea mesajArea;
    private JTextArea muayeneOlanlarArea;
    private JPanel heapPanel;
    private SanalSaat saat;
    private HastaManager manager;
    private Hasta aktifHasta = null;
    private double sonrakiMuayeneBaslangic = 9.00;
    private double muayeneBitisSaati = 9.00;
    private double aktifHastaGirisSaati = 0.0;
    private JButton btnDurdur;
    private JButton btnDevam;
    private boolean saatDurdu = false;
    private JTable heapTable;
    private JScrollPane heapTableScroll;

    public MuayeneSimulasyonFrame(HastaManager manager) {
        this.manager = manager;
        setTitle("Muayene Simülasyonu");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Tam ekran
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Saat ve butonlar paneli
        JPanel saatPanel = new JPanel(new BorderLayout());
        saatLabel = new JLabel("Saat: 09:00", SwingConstants.RIGHT);
        saatLabel.setFont(new Font("Arial", Font.BOLD, 20));
        btnDurdur = new JButton("Sanal Saati Durdur");
        btnDevam = new JButton("Sanal Saati Devam Ettir");
        btnDevam.setEnabled(false);
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnDurdur);
        btnPanel.add(btnDevam);
        saatPanel.add(saatLabel, BorderLayout.CENTER);
        saatPanel.add(btnPanel, BorderLayout.EAST);
        add(saatPanel, BorderLayout.NORTH);

        // Mesaj alanı
        mesajArea = new JTextArea();
        mesajArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(mesajArea);
        scroll.setPreferredSize(new Dimension(600, 0));
        JLabel bilgiLabel = new JLabel("Bilgi Paneli", SwingConstants.CENTER);
        bilgiLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scroll.setColumnHeaderView(bilgiLabel);
        add(scroll, BorderLayout.EAST);

        // Muayene olanlar alanı
        muayeneOlanlarArea = new JTextArea();
        muayeneOlanlarArea.setEditable(false);
        muayeneOlanlarArea.setFont(new Font("Arial", Font.PLAIN, 15));
        JScrollPane muayeneScroll = new JScrollPane(muayeneOlanlarArea);
        muayeneScroll.setPreferredSize(new Dimension(600, 0));
        add(muayeneScroll, BorderLayout.WEST);
        JLabel muayeneLabel = new JLabel("Muayene Olanlar", SwingConstants.CENTER);
        muayeneLabel.setFont(new Font("Arial", Font.BOLD, 16));
        muayeneScroll.setColumnHeaderView(muayeneLabel);

        // Heap paneli (orta panel)
        heapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawHeapTree(g, manager.muayeneSirasi.getHeapList());
            }
        };
        heapPanel.setLayout(new BorderLayout());
        add(heapPanel, BorderLayout.CENTER);

        // Heap tablo paneli (orta alt)
        String[] columnNames = {"Hasta Adı", "Öncelik Puanı", "Kayıt Saati", "Muayene Süresi (dk)"};
        Object[][] data = {};
        heapTable = new JTable(data, columnNames);
        heapTableScroll = new JScrollPane(heapTable);
        heapTableScroll.setPreferredSize(new Dimension(600, 200));
        heapPanel.add(heapTableScroll, BorderLayout.SOUTH);

        // Sanal saat ve simülasyon başlat
        saat = new SanalSaat(8.00, 12.0); // 1 sn = 12 dk
        saat.baslat(() -> SwingUtilities.invokeLater(this::simulasyonTick));

        btnDurdur.addActionListener(e -> {
            saat.durdur();
            saatDurdu = true;
            btnDurdur.setEnabled(false);
            btnDevam.setEnabled(true);
        });
        btnDevam.addActionListener(e -> {
            if (saat.getSaat() < 24.00) {
                saat.baslat(() -> SwingUtilities.invokeLater(this::simulasyonTick));
                saatDurdu = false;
                btnDurdur.setEnabled(true);
                btnDevam.setEnabled(false);
            }
        });
    }

    private void simulasyonTick() {
        // Saat güncelle
        saatLabel.setText("Saat: " + saatString(saat.getSaat()));
        // --- Kayıt saati gelmiş hastaları heap'e ekle ---
        List<Hasta> eklenecekler = new java.util.ArrayList<>();
        for (Hasta h : manager.eklenmemisHastalar) {
            if (h.hastaKayitSaati <= saat.getSaat()) {
                manager.muayeneSirasi.ekle(h);
                mesajEkle(h.hastaAdi + " sisteme kaydoldu. (Kayıt saati: " + saatString(h.hastaKayitSaati) + ")");
                eklenecekler.add(h);
            }
        }
        manager.eklenmemisHastalar.removeAll(eklenecekler);
        // Muayene akışı ve mesajlar
        if (!saatDurdu && aktifHasta == null && manager.bekleyenHastaVar() && saat.getSaat() >= sonrakiMuayeneBaslangic) {
            aktifHasta = manager.siradakiHastayiMuayeneyeAl(saat.getSaat());
            if (aktifHasta != null) {
                aktifHastaGirisSaati = saat.getSaat();
                mesajEkle(
                    aktifHasta.hastaAdi + " muayeneye alındı. " +
                    "[Öncelik: " + aktifHasta.oncelikPuani +
                    ", Süre: " + aktifHasta.muayeneSuresi +
                    " dk, Kanama: " + aktifHasta.kanamaliHastaDurumBilgisi +
                    ", Mahkum: " + (aktifHasta.mahkumlukDurumBilgisi ? "Evet" : "Hayır") +
                    ", Engellilik: " + aktifHasta.engellilikOrani + "]"
                );
                muayeneBitisSaati = saat.getSaat() + aktifHasta.muayeneSuresi / 60.0;
            }
        }
        if (!saatDurdu && aktifHasta != null && saat.getSaat() >= muayeneBitisSaati) {
            mesajEkle(aktifHasta.hastaAdi + " muayeneden çıktı. Çıkış saati: " + saatString(saat.getSaat()));
            muayeneOlanlarArea.append(
                aktifHasta.hastaAdi +
                " - Öncelik: " + aktifHasta.oncelikPuani +
                " - Kayıt: " + saatString(aktifHasta.hastaKayitSaati) +
                " - Giriş: " + saatString(aktifHastaGirisSaati) +
                " - Çıkış: " + saatString(saat.getSaat()) +
                " - Süre: " + aktifHasta.muayeneSuresi + " dk\n"
            );
            sonrakiMuayeneBaslangic = saat.getSaat();
            aktifHasta = null;
        }
        heapPanel.repaint();
        updateHeapTable();

        // Tüm hastalar muayene olduysa saati durdur ve mesaj göster
        if (manager.eklenmemisHastalar.isEmpty() && !manager.bekleyenHastaVar() && aktifHasta == null) {
            saat.durdur();
            saatDurdu = true;
            btnDurdur.setEnabled(false);
            btnDevam.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Bütün hastalar muayene edildi.");
        }
    }

    private String saatString(double saat) {
        double gosterimSaat = saat % 24.0;
        if (gosterimSaat < 0) gosterimSaat += 24.0;
        int h = (int) gosterimSaat;
        int m = (int) ((gosterimSaat - h) * 100 * 0.6);
        return String.format("%02d:%02d", h, m);
    }

    // Basit max heap ağacı çizimi
    private void drawHeapTree(Graphics g, List<Hasta> heapList) {
        if (heapList.isEmpty()) return;
        int nodeRadius = 30;
        int panelWidth = heapPanel.getWidth();
        int startX = panelWidth / 2;
        int startY = 60;
        drawHeapNode(g, heapList, 0, startX, startY, panelWidth / 4, nodeRadius);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Muayene Sırası (Öncelik Puanı):", 20, 30);
    }

    // Heap'i binary tree olarak çiz
    private void drawHeapNode(Graphics g, List<Hasta> heap, int index, int x, int y, int xOffset, int r) {
        if (index >= heap.size()) return;
        Hasta h = heap.get(index);
        g.setColor(new Color(200, 220, 255));
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        g.setColor(Color.BLACK);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        String label = h.hastaAdi + "\n" + h.oncelikPuani;
        g.drawString(h.hastaAdi, x - r + 5, y);
        g.drawString("(" + h.oncelikPuani + ")", x - r + 5, y + 15);
        // Sol çocuk
        int left = 2 * index + 1;
        if (left < heap.size()) {
            int childX = x - xOffset;
            int childY = y + 70;
            g.drawLine(x, y + r, childX, childY - r);
            drawHeapNode(g, heap, left, childX, childY, xOffset / 2, r);
        }
        // Sağ çocuk
        int right = 2 * index + 2;
        if (right < heap.size()) {
            int childX = x + xOffset;
            int childY = y + 70;
            g.drawLine(x, y + r, childX, childY - r);
            drawHeapNode(g, heap, right, childX, childY, xOffset / 2, r);
        }
    }

    private void updateHeapTable() {
        List<Hasta> heapList = manager.muayeneSirasi.getHeapList();
        String[] columnNames = {"Hasta Adı", "Öncelik Puanı", "Kayıt Saati", "Muayene Süresi (dk)"};
        Object[][] data = new Object[heapList.size()][4];
        for (int i = 0; i < heapList.size(); i++) {
            Hasta h = heapList.get(i);
            data[i][0] = h.hastaAdi;
            data[i][1] = h.oncelikPuani;
            data[i][2] = saatString(h.hastaKayitSaati);
            data[i][3] = h.muayeneSuresi;
        }
        heapTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
    }

    public void mesajEkle(String mesaj) {
        mesajArea.append(mesaj + "\n");
    }
} 