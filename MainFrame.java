
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainFrame extends JFrame {

    private DefaultTableModel model;

    public MainFrame() {
        setTitle("Co-Working Space & Rental PC Gaming");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel judul = new JLabel(
            "CO-WORKING SPACE & RENTAL PC GAMING",
            SwingConstants.CENTER
        );
        judul.setFont(new Font("Arial", Font.BOLD, 22));
        panelUtama.add(judul, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));

        JLabel labelNama = new JLabel("Nama Pelanggan:");
        JTextField inputNama = new JTextField();

        JLabel labelFasilitas = new JLabel("Jenis Fasilitas:");
        String[] daftarFasilitas = {
            "Meja Desk", "Meeting Room", "PC Gaming"
        };
        JComboBox<String> pilihFasilitas =
            new JComboBox<>(daftarFasilitas);

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

        tombolPesan.addActionListener(e -> {

            String nama = inputNama.getText().trim();
            String fasilitas =
                pilihFasilitas.getSelectedItem().toString();
            String durasiText = inputDurasi.getText().trim();

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nama harus diisi!");
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

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Durasi harus angka!");
                return;
            }

            double tarifPerJam = 0;

            if (fasilitas.equals("PC Gaming")) {
                tarifPerJam = 10000;
            } else if (fasilitas.equals("Meeting Room")) {
                tarifPerJam = 50000;
            } else if (fasilitas.equals("Meja Desk")) {
                tarifPerJam = 15000;
            }

            double totalHarga = tarifPerJam * durasi;

            try {
                Connection conn = KoneksiJava.getKoneksi();

                if (conn == null) {
                    JOptionPane.showMessageDialog(this,
                        "XAMPP belum nyala!");
                    return;
                }

                String sql = "INSERT INTO fasilitas "
                    + "(nama_fasilitas, jenis, tarif_per_jam, "
                    + "durasi, total_harga, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setString(1, nama);
                ps.setString(2, fasilitas);
                ps.setDouble(3, tarifPerJam);
                ps.setInt(4, durasi);
                ps.setDouble(5, totalHarga);
                ps.setString(6, "Disewa");

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "SUKSES! Data berhasil disimpan.");

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

    private void tampilkanDataTabel() {

        model.setRowCount(0);

        try {
            Connection conn = KoneksiJava.getKoneksi();

            if (conn != null) {

                Statement stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM fasilitas"
                );

                int no = 1;

                while (rs.next()) {

                    String nama = rs.getString("nama_fasilitas");
                    String jenis = rs.getString("jenis");
                    double tarif = rs.getDouble("tarif_per_jam");
                    int durasi = rs.getInt("durasi");
                    double totalHarga = rs.getDouble("total_harga");
                    String status = rs.getString("status");

                    model.addRow(new Object[]{
                        no++, nama, jenis, tarif,
                        durasi, totalHarga, status
                    });
                }
            }

        } catch (Exception e) {
            System.out.println(
                "Gagal narik data: " + e.getMessage()
            );
        }
    }

    // HALAMAN LOGIN
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

            String username = inputUsername.getText();
            String password = new String(
                inputPassword.getPassword()
            );

            if (username.equals("admin")
                    && password.equals("12345")) {

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
        });

        login.add(panel);
        login.setVisible(true);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            tampilkanLogin();
        });
    }
}
