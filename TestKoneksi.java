import java.sql.Connection;

public class TestKoneksi {
    public static void main(String[] args) {
        Connection conn = KoneksiJava.getKoneksi();

        if (conn != null) {
            System.out.println("Koneksi Database Berhasil!");
        } else {
            System.out.println("Koneksi Database Gagal!");
        }
    }
}