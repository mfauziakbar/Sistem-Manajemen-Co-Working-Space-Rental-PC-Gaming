
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainFrame extends JFrame {

    private DefaultTableModel model;
    private JComboBox<FacilityItem> pilihFasilitas;

    // MENYIMPAN DATA FASILITAS
    private static class FacilityItem {
        int id;
        String nama;
        String jenis;
        double tarif;

        FacilityItem(int id, String nama, String jenis, double tarif) {
            this.id = id;
            this.nama = nama;
            this.jenis = jenis;
            this.tarif = tarif;
        }

        @Override
        public String toString() {
            return nama + " (" + jenis + ")";
        }
    }

    public MainFrame() {

        setTitle("Co-Working Space & Rental PC Gaming");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel judul = new JLabel(
            "CO-WORKING SPACE & RENTAL PC GAMING",
            SwingConstants.CENTER
        );

        judul.setFont(new Font("Arial", Font.BOLD, 22));
        panelUtama.add(judul, BorderLayout.NORTH);

        // FORM INPUT
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));

        JLabel labelNama = new JLabel("Nama Pelanggan:");
        JTextField inputNama = new JTextField();

        JLabel labelFasilitas = new JLabel("Jenis Fasilitas:");

        pilihFasilitas = new JComboBox<>();
        muatFasilitas();

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

        // PANEL TENGAH
        JPanel panelTengah = new JPanel(new BorderLayout(10, 20));
        panelTengah.add(panelForm, BorderLayout.NORTH);

        // TABEL BOOKING
        JLabel labelTabel = new JLabel("DAFTAR BOOKING");

        String[] namaKolom = {
            "No", "Nama Pelanggan", "Jenis Fasilitas",
            "Tarif/Jam", "Durasi", "Total Harga", "Status"
        };

        model = new DefaultTableModel(namaKolom, 0);

        JTable tabelBooking = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabelBooking);

        JPanel panelTabel = new JPanel(new BorderLayout(5, 5));

        panelTabel.add(labelTabel, BorderLayout.NORTH);
        panelTabel.add(scrollPane, BorderLayout.CENTER);

        panelTengah.add(panelTabel, BorderLayout.CENTER);
        panelUtama.add(panelTengah, BorderLayout.CENTER);

        tampilkanDataTabel();

        // EVENT TOMBOL PESAN
        tombolPesan.addActionListener(e -> {

            String nama = inputNama.getText().trim();
            String durasiText = inputDurasi.getText().trim();

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nama pelanggan harus diisi!");
                return;
            }

            FacilityItem fasilitas =
                (FacilityItem) pilihFasilitas.getSelectedItem();

            if (fasilitas == null) {
                JOptionPane.showMessageDialog(this,
                    "Data fasilitas belum tersedia!");
                return;
            }

            int durasi;

            try {
                durasi = Integer.parseInt(durasiText);

                if (durasi <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Durasi harus lebih dari 0!");
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Durasi harus berupa angka!");
                return;
            }

            double tarifPerJam = fasilitas.tarif;
            double totalHarga = tarifPerJam * durasi;

            String sql = "INSERT INTO tb_transaksi "
                + "(id_fasilitas, nama_pelanggan, durasi, "
                + "tarif_per_jam, total_bayar) "
                + "VALUES (?, ?, ?, ?, ?)";

            try (Connection conn = KoneksiJava.getKoneksi()) {

                if (conn == null) {
                    JOptionPane.showMessageDialog(this,
                        "Koneksi database gagal!");
                    return;
                }

                try (PreparedStatement ps =
                    conn.prepareStatement(sql)) {

                    ps.setInt(1, fasilitas.id);
                    ps.setString(2, nama);
                    ps.setInt(3, durasi);
                    ps.setDouble(4, tarifPerJam);
                    ps.setDouble(5, totalHarga);

                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                    "SUKSES! Data booking berhasil disimpan.");

                tampilkanDataTabel();

                inputNama.setText("");
                inputDurasi.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "ERROR DB: " + ex.getMessage());
            }
        });

        add(panelUtama);
    }

    // METHOD MEMUAT DATA FASILITAS DARI DATABASE
    private void muatFasilitas() {

        pilihFasilitas.removeAllItems();

        String sql = "SELECT id_fasilitas, nama, jenis, tarif_per_jam "
            + "FROM tb_fasilitas";

        try (Connection conn = KoneksiJava.getKoneksi()) {

            if (conn == null) {
                return;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {

                    FacilityItem fasilitas = new FacilityItem(
                        rs.getInt("id_fasilitas"),
                        rs.getString("nama"),
                        rs.getString("jenis"),
                        rs.getDouble("tarif_per_jam")
                    );

                    pilihFasilitas.addItem(fasilitas);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat fasilitas: " + e.getMessage());
        }
    }

    // METHOD MENAMPILKAN DATA TRANSAKSI
    private void tampilkanDataTabel() {

        model.setRowCount(0);

        String sql = "SELECT t.nama_pelanggan, f.jenis, "
            + "t.tarif_per_jam, t.durasi, t.total_bayar "
            + "FROM tb_transaksi t "
            + "LEFT JOIN tb_fasilitas f "
            + "ON t.id_fasilitas = f.id_fasilitas "
            + "ORDER BY t.id_transaksi ASC";

        try (Connection conn = KoneksiJava.getKoneksi()) {

            if (conn == null) {
                return;
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                int no = 1;

                while (rs.next()) {

                    model.addRow(new Object[]{
                        no++,
                        rs.getString("nama_pelanggan"),
                        rs.getString("jenis"),
                        rs.getDouble("tarif_per_jam"),
                        rs.getInt("durasi"),
                        rs.getDouble("total_bayar"),
                        "Booking"
                    });
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                "Gagal menampilkan transaksi: " + e.getMessage());
        }
    }

    // HALAMAN LOGIN ADMIN
    public static void tampilkanLogin() {

        JFrame login = new JFrame("Login Admin");

        login.setSize(350, 250);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));

        panel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel judul = new JLabel("LOGIN ADMIN");
        judul.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel labelUsername = new JLabel("Username:");
        JTextField inputUsername = new JTextField();

        JLabel labelPassword = new JLabel("Password:");
        JPasswordField inputPassword = new JPasswordField();

        JButton tombolLogin = new JButton("Login");

        panel.add(judul);
        panel.add(new JLabel(""));

        panel.add(labelUsername);
        panel.add(inputUsername);

        panel.add(labelPassword);
        panel.add(inputPassword);

        panel.add(new JLabel(""));
        panel.add(tombolLogin);

        tombolLogin.addActionListener(e -> {

            String username = inputUsername.getText().trim();

            String password = new String(
                inputPassword.getPassword()
            );

            String sql = "SELECT * FROM tb_admin "
                + "WHERE username = ? AND password = ?";

            try (Connection conn = KoneksiJava.getKoneksi()) {

                if (conn == null) {
                    JOptionPane.showMessageDialog(login,
                        "Koneksi database gagal!");
                    return;
                }

                try (PreparedStatement ps =
                    conn.prepareStatement(sql)) {

                    ps.setString(1, username);
                    ps.setString(2, password);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {

                            JOptionPane.showMessageDialog(login,
                                "Login berhasil!");

                            login.dispose();

                            MainFrame frame = new MainFrame();
                            frame.setVisible(true);

                        } else {

                            JOptionPane.showMessageDialog(login,
                                "Username atau password salah!");

                            inputPassword.setText("");
                        }
                    }
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(login,
                    "ERROR LOGIN: " + ex.getMessage());
            }
        });

        login.add(panel);
        login.setVisible(true);
    }

    // MAIN PROGRAM
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            tampilkanLogin();
        });
    }
}
