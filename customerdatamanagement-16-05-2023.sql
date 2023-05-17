/*
SQLyog Ultimate v12.5.1 (64 bit)
MySQL - 10.4.20-MariaDB : Database - customerdatamanagement
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`customerdatamanagement` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `customerdatamanagement`;

/*Table structure for table `admin` */

DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `id_admin` int(3) NOT NULL AUTO_INCREMENT,
  `nama` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_admin`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

/*Data for the table `admin` */

insert  into `admin`(`id_admin`,`nama`,`email`,`password`) values 
(1,'admin','admin@gmail.com','admin'),
(2,'Timur','timur@gmail.com','timur');

/*Table structure for table `informasi_perusahaan` */

DROP TABLE IF EXISTS `informasi_perusahaan`;

CREATE TABLE `informasi_perusahaan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nama_perusahaan` varchar(50) DEFAULT NULL,
  `alamat` varchar(100) DEFAULT NULL,
  `no_tlp` varchar(13) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `logo` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `informasi_perusahaan` */

/*Table structure for table `jadwal_sales` */

DROP TABLE IF EXISTS `jadwal_sales`;

CREATE TABLE `jadwal_sales` (
  `id_jadwal` int(11) NOT NULL AUTO_INCREMENT,
  `id_sales` int(3) DEFAULT NULL,
  `id_pelanggan` int(5) DEFAULT NULL,
  `kegiatan` text DEFAULT NULL,
  `waktu` datetime DEFAULT NULL,
  PRIMARY KEY (`id_jadwal`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

/*Data for the table `jadwal_sales` */

insert  into `jadwal_sales`(`id_jadwal`,`id_sales`,`id_pelanggan`,`kegiatan`,`waktu`) values 
(1,1,1,'Iseng','2023-04-11 00:00:00'),
(2,3,2,'Wadadaw','2023-04-18 00:00:00'),
(3,1,2,'Apa yak','2023-04-14 00:00:00'),
(4,1,6,'Paparan Kesiapan Pelatihan CEH','2023-05-31 00:00:00');

/*Table structure for table `layanan` */

DROP TABLE IF EXISTS `layanan`;

CREATE TABLE `layanan` (
  `id_layanan` int(5) NOT NULL AUTO_INCREMENT,
  `nama` varchar(50) DEFAULT NULL,
  `deskripsi` varchar(50) DEFAULT NULL,
  `harga` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_layanan`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;

/*Data for the table `layanan` */

insert  into `layanan`(`id_layanan`,`nama`,`deskripsi`,`harga`) values 
(1,'Vulnerability Assessment','Jasa Penilaian Celah Kerentanan',35000000),
(2,'Penetration Test','Jasa Pengujian Celah Kerentanan',50000000),
(3,'SOC Small Business','Jasa Monitoring Keamanan Jaringan untuk selama sat',300000000),
(4,'SOC Medium Business','Jasa Monitoring Keamanan Jaringan selama satu tahu',500000000),
(5,'Game Bug Hunting','Jasa Pengujian Bug pada Game',100000000),
(6,'Red Team','Jasa Pengujian Keamanan terhadap seluruh infrastru',150000000),
(7,'Pelatihan dan Sertifikasi CEH','Jasa pelatihan dan sertifikasi CEH',35000000);

/*Table structure for table `pelanggan` */

DROP TABLE IF EXISTS `pelanggan`;

CREATE TABLE `pelanggan` (
  `id_pelanggan` int(5) NOT NULL AUTO_INCREMENT,
  `nik` varchar(20) DEFAULT NULL,
  `nama_pic` varchar(50) DEFAULT NULL,
  `nama_instansi` varchar(50) DEFAULT NULL,
  `no_tlp` varchar(13) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_pelanggan`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

/*Data for the table `pelanggan` */

insert  into `pelanggan`(`id_pelanggan`,`nik`,`nama_pic`,`nama_instansi`,`no_tlp`,`email`) values 
(1,'3522241907910001','Dedi','PT Sinalum Abadi','081122334455','dedi@google.com'),
(2,'3522240407540003','Hamdan','PT. Teknologi Mitra Mandiri','081278565890','hamdan@gmail.com'),
(3,'3522245606830003','YETI MARIANA ','Kementerian Marves','087798495969','yeti@marves.go.id'),
(4,'3522244309030001','ADELINA ADINDA PUTRI','Kementerian Keuangan','087798495969','adelina@kemenkeu.go.id'),
(5,'3622242301150002 ','PANDU ','HiPajak','085687869876','pandu@hipajak.id'),
(6,'3522242405970001','AHMAD SAIFUDIN ','Naval CSIRT','085647754488','ahmad@naval-csirt.id');

/*Table structure for table `pembayaran` */

DROP TABLE IF EXISTS `pembayaran`;

CREATE TABLE `pembayaran` (
  `id_pembayaran` int(11) NOT NULL AUTO_INCREMENT,
  `id_transaksi` int(11) DEFAULT NULL,
  `jumlah_pembayaran` int(11) DEFAULT NULL,
  `tgl_bayar` date DEFAULT NULL,
  `keterangan` varchar(255) DEFAULT NULL,
  `id_admin` int(11) NOT NULL,
  PRIMARY KEY (`id_pembayaran`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Data for the table `pembayaran` */

insert  into `pembayaran`(`id_pembayaran`,`id_transaksi`,`jumlah_pembayaran`,`tgl_bayar`,`keterangan`,`id_admin`) values 
(1,1,10000,'2023-05-01','test',1);

/*Table structure for table `sales` */

DROP TABLE IF EXISTS `sales`;

CREATE TABLE `sales` (
  `id_sales` int(3) NOT NULL AUTO_INCREMENT,
  `nama` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_sales`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;

/*Data for the table `sales` */

insert  into `sales`(`id_sales`,`nama`,`email`,`password`) values 
(1,'Hakim Aminudin','hakim@maxplus.com','sales'),
(3,'Sandi Nugraha','sandi@maxplus.com','sales'),
(4,'Idrus','idrus@maxplus.com','sales'),
(5,'Faisal Hidayat','faisal@maxplus.com','sales'),
(6,'Hamdan Maulani','hamdan@maxplus.com','sales');

/*Table structure for table `transaksi` */

DROP TABLE IF EXISTS `transaksi`;

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL AUTO_INCREMENT,
  `tanggal_mulai` datetime DEFAULT NULL,
  `tanggal_selesai` datetime DEFAULT NULL,
  `id_pelanggan` int(5) DEFAULT NULL,
  `id_layanan` int(5) DEFAULT NULL,
  `id_sales` int(3) DEFAULT NULL,
  `total_harga` int(11) DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id_transaksi`),
  KEY `id_pelanggan` (`id_pelanggan`),
  KEY `id_layanan` (`id_layanan`),
  KEY `id_sales` (`id_sales`),
  CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan` (`id_pelanggan`),
  CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`id_layanan`) REFERENCES `layanan` (`id_layanan`),
  CONSTRAINT `transaksi_ibfk_3` FOREIGN KEY (`id_sales`) REFERENCES `sales` (`id_sales`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;

/*Data for the table `transaksi` */

insert  into `transaksi`(`id_transaksi`,`tanggal_mulai`,`tanggal_selesai`,`id_pelanggan`,`id_layanan`,`id_sales`,`total_harga`,`status`,`created_at`,`updated_at`) values 
(1,'2022-08-19 00:00:00','2022-08-26 00:00:00',1,1,1,35000000,'','2022-08-26 14:10:53',NULL),
(3,'2022-08-21 00:00:00','2022-08-28 00:00:00',2,3,1,50000000,'','2022-08-28 14:06:24',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
