CREATE DATABASE IF NOT EXISTS db_coworking;
USE db_coworking;

CREATE TABLE tb_admin (
  id_admin int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username varchar(50) NOT NULL UNIQUE,
  password varchar(255) NOT NULL
);

CREATE TABLE tb_user (
  id_user int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username varchar(50) NOT NULL UNIQUE,
  password varchar(255) NOT NULL
);

CREATE TABLE tb_fasilitas (
  id_fasilitas int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  nama varchar(100) NOT NULL,
  jenis varchar(50) NOT NULL,
  tarif_per_jam double NOT NULL,
  status varchar(30) NOT NULL
);

CREATE TABLE tb_transaksi (
  id_transaksi int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  id_user int(11) DEFAULT NULL,
  id_fasilitas int(11) DEFAULT NULL,
  nama_pelanggan varchar(100) NOT NULL,
  tanggal datetime DEFAULT current_timestamp(),
  durasi int(11) NOT NULL,
  tarif_per_jam double NOT NULL,
  total_bayar double NOT NULL,
  FOREIGN KEY (id_fasilitas) REFERENCES tb_fasilitas(id_fasilitas) ON DELETE SET NULL ON UPDATE CASCADE,
  FOREIGN KEY (id_user) REFERENCES tb_user(id_user)
);

-- Data awal agar aplikasi bisa langsung di-login dan diuji coba
INSERT INTO tb_admin (username, password) VALUES ('admin', 'admin123');
INSERT INTO tb_user (username, password) VALUES ('admin', '12345');

INSERT INTO tb_fasilitas (nama, jenis, tarif_per_jam, status) VALUES
('PC Gaming 1', 'PC Gaming', 10000, 'Tersedia'),
('PC Gaming 2', 'PC Gaming', 10000, 'Tersedia'),
('PC Gaming 3', 'PC Gaming', 10000, 'Tersedia'),
('Ruang Meeting A', 'Coworking Space', 20000, 'Tersedia'),
('Ruang Meeting B', 'Coworking Space', 20000, 'Tersedia'),
('Meja Desk 1', 'Coworking Space', 15000, 'Tersedia'),
('Meja Desk 2', 'Coworking Space', 15000, 'Tersedia'),
('Meja Desk 3', 'Coworking Space', 15000, 'Tersedia');