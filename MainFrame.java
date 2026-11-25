import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainFrame extends JFrame {

    // warna background sama panel
    private static final Color BACKGROUND = new Color(23, 26, 33);
    private static final Color PANEL = new Color(34, 38, 48);
    private static final Color INPUT = new Color(44, 48, 59);
    private static final Color HEADER = new Color(48, 54, 67);
    private static final Color BORDER = new Color(53, 58, 70);

    // warna teks
    private static final Color TEXT = new Color(230, 232, 237);
    private static final Color TEXT_SECONDARY = new Color(160, 166, 180);

    // warna tombol
    private static final Color MINT = new Color(55, 183, 160);
    private static final Color BLUE = new Color(59, 130, 246);
    private static final Color RED = new Color(217, 76, 89);
    private static final Color SLATE = new Color(70, 80, 99);

    // warna baris tabel
    private static final Color ROW_ALTERNATE = new Color(39, 43, 53);
    private static final Color ROW_SELECTED = new Color(52, 74, 73);

    // komponen utama tampilan
    private DefaultTableModel model;
    private JTable tabel;
    private JComboBox<Facility> pilihFasilitas;
    private JTextField inputNama, inputDurasi;
    private final Rekapbulanan rekap = new Rekapbulanan();

    // class bantu buat nampung data fasilitas di dropdown
    private static class Facility {
        int id;
        String nama, jenis, status;
        double tarif;

        Facility(int id, String nama, String jenis, double tarif, String status) {
            this.id = id;
            this.nama = nama;
            this.jenis = jenis;
            this.tarif = tarif;
            this.status = status;
        }

        // format teks yang muncul di list dropdown
        public String toString() {
            return "[" + status + "] " + nama + " - Rp " + (int) tarif + "/jam";
        }
    }

    // pengaturan tampilan utama aplikasi
    public MainFrame() {
        setTitle("Co-Working Space & Rental PC Gaming");
        setSize(1050, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        // judul atas
        JLabel title = new JLabel("CO-WORKING SPACE & RENTAL PC GAMING",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(MINT);
        root.add(title, BorderLayout.NORTH);

        // FORM BOOKING
        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.setBackground(PANEL);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER), "FORM BOOKING",
                0, 0, new Font("Arial", Font.BOLD, 14), TEXT));

        inputNama = new JTextField();
        inputDurasi = new JTextField();
        pilihFasilitas = new JComboBox<>();
        
        // ubah warna tombol panah dropdown biar beda sendiri
        pilihFasilitas.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton panah = new JButton("▼");
                panah.setBackground(MINT); 
                panah.setForeground(TEXT);
                panah.setFocusPainted(false);
                panah.setBorder(BorderFactory.createEmptyBorder());
                return panah;
            }
        });
        muatFasilitas();

        tambahField(form, "Nama Pelanggan:", inputNama);
        tambahField(form, "Jenis Fasilitas:", pilihFasilitas);
        tambahField(form, "Durasi (Jam):", inputDurasi);

        JButton pesan = tombol("Pesan", MINT);
        JPanel formArea = new JPanel(new BorderLayout(10, 10));
        formArea.setBackground(PANEL);
        formArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        formArea.add(form, BorderLayout.CENTER);
        formArea.add(pesan, BorderLayout.EAST);

        // TABEL RIWAYAT
        String[] kolom = {"ID", "ID Fasilitas", "No", "Nama Pelanggan",
                "Jenis Fasilitas", "Tarif/Jam", "Durasi",
                "Total Harga", "Tanggal", "Status"};
        
        // kunci sel tabel biar ga bisa diedit langsung
        model = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabel = new JTable(model);
        tabel.setRowHeight(27);
        tabel.setFont(new Font("Arial", Font.PLAIN, 13));
        tabel.setBackground(PANEL);
        tabel.setForeground(TEXT);
        tabel.setGridColor(BORDER);
        tabel.setSelectionBackground(ROW_SELECTED);
        tabel.setSelectionForeground(TEXT);
        tabel.setFillsViewportHeight(true);

        // warna baris tabel selang-seling
        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? ROW_SELECTED : (row % 2 == 0 ? PANEL : ROW_ALTERNATE));
                c.setForeground(TEXT);
                return c;
            }
        });

        JTableHeader header = tabel.getTableHeader();
        header.setBackground(HEADER);
        header.setForeground(TEXT);
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 32));

        // Sembunyikan kolom ID transaksi dan ID fasilitas.
        tabel.removeColumn(tabel.getColumnModel().getColumn(0));
        tabel.removeColumn(tabel.getColumnModel().getColumn(0));

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.getViewport().setBackground(PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));

        JLabel historyTitle = new JLabel("RIWAYAT TRANSAKSI");
        historyTitle.setForeground(TEXT);
        historyTitle.setFont(new Font("Arial", Font.BOLD, 16));

        // tombol-tombol aksi di bawah tabel
        JButton ubah = tombol("Ubah", BLUE);
        JButton hapus = tombol("Hapus", RED);
        JButton refresh = tombol("Refresh", SLATE);
        JButton cetak = tombol("Cetak Struk", MINT);
        JButton rekapButton = tombol("Rekap Bulanan", BLUE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBackground(BACKGROUND);
        actions.add(ubah);
        actions.add(hapus);
        actions.add(refresh);
        actions.add(cetak);
        actions.add(rekapButton);

        JPanel history = new JPanel(new BorderLayout(8, 8));
        history.setBackground(BACKGROUND);
        history.add(historyTitle, BorderLayout.NORTH);
        history.add(scroll, BorderLayout.CENTER);
        history.add(actions, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setBackground(BACKGROUND);
        center.add(formArea, BorderLayout.NORTH);
        center.add(history, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        // muat data awal & nyalakan timer
        tampilkanData();
        jalankanTimer();

        // isi form otomatis pas baris tabel diklik
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                int row = tabel.convertRowIndexToModel(tabel.getSelectedRow());
                inputNama.setText(model.getValueAt(row, 3).toString());
                inputDurasi.setText(model.getValueAt(row, 6).toString());
                pilihFasilitasById(
                        Integer.parseInt(model.getValueAt(row, 1).toString()));
            }
        });

        // hubungkan tombol ke fungsinya masing-masing
        pesan.addActionListener(e -> simpanTransaksi());
        ubah.addActionListener(e -> ubahTransaksi());
        hapus.addActionListener(e -> hapusTransaksi());
        refresh.addActionListener(e -> {
            bersihkanForm();
            tampilkanData();
        });
        cetak.addActionListener(e -> cetakStruk());
        rekapButton.addActionListener(e -> rekap.tampilkanRekap(this));
    }

    // helper buat pasang label + kotak input ke panel form
    private void tambahField(JPanel panel, String text, JComponent field) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SECONDARY);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(label);
        panel.add(field);

        field.setBackground(INPUT);
        field.setForeground(TEXT);
        if (field instanceof JTextField) {
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            ((JTextField) field).setCaretColor(TEXT);
        } else {
            field.setFont(new Font("Arial", Font.PLAIN, 13));
        }
    }

    // helper buat bikin tombol dengan desain seragam
    private JButton tombol(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(TEXT);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // cek input form sebelum simpan/ubah
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

    // FITUR CREATE: simpan booking baru ke database
    private void simpanTransaksi() {
        if (!validasi()) return;

        Facility f = (Facility) pilihFasilitas.getSelectedItem();
        if (f.status.equalsIgnoreCase("Disewa")) {
            pesan("Fasilitas ini sedang disewa! Pilih fasilitas lain.");
            return;
        }
        int durasi = Integer.parseInt(inputDurasi.getText().trim());

        // PBO Polymorphism: buat objek subclass sesuai jenis fasilitas
        Fasilitas fasilitasPilihan;
            if (f.jenis.equalsIgnoreCase("PC Gaming")) {
                fasilitasPilihan = new PCGaming(f.nama, f.jenis, f.tarif, "Disewa");
            } else if (f.nama.toLowerCase().contains("meeting")) {
                fasilitasPilihan = new MeetingRoom(f.nama, f.jenis, f.tarif, "Disewa");
            } else {
                fasilitasPilihan = new MejaDesk(f.nama, f.jenis, f.tarif, "Disewa");
            }

            // hitung total bayar lewat method override di masing-masing subclass
            double totalHargaPBO = fasilitasPilihan.hitungTarif(durasi);
            
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
            p.setDouble(5, totalHargaPBO);
            p.executeUpdate();
            ubahStatusFasilitas(f.id, "Disewa"); // tandai unit jadi Disewa

            pesan("Booking berhasil disimpan!");
            muatFasilitas();
            bersihkanForm();
            tampilkanData();
        } catch (Exception e) {
            pesan("Gagal menyimpan: " + e.getMessage());
        }
    }

    // FITUR UPDATE: edit transaksi yang dipilih di tabel
    private void ubahTransaksi() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            pesan("Pilih transaksi yang ingin diubah!");
            return;
        }
        if (!validasi()) return;

        int r = tabel.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(r, 0).toString());
        int idFasilitasLama = Integer.parseInt(model.getValueAt(r, 1).toString());
        Facility f = (Facility) pilihFasilitas.getSelectedItem();
        
        // cegah pindah ke unit lain yang lagi disewa
        if (f.id != idFasilitasLama && f.status.equalsIgnoreCase("Disewa")) {
            pesan("Fasilitas baru sedang disewa! Pilih fasilitas lain.");
            return;
        }
        int durasi = Integer.parseInt(inputDurasi.getText().trim());

        // hitung ulang tarif pakai PBO
        Fasilitas fasilitasPilihan;
        if (f.jenis.equalsIgnoreCase("PC Gaming")) {
            fasilitasPilihan = new PCGaming(f.nama, f.jenis, f.tarif, "Disewa");
        } else if (f.nama.toLowerCase().contains("meeting")) {
            fasilitasPilihan = new MeetingRoom(f.nama, f.jenis, f.tarif, "Disewa");
        } else {
            fasilitasPilihan = new MejaDesk(f.nama, f.jenis, f.tarif, "Disewa");
        }

        double totalHargaPBO = fasilitasPilihan.hitungTarif(durasi);

        String sql = "UPDATE tb_transaksi SET id_fasilitas=?, nama_pelanggan=?, "
                + "durasi=?, tarif_per_jam=?, total_bayar=? WHERE id_transaksi=?";

        try (Connection c = KoneksiJava.getKoneksi();
            PreparedStatement p = c.prepareStatement(sql)) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            p.setInt(1, f.id);
            p.setString(2, inputNama.getText().trim());
            p.setInt(3, durasi);
            p.setDouble(4, f.tarif);
            p.setDouble(5, totalHargaPBO); // Pakai hasil hitungan PBO, bukan f.tarif * durasi
            p.setInt(6, id);
            p.executeUpdate();

            // kalau ganti unit & sewanya masih aktif, tukar status unit lama dan baru
            if (idFasilitasLama != f.id) {
                if (model.getValueAt(r, 9).toString().startsWith("Aktif")) {
                    ubahStatusFasilitas(idFasilitasLama, "Tersedia");
                    ubahStatusFasilitas(f.id, "Disewa");
                }
            muatFasilitas();
        }

            pesan("Transaksi berhasil diperbarui!");
            bersihkanForm();
            tampilkanData();
        } catch (Exception e) {
            pesan("Gagal mengubah transaksi: " + e.getMessage());
        }
    }

    // FITUR DELETE: hapus transaksi yang dipilih
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
            int idFasilitas = Integer.parseInt(model.getValueAt(r, 1).toString());
            p.executeUpdate();

            // balikin status jadi Tersedia cuma kalau transaksi yang dihapus masih aktif
            if (model.getValueAt(r, 9).toString().startsWith("Aktif")) {
                ubahStatusFasilitas(idFasilitas, "Tersedia");
            }
            muatFasilitas();

            pesan("Transaksi berhasil dihapus!");
            bersihkanForm();
            tampilkanData();
        } catch (Exception e) {
            pesan("Gagal menghapus transaksi: " + e.getMessage());
        }
    }

    // isi dropdown fasilitas, yang Tersedia ditaruh paling atas
    private void muatFasilitas() {
        pilihFasilitas.removeAllItems();
        String sql = "SELECT id_fasilitas, nama, jenis, tarif_per_jam, status "
                + "FROM tb_fasilitas "
                + "ORDER BY CASE WHEN status = 'Tersedia' THEN 1 ELSE 2 END, "
                + "CASE "
                + "WHEN nama LIKE 'PC Gaming%' THEN 1 "
                + "WHEN nama LIKE 'Ruang Meeting%' THEN 2 "
                + "ELSE 3 END, nama ASC";
        try (Connection c = KoneksiJava.getKoneksi()) {
            if (c == null) throw new SQLException("Koneksi database gagal!");
            try (Statement s = c.createStatement();
                ResultSet r = s.executeQuery(sql)) {
                while (r.next()) {
                    pilihFasilitas.addItem(new Facility(
                            r.getInt("id_fasilitas"),
                            r.getString("nama"),
                            r.getString("jenis"),
                            r.getDouble("tarif_per_jam"),
                            r.getString("status")));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat fasilitas: " + e.getMessage());
        }
        pilihFasilitas.setSelectedIndex(-1); // kosongkan pilihan awal
    }

    // update status Tersedia/Disewa di tabel tb_fasilitas
    private void ubahStatusFasilitas(int idFasilitas, String statusBaru) {
        String sql = "UPDATE tb_fasilitas SET status=? WHERE id_fasilitas=?";
        try (Connection c = KoneksiJava.getKoneksi();
            PreparedStatement p = c.prepareStatement(sql)) {
            if (c != null) {
                p.setString(1, statusBaru);
                p.setInt(2, idFasilitas);
                p.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Gagal ubah status: " + e.getMessage());
        }
    }

    // reset semua fasilitas jadi Tersedia kalau tabel transaksi kosong
    private void resetSemuaStatus() {
        try (Connection c = KoneksiJava.getKoneksi();
            Statement s = c.createStatement()) {
            if (c != null) {
                s.executeUpdate("UPDATE tb_fasilitas SET status = 'Tersedia'");
            }
        } catch (Exception e) {
            System.out.println("Gagal reset status: " + e.getMessage());
        }
    }

    // pilih item dropdown sesuai ID fasilitas dari baris tabel yang diklik
    private void pilihFasilitasById(int id) {
        for (int i = 0; i < pilihFasilitas.getItemCount(); i++) {
            if (pilihFasilitas.getItemAt(i).id == id) {
                pilihFasilitas.setSelectedIndex(i);
                return;
            }
        }
    }

    // FITUR READ: ambil data transaksi dari database lalu tampilkan ke tabel
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

        if (model.getRowCount() == 0) {
            resetSemuaStatus();
            muatFasilitas();
        }
    }

    // kosongkan form input & pilihan baris tabel
    private void bersihkanForm() {
        inputNama.setText("");
        inputDurasi.setText("");
        tabel.clearSelection();
        pilihFasilitas.setSelectedIndex(-1); 
    }

    // FITUR TIMER REAL-TIME: hitung mundur durasi tiap 1 detik
    private void jalankanTimer() {
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long waktuSekarang = System.currentTimeMillis();
                // Simpan ID fasilitas yang sudah dicek (karena baris paling atas = transaksi terbaru)
                java.util.ArrayList<Integer> sudahDicek = new java.util.ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    int idFasilitas = Integer.parseInt(model.getValueAt(i, 1).toString());
                    Timestamp tgl = (Timestamp) model.getValueAt(i, 8);
                    int durasiJam = Integer.parseInt(model.getValueAt(i, 6).toString());

                    if (tgl != null) {
                        // hitung batas waktu selesai (waktu booking + durasi dalam milidetik)
                        long waktuSelesai = tgl.getTime() + (durasiJam * 3600L * 1000L);
                        long sisaDetik = (waktuSelesai - waktuSekarang) / 1000;

                        if (sisaDetik > 0) {
                            // kalau waktu masih sisa, tampilkan hitung mundur
                            long jam = sisaDetik / 3600;
                            long menit = (sisaDetik % 3600) / 60;
                            long detik = sisaDetik % 60;
                            model.setValueAt("Aktif (" + jam + "j " + menit + "m " + detik + "d)", i, 9);
                        } else {
                            // kalau waktu habis, ubah jadi Selesai & balikin unit jadi Tersedia
                            String statusTabel = model.getValueAt(i, 9).toString();
                            if (!statusTabel.equals("Selesai")) {
                                model.setValueAt("Selesai", i, 9);
                                if (!sudahDicek.contains(idFasilitas)) {
                                    ubahStatusFasilitas(idFasilitas, "Tersedia");
                                    muatFasilitas();
                                }
                            }
                        }
                    }
                    sudahDicek.add(idFasilitas);
                }
            }
        });
        timer.start();
    }

    // helper pop-up notifikasi
    private void pesan(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    // FITUR CETAK STRUK: preview struk & kirim ke printer / save PDF
    private void cetakStruk() {
        int row = tabel.getSelectedRow();
        if (row < 0) {
            pesan("Pilih transaksi di tabel dulu buat dicetak!");
            return;
        }

        // ambil data transaksi dari baris yang dipilih
        int r = tabel.convertRowIndexToModel(row);
        String nama = model.getValueAt(r, 3).toString();
        String fasilitas = model.getValueAt(r, 4).toString();
        String tarif = model.getValueAt(r, 5).toString();
        String durasi = model.getValueAt(r, 6).toString();
        String total = model.getValueAt(r, 7).toString();
        String tanggal = model.getValueAt(r, 8).toString();

        // susun tampilan teks struk
        JTextArea struk = new JTextArea();
        struk.setFont(new Font("Monospaced", Font.PLAIN, 12));
        struk.setEditable(false); // Biar teks previewnya gak bisa diedit user
        struk.append("========================================\n");
        struk.append("         STRUK CO-WORKING SPACE         \n");
        struk.append("========================================\n");
        struk.append("Tanggal    : " + tanggal + "\n");
        struk.append("Nama       : " + nama + "\n");
        struk.append("Fasilitas  : " + fasilitas + "\n");
        struk.append("Tarif/Jam  : Rp " + tarif + "\n");
        struk.append("Durasi     : " + durasi + " Jam\n");
        struk.append("----------------------------------------\n");
        struk.append("TOTAL BAYAR: Rp " + total + "\n");
        struk.append("========================================\n");
        struk.append("              Terima Kasih!             \n");

        // Masukkan struk ke dalam JScrollPane untuk preview
        JScrollPane preview = new JScrollPane(struk);
        preview.setPreferredSize(new Dimension(350, 250));

        // tampilkan preview, kalau klik Yes langsung print / save PDF
        int pilihan = JOptionPane.showConfirmDialog(this, preview, 
                "Preview Struk - Print / Simpan PDF?", JOptionPane.YES_NO_OPTION);

        // Kalau user pilih Yes, eksekusi fungsi print
        if (pilihan == JOptionPane.YES_OPTION) {
            try {
                struk.print(); // Otomatis buka dialog OS untuk Print atau Save as PDF
            } catch (Exception e) {
                pesan("Gagal nge-print struk!");
            }
        }
    }

    // FITUR LOGIN ADMIN: cek username & password ke database sebelum buka aplikasi
    private static void tampilkanLogin() {
        JFrame login = new JFrame("Program Co Working & Rental PC Gaming");
        login.setSize(380, 280);
        login.setDefaultCloseOperation(EXIT_ON_CLOSE);
        login.setLocationRelativeTo(null);
        login.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 14));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("LOGIN ADMIN");
        title.setForeground(MINT);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField  username = new JTextField();
        username.setBackground(INPUT);
        username.setForeground(TEXT);
        username.setCaretColor(TEXT);

        JPasswordField password = new JPasswordField();
        password.setBackground(INPUT);
        password.setForeground(TEXT);
        password.setCaretColor(TEXT);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(MINT);
        loginButton.setForeground(TEXT);
        loginButton.setFocusPainted(false);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userLabel.setForeground(TEXT_SECONDARY);
        passLabel.setForeground(TEXT_SECONDARY);

        panel.add(title);
        panel.add(new JLabel(""));
        panel.add(userLabel);
        panel.add(username);
        panel.add(passLabel);
        panel.add(password);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        // proses cek login saat tombol Login diklik
        loginButton.addActionListener(e -> {
            String sql = "SELECT * FROM tb_admin WHERE username=? AND password=?";

            try (Connection c = KoneksiJava.getKoneksi()) {
                if (c == null) throw new SQLException("Koneksi database gagal!");
                try (PreparedStatement p = c.prepareStatement(sql)) {
                    p.setString(1, username.getText().trim());
                    p.setString(2, new String(password.getPassword()));

                    try (ResultSet r = p.executeQuery()) {
                        if (r.next()) {
                            login.dispose(); // tutup window login
                            new MainFrame().setVisible(true); // buka halaman utama
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
    // titik awal program dijalankan
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::tampilkanLogin);
    }
}