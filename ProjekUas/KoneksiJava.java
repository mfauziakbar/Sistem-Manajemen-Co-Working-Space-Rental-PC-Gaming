package ProjekUas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiJava {

    public static Connection getKoneksi() {
        try {
            String url = "jdbc:mysql://localhost:3306/db_coworking";
            String username = "root";
            String password = "";

            return DriverManager.getConnection(
                url, username, password
            );

        } catch (SQLException e) {
            System.out.println("Koneksi Database Gagal: " + e.getMessage());
            return null;
        }
    }
}
