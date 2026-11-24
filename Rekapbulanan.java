import javax.swing.*;
import java.sql.*;
import java.time.YearMonth;
import java.text.NumberFormat;
import java.util.Locale;

public class Rekapbulanan {

    private final NumberFormat rupiah =
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        
    public void tampilkanRekap(JFrame parent) {

        String bulanInput = JOptionPane.showInputDialog(
                null, "Masukkan bulan (1-12):"
        );

        if (bulanInput == null) return;

        String tahunInput = JOptionPane.showInputDialog(
                null, "Masukkan tahun:"
        );

        if (tahunInput == null) return;

        try {
            int bulan = Integer.parseInt(bulanInput);
            int tahun = Integer.parseInt(tahunInput);

            if (bulan < 1 || bulan > 12 || tahun < 1) {
                throw new IllegalArgumentException();
            }

            YearMonth periode = YearMonth.of(tahun, bulan);

            hitungRekap(periode);

       } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    null, "Bulan atau tahun tidak valid!"
            );
        }
    }

    private void hitungRekap(YearMonth periode) {

        String sqlTotal =
                "SELECT COUNT(*) AS jumlah, " +
                "COALESCE(SUM(durasi), 0) AS total_jam, " +
                "COALESCE(SUM(total_bayar), 0) AS pendapatan " +
                "FROM tb_transaksi " +
                "WHERE tanggal >= ? AND tanggal < ?";

        String sqlJenis =
                "SELECT f.jenis, " +
                "COUNT(t.id_transaksi) AS jumlah, " +
                "COALESCE(SUM(t.total_bayar), 0) AS pendapatan " +
                "FROM tb_transaksi t " +
                "LEFT JOIN tb_fasilitas f " +
                "ON t.id_fasilitas = f.id_fasilitas " +
                "WHERE t.tanggal >= ? AND t.tanggal < ? " +
                "GROUP BY f.jenis";

        try (Connection conn = KoneksiJava.getKoneksi()) {

            if (conn == null) {
                throw new SQLException("Koneksi database gagal!");
            }

            int jumlah = 0;
            int totalJam = 0;
            double pendapatan = 0;

            try (PreparedStatement ps =
                         conn.prepareStatement(sqlTotal)) {

                ps.setDate(1, Date.valueOf(periode.atDay(1)));
                ps.setDate(2, Date.valueOf(periode.plusMonths(1).atDay(1)));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        jumlah = rs.getInt("jumlah");
                        totalJam = rs.getInt("total_jam");
                        pendapatan = rs.getDouble("pendapatan");
                    }
                }
            }

            StringBuilder hasil = new StringBuilder();

            hasil.append("REKAP BULANAN\n");
            hasil.append("============================\n");
            hasil.append("Periode: ")
                 .append(periode.getMonthValue())
                 .append("/")
                 .append(periode.getYear())
                 .append("\n\n");

            hasil.append("Jumlah Transaksi : ")
                 .append(jumlah).append("\n");

            hasil.append("Total Durasi     : ")
                 .append(totalJam).append(" jam\n");

            hasil.append("Total Pendapatan : ")
                 .append(rupiah.format(pendapatan)).append("\n");

            double rataRata = jumlah > 0
                    ? pendapatan / jumlah : 0;

            hasil.append("Rata-rata        : ")
                 .append(rupiah.format(rataRata))
                 .append("\n\n");

            hasil.append("PENDAPATAN PER JENIS\n");
            hasil.append("============================\n");

            try (PreparedStatement ps =
                         conn.prepareStatement(sqlJenis)) {

                ps.setDate(1, Date.valueOf(periode.atDay(1)));
                ps.setDate(2, Date.valueOf(periode.plusMonths(1).atDay(1)));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {

                        String jenis = rs.getString("jenis");

                        if (jenis == null) {
                            jenis = "Fasilitas tidak tersedia";
                        }

                        hasil.append(jenis)
                             .append("\nTransaksi: ")
                             .append(rs.getInt("jumlah"))
                             .append("\nPendapatan: ")
                             .append(rupiah.format(
                                     rs.getDouble("pendapatan")))
                             .append("\n\n");
                    }
                }
            }

            JTextArea area = new JTextArea(hasil.toString());
            area.setEditable(false);
            area.setFont(new java.awt.Font(
                    "Monospaced", java.awt.Font.PLAIN, 13
            ));

            JOptionPane.showMessageDialog(
                    null,
                    new JScrollPane(area),
                    "Rekap Bulanan",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Gagal mengambil rekap: " + e.getMessage()
            );
        }
    }
}
