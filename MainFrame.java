import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class MainFrame extends JFrame {
    private static final Color NAVY = new Color(15, 23, 42);
    private static final Color PANEL = new Color(30, 41, 59);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color CYAN = new Color(56, 189, 248);
    private static final Color WHITE = new Color(241, 245, 249);

    private DefaultTableModel model;
    private JTable tabel;
    private JComboBox<Facility> pilihFasilitas;
    private JTextField inputNama, inputDurasi;

    private static class Facility {
        int id;
        String nama, jenis;
        double tarif;

        Facility(int id, String nama, String jenis, double tarif) {
            this.id = id;
            this.nama = nama;
            this.jenis = jenis;
            this.tarif = tarif;
        }

        public String toString() {
            return nama + " (" + jenis + ")";
        }
    }

    public MainFrame() {
        setTitle("Co-Working Space & Rental PC Gaming");
        setSize(1050, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(NAVY);
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("CO-WORKING SPACE & RENTAL PC GAMING",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(CYAN);
        root.add(title, BorderLayout.NORTH);

        // FORM BOOKING
        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.setBackground(PANEL);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CYAN), "FORM BOOKING",
                0, 0, new Font("Arial", Font.BOLD, 14), WHITE));

        inputNama = new JTextField();
        inputDurasi = new JTextField();
        pilihFasilitas = new JComboBox<>();
        muatFasilitas();

        tambahField(form, "Nama Pelanggan:", inputNama);
        tambahField(form, "Jenis Fasilitas:", pilihFasilitas);
        tambahField(form, "Durasi (Jam):", inputDurasi);

        JButton pesan = tombol("Pesan", BLUE);
        JPanel formArea = new JPanel(new BorderLayout(10, 10));
        formArea.setBackground(PANEL);
        formArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        formArea.add(form, BorderLayout.CENTER);
        formArea.add(pesan, BorderLayout.EAST);

        // TABEL RIWAYAT
        String[] kolom = {"ID", "ID Fasilitas", "No", "Nama Pelanggan",
                "Jenis Fasilitas", "Tarif/Jam", "Durasi",
                "Total Harga", "Tanggal", "Status"};

        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        tabel.setRowHeight(27);
        tabel.setFont(new Font("Arial", Font.PLAIN, 13));
        tabel.setBackground(PANEL);
        tabel.setForeground(WHITE);
        tabel.setGridColor(new Color(71, 85, 105));
        tabel.setSelectionBackground(BLUE);
        tabel.setSelectionForeground(Color.WHITE);
        tabel.setFillsViewportHeight(true);

        JTableHeader header = tabel.getTableHeader();
        header.setBackground(BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 32));

        // Sembunyikan kolom ID transaksi dan ID fasilitas.
        tabel.removeColumn(tabel.getColumnModel().getColumn(0));
        tabel.removeColumn(tabel.getColumnModel().getColumn(0));

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(CYAN));

        JLabel historyTitle = new JLabel("RIWAYAT TRANSAKSI");
        historyTitle.setForeground(CYAN);
        historyTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JButton ubah = tombol("Ubah", BLUE);
        JButton hapus = tombol("Hapus", new Color(185, 28, 28));
        JButton refresh = tombol("Refresh", new Color(71, 85, 105));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(NAVY);
        actions.add(ubah);
        actions.add(hapus);
        actions.add(refresh);

        JPanel history = new JPanel(new BorderLayout(8, 8));
        history.setBackground(NAVY);
        history.add(historyTitle, BorderLayout.NORTH);
        history.add(scroll, BorderLayout.CENTER);
        history.add(actions, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setBackground(NAVY);
        center.add(formArea, BorderLayout.NORTH);
        center.add(history, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        tampilkanData();

        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                int row = tabel.convertRowIndexToModel(tabel.getSelectedRow());
                inputNama.setText(model.getValueAt(row, 3).toString());
                inputDurasi.setText(model.getValueAt(row, 6).toString());
                pilihFasilitasById(
                        Integer.parseInt(model.getValueAt(row, 1).toString()));
            }
        });

        pesan.addActionListener(e -> simpanTransaksi());
        ubah.addActionListener(e -> ubahTransaksi());
        hapus.addActionListener(e -> hapusTransaksi());
        refresh.addActionListener(e -> {
            bersihkanForm();
            tampilkanData();
        });
    }

    private void tambahField(JPanel panel, String text, JComponent field) {
        JLabel label = new JLabel(text);
        label.setForeground(WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(label);
        panel.add(field);

        if (field instanceof JTextField) {
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            field.setBackground(new Color(248, 250, 252));
        } else {
            field.setFont(new Font("Arial", Font.PLAIN, 13));
        }
    }

    private JButton tombol(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private boolean validasi() {
        if (inputNama.getText().trim().isEmpty()) {
            pesan("Nama pelanggan harus diisi!");
            return false;
        }
        if (pilihFasilitas.getSelectedItem() == null) {
            pesan("Pilih fasilitas terlebih dahulu!");
            return false;
        }
        try {
            if (Integer.parseInt(inputDurasi.getText().trim()) <= 0) {
                pesan("Durasi harus lebih dari 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            pesan("Durasi harus berupa angka!");
            return false;
        }
        return true;
    }

    private void simpanTransaksi() {
        if (!validasi()) return;

        Facility f = (Facility) pilihFasilitas.getSelectedItem();
        int durasi = Integer.parseInt(inputDurasi.getText().trim());
        String sql = "INSERT INTO tb_transaksi "
                + "(id_fasilitas, nama_pelanggan, durasi, tarif_per_jam, total_bayar) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = KoneksiJava.getKoneksi();
             PreparedStatement p = c.prepareStatement(sql)) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            p.setInt(1, f.id);
            p.setString(2, inputNama.getText().trim());
            p.setInt(3, durasi);
            p.setDouble(4, f.tarif);
            p.setDouble(5, f.tarif * durasi);
            p.executeUpdate();

            pesan("Booking berhasil disimpan!");
            bersihkanForm();
            tampilkanData();
        } catch (Exception e) {
            pesan("Gagal menyimpan: " + e.getMessage());
        }
    }

    private void ubahTransaksi() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            pesan("Pilih transaksi yang ingin diubah!");
            return;
        }
        if (!validasi()) return;

        int r = tabel.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        Facility f = (Facility) pilihFasilitas.getSelectedItem();
        int durasi = Integer.parseInt(inputDurasi.getText().trim());

        String sql = "UPDATE tb_transaksi SET id_fasilitas=?, nama_pelanggan=?, "
                + "durasi=?, tarif_per_jam=?, total_bayar=? WHERE id_transaksi=?";

        try (Connection c = KoneksiJava.getKoneksi();
             PreparedStatement p = c.prepareStatement(sql)) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            p.setInt(1, f.id);
            p.setString(2, inputNama.getText().trim());
            p.setInt(3, durasi);
            p.setDouble(4, f.tarif);
            p.setDouble(5, f.tarif * durasi);
            p.setInt(6, id);
            p.executeUpdate();

            pesan("Transaksi berhasil diperbarui!");
            bersihkanForm();
            tampilkanData();
        } catch (Exception e) {
            pesan("Gagal mengubah transaksi: " + e.getMessage());
        }
    }

    private void hapusTransaksi() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            pesan("Pilih transaksi yang ingin dihapus!");
            return;
        }

        if (JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus transaksi ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        int r = tabel.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());

        try (Connection c = KoneksiJava.getKoneksi();
             PreparedStatement p = c.prepareStatement(
                     "DELETE FROM tb_transaksi WHERE id_transaksi=?")) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            p.setInt(1, id);
            p.executeUpdate();

            pesan("Transaksi berhasil dihapus!");
            bersihkanForm();
            tampilkanData();
        } catch (Exception e) {
            pesan("Gagal menghapus transaksi: " + e.getMessage());
        }
    }

    private void muatFasilitas() {
        pilihFasilitas.removeAllItems();
        String sql = "SELECT id_fasilitas, nama, jenis, tarif_per_jam FROM tb_fasilitas";

        try (Connection c = KoneksiJava.getKoneksi()) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            try (Statement s = c.createStatement();
                 ResultSet r = s.executeQuery(sql)) {
                while (r.next()) {
                    pilihFasilitas.addItem(new Facility(
                            r.getInt("id_fasilitas"),
                            r.getString("nama"),
                            r.getString("jenis"),
                            r.getDouble("tarif_per_jam")));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat fasilitas: " + e.getMessage());
        }
    }

    private void pilihFasilitasById(int id) {
        for (int i = 0; i < pilihFasilitas.getItemCount(); i++) {
            if (pilihFasilitas.getItemAt(i).id == id) {
                pilihFasilitas.setSelectedIndex(i);
                return;
            }
        }
    }

    private void tampilkanData() {
        model.setRowCount(0);
        String sql = "SELECT t.id_transaksi, t.id_fasilitas, t.nama_pelanggan, "
                + "f.jenis, t.tarif_per_jam, t.durasi, t.total_bayar, t.tanggal "
                + "FROM tb_transaksi t LEFT JOIN tb_fasilitas f "
                + "ON t.id_fasilitas=f.id_fasilitas "
                + "ORDER BY t.id_transaksi DESC";

        try (Connection c = KoneksiJava.getKoneksi()) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            try (Statement s = c.createStatement();
                 ResultSet r = s.executeQuery(sql)) {
                int no = 1;
                while (r.next()) {
                    String jenis = r.getString("jenis");
                    if (jenis == null) jenis = "Fasilitas tidak tersedia";

                    model.addRow(new Object[]{
                            r.getInt("id_transaksi"),
                            r.getInt("id_fasilitas"),
                            no++,
                            r.getString("nama_pelanggan"),
                            jenis,
                            r.getDouble("tarif_per_jam"),
                            r.getInt("durasi"),
                            r.getDouble("total_bayar"),
                            r.getTimestamp("tanggal"),
                            "Booking"
                    });
                }
            }
        } catch (Exception e) {
            pesan("Gagal menampilkan riwayat: " + e.getMessage());
        }
    }

    private void bersihkanForm() {
        inputNama.setText("");
        inputDurasi.setText("");
        tabel.clearSelection();
        if (pilihFasilitas.getItemCount() > 0) pilihFasilitas.setSelectedIndex(0);
    }

    private void pesan(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    // LOGIN ADMIN
    private static void tampilkanLogin() {
        JFrame login = new JFrame("Login Admin");
        login.setSize(380, 280);
        login.setDefaultCloseOperation(EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 14));
        panel.setBackground(NAVY);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("LOGIN ADMIN");
        title.setForeground(CYAN);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userLabel.setForeground(WHITE);
        passLabel.setForeground(WHITE);

        panel.add(title);
        panel.add(new JLabel(""));
        panel.add(userLabel);
        panel.add(username);
        panel.add(passLabel);
        panel.add(password);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        loginButton.addActionListener(e -> {
            String sql = "SELECT * FROM tb_admin WHERE username=? AND password=?";

            try (Connection c = KoneksiJava.getKoneksi()) {
                if (c == null) throw new SQLException("Koneksi database gagal!");
                try (PreparedStatement p = c.prepareStatement(sql)) {
                    p.setString(1, username.getText().trim());
                    p.setString(2, new String(password.getPassword()));

                    try (ResultSet r = p.executeQuery()) {
                        if (r.next()) {
                            login.dispose();
                            new MainFrame().setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(login,
                                    "Username atau password salah!");
                            password.setText("");
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(login,
                        "Error login: " + ex.getMessage());
            }
        });

        login.setContentPane(panel);
        login.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::tampilkanLogin);
    }
}
