package PROJEKUAS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import PROJEKUAS.KoneksiJava;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainFrame extends JFrame {

    // Bikin model tabel di luar biar bisa diakses dari mana aja
    private DefaultTableModel model;

    public MainFrame() {
        setTitle("Co-Working Space & Rental PC Gaming");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel judul = new JLabel("CO-WORKING SPACE & RENTAL PC GAMING", SwingConstants.CENTER);
        judul.setFont(new Font("Arial", Font.BOLD, 22));
        panelUtama.add(judul, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));

        JLabel labelNama = new JLabel("Nama Pelanggan:");
        JTextField inputNama = new JTextField();

        JLabel labelFasilitas = new JLabel("Jenis Fasilitas:");
        String[] daftarFasilitas = {"Meja Desk", "Meeting Room", "PC Gaming"};
        JComboBox<String> pilihFasilitas = new JComboBox<>(daftarFasilitas);

        JLabel labelDurasi = new JLabel("Durasi (Jam):");
        JTextField inputDurasi = new JTextField();

        JButton tombolPesan = new JButton("Pesan");

        panelForm.add(labelNama);
        panelForm.add(inputNama);
        panelForm.add(labelFasilitas);
        panelForm.add(pilihFasilitas);
        panelForm.add(labelDurasi);
        panelForm.add(inputDurasi);
        panelForm.add(new JLabel(""));
        panelForm.add(tombolPesan);

        JPanel panelTengah = new JPanel(new BorderLayout(10, 20));
        panelTengah.add(panelForm, BorderLayout.NORTH);

        JLabel labelTabel = new JLabel("DAFTAR BOOKING");
        String[] namaKolom = {"No", "Nama Pelanggan", "Jenis Fasilitas", "Durasi"};
        
        // Inisialisasi model tabel di sini
        model = new DefaultTableModel(namaKolom, 0);
        JTable tabelBooking = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabelBooking);

        JPanel panelTabel = new JPanel(new BorderLayout(5, 5));
        panelTabel.add(labelTabel, BorderLayout.NORTH);
        panelTabel.add(scrollPane, BorderLayout.CENTER);

        panelTengah.add(panelTabel, BorderLayout.CENTER);
        panelUtama.add(panelTengah, BorderLayout.CENTER);

        // --- PANGGIL DATA PERTAMA KALI SAAT APLIKASI DIBUKA ---
        tampilkanDataTabel();

        // EVENT KETIKA TOMBOL DIKLIK 
        tombolPesan.addActionListener(e -> {
            String nama = inputNama.getText().trim();
            String fasilitas = pilihFasilitas.getSelectedItem().toString();
            String durasiText = inputDurasi.getText().trim();

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama harus diisi!");
                return;
            }

            int durasi;
            try {
                durasi = Integer.parseInt(durasiText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Durasi harus angka!");
                return;
            }

            // PROSES SIMPAN KE MYSQL
            try {
                Connection conn = KoneksiJava.getKoneksi();
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "XAMPP belum nyala!");
                    return;
                }

                String sql = "INSERT INTO fasilitas (nama_fasilitas, jenis, tarif_per_jam, status) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                
                ps.setString(1, nama);
                ps.setString(2, fasilitas);
                ps.setDouble(3, durasi * 10000); // Anggap tarif 10rb per jam
                ps.setString(4, "Disewa");

                ps.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "SUKSES! Data berhasil disimpan.");
                
                // --- UPDATE TABEL SETELAH DATA BERHASIL DISIMPAN ---
                tampilkanDataTabel(); 
                
                inputNama.setText("");
                inputDurasi.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "ERROR DB: " + ex.getMessage());
            }
        });

        add(panelUtama);
    }

    // Method KHUSUS buat narik data dari XAMPP ke Tabel GUI
    private void tampilkanDataTabel() {
        model.setRowCount(0); 
        try {
            Connection conn = KoneksiJava.getKoneksi();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM fasilitas");

                int no = 1;
                while (rs.next()) {
                    String nama = rs.getString("nama_fasilitas");
                    String jenis = rs.getString("jenis");
                    
                    // Tarik tarif dari DB, lalu bagi 10.000 biar balik jadi angka Durasi
                    double tarif = rs.getDouble("tarif_per_jam");
                    int durasi = (int) (tarif / 10000); 
                    
                    // Masukin ke baris tabel GUI
                    model.addRow(new Object[]{no++, nama, jenis, durasi});
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal narik data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}