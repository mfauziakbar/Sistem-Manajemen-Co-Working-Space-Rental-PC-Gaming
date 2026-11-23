package ProjekUas;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {

        setTitle("Login - Coworking Space");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel utama
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 244, 248));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Judul
        JLabel lblTitle = new JLabel("COWORKING SPACE " + 
        "& RENTAL PC GAMING");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(35, 55, 80));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        // Subjudul
        JLabel lblSubtitle = new JLabel("Silakan Login Terlebih Dahulu");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridy = 1;
        panel.add(lblSubtitle, gbc);

        // Username
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 13));

        gbc.gridy = 2;
        panel.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(280, 35));
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridy = 3;
        panel.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 13));

        gbc.gridy = 4;
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(280, 35));

        gbc.gridy = 5;
        panel.add(txtPassword, gbc);

        // Tombol Login
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 98, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(280, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 6;
        panel.add(btnLogin, gbc);

        add(panel);

        // Event tombol Login
        btnLogin.addActionListener(e -> prosesLogin());

        // Enter untuk login
        txtPassword.addActionListener(e -> prosesLogin());
    }

    private void prosesLogin() {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Username dan password wajib diisi!",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";

        try (Connection conn = KoneksiJava.getKoneksi()) {

            if (conn == null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Koneksi database gagal!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {

                pst.setString(1, username);
                pst.setString(2, password);

                try (ResultSet rs = pst.executeQuery()) {

                    if (rs.next()) {

                        JOptionPane.showMessageDialog(
                            this,
                            "Login berhasil! Selamat datang " + username,
                            "Login Berhasil",
                            JOptionPane.INFORMATION_MESSAGE
                        );

                        // Sementara: menutup LoginFrame.
                        // Nanti diganti dengan membuka MainFrame.
                        dispose();

                    } else {

                        JOptionPane.showMessageDialog(
                            this,
                            "Username atau password salah!",
                            "Login Gagal",
                            JOptionPane.ERROR_MESSAGE
                        );

                        txtPassword.setText("");
                    }
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Terjadi kesalahan: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
