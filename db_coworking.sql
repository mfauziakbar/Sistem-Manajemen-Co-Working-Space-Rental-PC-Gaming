-- 1. INI BUAT DATABASE
CREATE DATABASE IF NOT EXISTS db_coworking;

USE db_coworking;

-- 2. TABEL ADMIN
CREATE TABLE IF NOT EXISTS tb_admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

-- 3. TABEL FASILITAS
CREATE TABLE IF NOT EXISTS tb_fasilitas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    jenis VARCHAR(50) NOT NULL,
    tarif_per_jam DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL
);

-- 4. TABEL TRANSAKSI
CREATE TABLE IF NOT EXISTS tb_transaksi (
    id_transaksi INT AUTO_INCREMENT PRIMARY KEY,
    nama_penyewa VARCHAR(100) NOT NULL,
    id_fasilitas INT NOT NULL,
    durasi INT NOT NULL,
    total_harga DECIMAL(10,2) NOT NULL,
    tanggal DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_fasilitas)
    REFERENCES tb_fasilitas(id)
);

-- 5. DATA ADMIN BUAT LOGIN
INSERT INTO tb_admin (username, password)
VALUES ('admin', '12345');

-- 6. DATA FASILITAS
INSERT INTO tb_fasilitas (nama, jenis, tarif_per_jam, status)
VALUES
('Meja Desk', 'Co-Working', 15000, 'Tersedia'),
('Meeting Room', 'Meeting', 50000, 'Tersedia'),
('PC Gaming', 'Gaming', 10000, 'Tersedia');
