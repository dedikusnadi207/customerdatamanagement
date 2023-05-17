/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboardapp;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import model.Layanan;
import model.Pelanggan;
import model.Transaksi;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.lang.time.DateUtils;
import repository.*;
import utils.MapCustom;
import utils.validator;

/**
 *
 * @author KeepToo
 */
public class Home extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    LayananRepository layananRepository;
    PelangganRepository pelangganRepository;
    TransaksiRepository transaksiRepository;
    TransaksiRepository reportRepository;
    TransaksiRepository pembayaranRepository;
    PembayaranRepository pembayaranDetailRepository;
    JadwalSalesRepository jadwalSalesRepository;
    ArrayList<model.Pelanggan> pelangganOptions;
    ArrayList<model.Layanan> layananOptions;
    public Home() {
        initComponents();
        layananRepository = new LayananRepository();
        pelangganRepository = new PelangganRepository();
        transaksiRepository = new TransaksiRepository();
        reportRepository = new TransaksiRepository();
        pembayaranRepository = new TransaksiRepository();
        pembayaranDetailRepository = new PembayaranRepository();
        jadwalSalesRepository = new JadwalSalesRepository(false);
        selectMenu("dashboard");
        JTextField[] fields = {txtNIKPelanggan, txtTeleponPelanggan};
        validator.handleNumberOnly(fields);
    }
    private void selectMenu(String menu) {
        panel_dashboard.setVisible(menu.equals("dashboard"));
        Map<String, JPanel> mapPanel = MapCustom.of("layanan", panel_layanan, "pelanggan", panel_pelanggan, "transaksi", panel_transaksi, "report", panel_report, "jadwal", panel_jadwal, "pembayaran", panel_pembayaran);
        Map<String, JPanel> mapBtnNav = MapCustom.of("layanan", btn_nav_layanan, "pelanggan", btn_nav_pelanggan, "transaksi", btn_nav_tansaksi, "report", btn_nav_report, "jadwal", btn_nav_jadwal, "pembayaran", btn_nav_pembayaran);
        Map<String, Repository> mapRepositories = MapCustom.of("layanan", layananRepository, "pelanggan", pelangganRepository, "transaksi", transaksiRepository, "report", reportRepository, "jadwal", jadwalSalesRepository, "pembayaran", pembayaranRepository);
        Map<String, JTable> mapTable = MapCustom.of("layanan", tabel_layanan, "pelanggan", tabel_pelanggan, "transaksi", tabel_transaksi, "report", tabel_report, "jadwal", tabel_jadwal, "pembayaran", tabel_pembayaran);
        if (menu.equals("dashboard")) {
            try {
                lbl_dashboard_pelanggan.setText(pelangganRepository.count()+"");
                lbl_dashboard_transaksi.setText(transaksiRepository.count()+"");
                transaksiRepository.renderDataTable(tabel_dashboard_transaksi, transaksiRepository.endInDays(30));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Gagal menampilkan data! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (menu.equals("transaksi") || menu.equals("pembayaran")) {
            try {
                pelangganOptions = pelangganRepository.all();
                if (menu.equals("transaksi")) {
                    cPelanggan.removeAllItems();

                    layananOptions = layananRepository.all();
                    cLayanan.removeAllItems();
                    layananOptions.forEach((layananOption) -> {
                        cLayanan.addItem(layananOption.getNama()+" - Rp."+layananOption.getHarga());
                    });
                } else {
                    cPelangganPembayaran.removeAllItems();
                }
                pelangganOptions.forEach((pelangganOption) -> {
                    if (menu.equals("transaksi")) { 
                        cPelanggan.addItem(pelangganOption.getNamaPic()+" - "+pelangganOption.getNamaInstansi());
                    } else {
                        cPelangganPembayaran.addItem(pelangganOption.getNamaPic()+" - "+pelangganOption.getNamaInstansi());
                    }
                });
            } catch (Exception e) {
            }
        } else if (menu.equals("jadwal")) {
            try {
                pelangganOptions = pelangganRepository.all();
                cPelangganJadwal.removeAllItems();
                pelangganOptions.forEach((pelangganOption) -> {
                    cPelangganJadwal.addItem(pelangganOption.getNamaPic()+" - "+pelangganOption.getNamaInstansi());
                });
            } catch (Exception e) {
            }
        }
        mapPanel.keySet().forEach((key) -> {
            if (key.equals(menu)) {
                mapPanel.get(key).setVisible(true);
                onClick(mapBtnNav.get(key));
                if (menu.equals("report")) {
                    tanggalMulaiReport.setDate(DateUtils.addDays(new Date(),-7));
                    tanggalSelesaiReport.setDate(new Date());
                    tampilReport();
                } else if (menu.equals("jadwal")){
                    tanggalMulaiJadwal.setDate(DateUtils.addDays(new Date(),-30));
                    tanggalSelesaiJadwal.setDate(new Date());
                    tampilJadwal();
                } else if (menu.equals("pembayaran")){
                    tampilPembayaran();
                } else {
                    mapRepositories.get(key).renderDataTable(mapTable.get(key));
                }
            } else {
                mapPanel.get(key).setVisible(false);
                onLeaveClick(mapBtnNav.get(key));
            }
        });
    }
    
    void tampilJadwal() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = simpleDateFormat.format(tanggalMulaiJadwal.getDate());
            String endTime = simpleDateFormat.format(tanggalSelesaiJadwal.getDate());
            jadwalSalesRepository.renderDataTable(tabel_jadwal, jadwalSalesRepository.all(Login.sessionSales.getIdSales(), startTime, endTime));
        } catch (Exception ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void tampilReport() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<model.Transaksi> rows = reportRepository.all(simpleDateFormat.format(tanggalMulaiReport.getDate()), simpleDateFormat.format(tanggalSelesaiReport.getDate()));
            reportRepository.renderDataTable(tabel_report, rows);
        } catch (Exception ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void tampilPembayaran() {
        try {
            pembayaranDetailRepository.renderDataTable(tabel_pembayaran_detail, new ArrayList<model.Pembayaran>());
            if (cPelangganPembayaran.getSelectedIndex() >= 0) {
                int idPelanggan = pelangganOptions.get(cPelangganPembayaran.getSelectedIndex()).getIdPelanggan();
                ArrayList<model.Transaksi> rows = pembayaranRepository.all(idPelanggan);
                pembayaranRepository.renderDataTable(tabel_pembayaran, rows);
            }
        } catch (Exception ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void clearFormPelanggan(){
        txtNIKPelanggan.setText("");
        txtNamaPelanggan.setText("");
        txtInstansiPelanggan.setText("");
        txtTeleponPelanggan.setText("");
        txtEmailPelanggan.setText("");
        tabel_pelanggan.clearSelection();
    }
    
    void clearFormTransaksi(){
        tanggalMulai.setDate(new Date());
        tanggalSelesai.setDate(new Date());
        cPelanggan.setSelectedIndex(0);
        cLayanan.setSelectedIndex(0);
        tabel_transaksi.clearSelection();
    }
    
    void clearFormJadwal(){
        cPelangganJadwal.setSelectedIndex(0);
        txtKegiatanJadwal.setText("");
        tanggalJadwal.setDate(new Date());
        tabel_jadwal.clearSelection();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btn_nav_layanan = new javax.swing.JPanel();
        lbl_layanan = new javax.swing.JLabel();
        btn_nav_pelanggan = new javax.swing.JPanel();
        lbl_pelanggan = new javax.swing.JLabel();
        btn_nav_tansaksi = new javax.swing.JPanel();
        lbl_transaksi = new javax.swing.JLabel();
        btn_nav_report = new javax.swing.JPanel();
        lbl_report = new javax.swing.JLabel();
        btn_nav_jadwal = new javax.swing.JPanel();
        lbl_jadwal = new javax.swing.JLabel();
        btn_nav_pembayaran = new javax.swing.JPanel();
        lbl_pembayaran = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        panel_dashboard = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        nav_panel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lbl_dashboard_pelanggan = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lbl_dashboard_transaksi = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btn_close = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        panel_tabel8 = new javax.swing.JScrollPane();
        tabel_dashboard_transaksi = new javax.swing.JTable();
        panel_layanan = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        panel_tabel4 = new javax.swing.JScrollPane();
        tabel_layanan = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        btn_tambah_pelanggan4 = new com.k33ptoo.components.KButton();
        panel_pelanggan = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        panel_tabel1 = new javax.swing.JScrollPane();
        tabel_pelanggan = new javax.swing.JTable();
        btn_tambah_pelanggan = new com.k33ptoo.components.KButton();
        btn_edit_pelanggan = new com.k33ptoo.components.KButton();
        btn_clear_pelanggan = new com.k33ptoo.components.KButton();
        btn_hapus_pelanggan = new com.k33ptoo.components.KButton();
        jLabel36 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtEmailPelanggan = new javax.swing.JTextField();
        txtNamaPelanggan = new javax.swing.JTextField();
        txtNIKPelanggan = new javax.swing.JTextField();
        txtInstansiPelanggan = new javax.swing.JTextField();
        txtTeleponPelanggan = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        btn_tambah_pelanggan3 = new com.k33ptoo.components.KButton();
        panel_transaksi = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        panel_tabel2 = new javax.swing.JScrollPane();
        tabel_transaksi = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        btn_tambah_pelanggan1 = new com.k33ptoo.components.KButton();
        btn_edit_pelanggan1 = new com.k33ptoo.components.KButton();
        btn_clear_pelanggan1 = new com.k33ptoo.components.KButton();
        btn_hapus_pelanggan1 = new com.k33ptoo.components.KButton();
        jButton10 = new javax.swing.JButton();
        tanggalMulai = new com.toedter.calendar.JDateChooser();
        tanggalSelesai = new com.toedter.calendar.JDateChooser();
        cPelanggan = new javax.swing.JComboBox<>();
        cLayanan = new javax.swing.JComboBox<>();
        panel_report = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        btn_tambah_pelanggan2 = new com.k33ptoo.components.KButton();
        jButton11 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tanggalMulaiReport = new com.toedter.calendar.JDateChooser();
        tanggalSelesaiReport = new com.toedter.calendar.JDateChooser();
        btn_tambah_pelanggan5 = new com.k33ptoo.components.KButton();
        panel_tabel5 = new javax.swing.JScrollPane();
        tabel_report = new javax.swing.JTable();
        panel_jadwal = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        panel_tabel3 = new javax.swing.JScrollPane();
        tabel_jadwal = new javax.swing.JTable();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        btn_tambah_jadwal = new com.k33ptoo.components.KButton();
        btn_edit_jadwal = new com.k33ptoo.components.KButton();
        btn_clear_jadwal = new com.k33ptoo.components.KButton();
        btn_hapus_jadwal = new com.k33ptoo.components.KButton();
        jButton12 = new javax.swing.JButton();
        cPelangganJadwal = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        tanggalJadwal = new com.toedter.calendar.JDateChooser();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        tanggalSelesaiJadwal = new com.toedter.calendar.JDateChooser();
        tanggalMulaiJadwal = new com.toedter.calendar.JDateChooser();
        btn_tampilkan_jadwal = new com.k33ptoo.components.KButton();
        txtKegiatanJadwal = new javax.swing.JTextField();
        btn_cetak_jadwal = new com.k33ptoo.components.KButton();
        panel_pembayaran = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        panel_tabel6 = new javax.swing.JScrollPane();
        tabel_pembayaran_detail = new javax.swing.JTable();
        jLabel43 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        cPelangganPembayaran = new javax.swing.JComboBox<>();
        btn_cetak_pembayaran = new com.k33ptoo.components.KButton();
        panel_tabel7 = new javax.swing.JScrollPane();
        tabel_pembayaran = new javax.swing.JTable();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("APLIKASI CUSTOMER DATA MANAGEMENT");
        setBackground(new java.awt.Color(204, 204, 204));
        setLocationByPlatform(true);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_nav_layanan.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_layanan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_layanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_layananMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_layananMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_layananMouseExited(evt);
            }
        });

        lbl_layanan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_layanan.setText("Layanan");
        lbl_layanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_layananMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_layananLayout = new javax.swing.GroupLayout(btn_nav_layanan);
        btn_nav_layanan.setLayout(btn_nav_layananLayout);
        btn_nav_layananLayout.setHorizontalGroup(
            btn_nav_layananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_layananLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_layanan)
                .addGap(0, 83, Short.MAX_VALUE))
        );
        btn_nav_layananLayout.setVerticalGroup(
            btn_nav_layananLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_layananLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_layanan, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_layanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 175, 190, -1));

        btn_nav_pelanggan.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_pelanggan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_pelangganMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_pelangganMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_pelangganMouseExited(evt);
            }
        });

        lbl_pelanggan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_pelanggan.setText("Pelanggan");
        lbl_pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_pelangganMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lbl_pelangganMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_pelangganLayout = new javax.swing.GroupLayout(btn_nav_pelanggan);
        btn_nav_pelanggan.setLayout(btn_nav_pelangganLayout);
        btn_nav_pelangganLayout.setHorizontalGroup(
            btn_nav_pelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_pelangganLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_pelanggan)
                .addGap(0, 70, Short.MAX_VALUE))
        );
        btn_nav_pelangganLayout.setVerticalGroup(
            btn_nav_pelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_pelangganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_pelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 227, 190, -1));

        btn_nav_tansaksi.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_tansaksi.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_tansaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_tansaksiMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_tansaksiMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_tansaksiMouseExited(evt);
            }
        });

        lbl_transaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_transaksi.setText("Transaksi");
        lbl_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_transaksiMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_tansaksiLayout = new javax.swing.GroupLayout(btn_nav_tansaksi);
        btn_nav_tansaksi.setLayout(btn_nav_tansaksiLayout);
        btn_nav_tansaksiLayout.setHorizontalGroup(
            btn_nav_tansaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_tansaksiLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_transaksi)
                .addGap(0, 76, Short.MAX_VALUE))
        );
        btn_nav_tansaksiLayout.setVerticalGroup(
            btn_nav_tansaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_tansaksiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_transaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_tansaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 279, 190, -1));

        btn_nav_report.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_report.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_report.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_reportMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_reportMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_reportMouseExited(evt);
            }
        });

        lbl_report.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_report.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_report.setText("Report Transaksi");
        lbl_report.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_reportMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_reportLayout = new javax.swing.GroupLayout(btn_nav_report);
        btn_nav_report.setLayout(btn_nav_reportLayout);
        btn_nav_reportLayout.setHorizontalGroup(
            btn_nav_reportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_report, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
        );
        btn_nav_reportLayout.setVerticalGroup(
            btn_nav_reportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_reportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_report, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_report, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 383, 190, -1));

        btn_nav_jadwal.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_jadwal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_jadwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_jadwalMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_jadwalMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_jadwalMouseExited(evt);
            }
        });

        lbl_jadwal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_jadwal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_jadwal.setText("Jadwal Kegiatan");
        lbl_jadwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_jadwalMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_jadwalLayout = new javax.swing.GroupLayout(btn_nav_jadwal);
        btn_nav_jadwal.setLayout(btn_nav_jadwalLayout);
        btn_nav_jadwalLayout.setHorizontalGroup(
            btn_nav_jadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_jadwalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_jadwal, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                .addContainerGap())
        );
        btn_nav_jadwalLayout.setVerticalGroup(
            btn_nav_jadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_jadwalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_jadwal, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 190, -1));

        btn_nav_pembayaran.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_pembayaran.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_pembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_pembayaranMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_pembayaranMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_pembayaranMouseExited(evt);
            }
        });

        lbl_pembayaran.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_pembayaran.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_pembayaran.setText("Status Pembayaran");
        lbl_pembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_pembayaranMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_pembayaranLayout = new javax.swing.GroupLayout(btn_nav_pembayaran);
        btn_nav_pembayaran.setLayout(btn_nav_pembayaranLayout);
        btn_nav_pembayaranLayout.setHorizontalGroup(
            btn_nav_pembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_pembayaran, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
        );
        btn_nav_pembayaranLayout.setVerticalGroup(
            btn_nav_pembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_pembayaranLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_pembayaran, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 190, -1));
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 39, -1, -1));

        jLabel21.setFont(new java.awt.Font("Impact", 0, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 153));
        jLabel21.setText("DASHBOARD");
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 130, -1));

        jPanel14.setBackground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 91, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, -1));

        jPanel13.setBackground(new java.awt.Color(0, 51, 153));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 100, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 530));

        panel_dashboard.setBackground(new java.awt.Color(247, 247, 247));
        panel_dashboard.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_dashboardMouseDragged(evt);
            }
        });
        panel_dashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_dashboardMousePressed(evt);
            }
        });
        panel_dashboard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 51, 153));
        jLabel5.setText("Dashboard Sales");
        panel_dashboard.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 410, 20));

        nav_panel.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 153));
        jLabel7.setText("Jumlah Pelanggan");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        lbl_dashboard_pelanggan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_dashboard_pelanggan.setForeground(new java.awt.Color(0, 51, 153));
        lbl_dashboard_pelanggan.setText("500");

        jPanel11.setBackground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_dashboard_pelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_dashboard_pelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(83, 83, 83))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_dashboard_transaksi.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_dashboard_transaksi.setForeground(new java.awt.Color(0, 51, 153));
        lbl_dashboard_transaksi.setText("50");
        jPanel4.add(lbl_dashboard_transaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 22, -1, -1));

        jPanel10.setBackground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 100, 10));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 51, 153));
        jLabel10.setText("Jumlah Transaksi");

        javax.swing.GroupLayout nav_panelLayout = new javax.swing.GroupLayout(nav_panel);
        nav_panel.setLayout(nav_panelLayout);
        nav_panelLayout.setHorizontalGroup(
            nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nav_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nav_panelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nav_panelLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(204, Short.MAX_VALUE))
        );
        nav_panelLayout.setVerticalGroup(
            nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nav_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nav_panelLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(nav_panelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_dashboard.add(nav_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 480, 150));

        jPanel8.setBackground(new java.awt.Color(0, 51, 153));
        jPanel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel8MouseClicked(evt);
            }
        });

        btn_close.setBackground(new java.awt.Color(96, 83, 150));
        btn_close.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        btn_close.setForeground(new java.awt.Color(255, 255, 255));
        btn_close.setText("X");
        btn_close.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_closeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 156, Short.MAX_VALUE)
                .addComponent(btn_close, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(btn_close, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        panel_dashboard.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 0, 180, -1));

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 51, 153));
        jLabel42.setText("APLIKASI PENGELOLAAN DATA PELANGGAN");
        panel_dashboard.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(0, 51, 153));
        jLabel48.setText("Notifikasi Transaksi :");
        panel_dashboard.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 130, 20));

        panel_tabel8.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_dashboard_transaksi.setBackground(new java.awt.Color(247, 247, 247));
        tabel_dashboard_transaksi.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_dashboard_transaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_dashboard_transaksi.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_dashboard_transaksi.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tabel_dashboard_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_dashboard_transaksiMouseClicked(evt);
            }
        });
        panel_tabel8.setViewportView(tabel_dashboard_transaksi);

        panel_dashboard.add(panel_tabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 270, 710, 190));

        getContentPane().add(panel_dashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        panel_layanan.setBackground(new java.awt.Color(247, 247, 247));
        panel_layanan.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_layananMouseDragged(evt);
            }
        });
        panel_layanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_layananMousePressed(evt);
            }
        });
        panel_layanan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 51, 153));
        jLabel35.setText("DATA LAYANAN");
        panel_layanan.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel4.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_layanan.setBackground(new java.awt.Color(247, 247, 247));
        tabel_layanan.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_layanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_layanan.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_layanan.setSelectionBackground(new java.awt.Color(96, 83, 150));
        panel_tabel4.setViewportView(tabel_layanan);

        panel_layanan.add(panel_tabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 710, 260));

        jButton8.setBackground(new java.awt.Color(0, 51, 153));
        jButton8.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("X ");
        jButton8.setBorder(null);
        jButton8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton8.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        panel_layanan.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        btn_tambah_pelanggan4.setText("Cetak");
        btn_tambah_pelanggan4.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan4ActionPerformed(evt);
            }
        });
        panel_layanan.add(btn_tambah_pelanggan4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 83, 29));

        getContentPane().add(panel_layanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        panel_pelanggan.setBackground(new java.awt.Color(247, 247, 247));
        panel_pelanggan.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_pelangganMouseDragged(evt);
            }
        });
        panel_pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_pelangganMousePressed(evt);
            }
        });
        panel_pelanggan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 51, 153));
        jLabel6.setText("Email");
        panel_pelanggan.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 140, 30));

        panel_tabel1.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_pelanggan.setBackground(new java.awt.Color(247, 247, 247));
        tabel_pelanggan.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_pelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_pelanggan.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_pelanggan.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tabel_pelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_pelangganMouseClicked(evt);
            }
        });
        panel_tabel1.setViewportView(tabel_pelanggan);

        panel_pelanggan.add(panel_tabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 710, 210));

        btn_tambah_pelanggan.setText("Tambah");
        btn_tambah_pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(btn_tambah_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 80, 83, 29));

        btn_edit_pelanggan.setText("Edit");
        btn_edit_pelanggan.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit_pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit_pelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(btn_edit_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 130, 83, 29));

        btn_clear_pelanggan.setText("Bersihkan");
        btn_clear_pelanggan.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_clear_pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clear_pelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(btn_clear_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 230, 83, 29));

        btn_hapus_pelanggan.setText("Hapus");
        btn_hapus_pelanggan.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus_pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_pelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(btn_hapus_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 180, 83, 29));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(0, 51, 153));
        jLabel36.setText("DATA PELANGGAN");
        panel_pelanggan.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 410, 37));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 51, 153));
        jLabel13.setText("Nama");
        panel_pelanggan.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 140, 30));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 51, 153));
        jLabel14.setText("NIK");
        panel_pelanggan.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 140, 30));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 51, 153));
        jLabel15.setText("Nama Instansi");
        panel_pelanggan.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 140, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 51, 153));
        jLabel16.setText("Telepon");
        panel_pelanggan.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 140, 30));

        txtEmailPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtEmailPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailPelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(txtEmailPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 240, 370, 30));

        txtNamaPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNamaPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaPelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(txtNamaPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 370, 30));

        txtNIKPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtNIKPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNIKPelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(txtNIKPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 120, 370, 30));

        txtInstansiPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtInstansiPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInstansiPelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(txtInstansiPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 370, 30));

        txtTeleponPelanggan.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTeleponPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTeleponPelangganActionPerformed(evt);
            }
        });
        txtTeleponPelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTeleponPelangganKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTeleponPelangganKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTeleponPelangganKeyTyped(evt);
            }
        });
        panel_pelanggan.add(txtTeleponPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 200, 370, 30));

        jButton9.setBackground(new java.awt.Color(0, 51, 153));
        jButton9.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("X ");
        jButton9.setBorder(null);
        jButton9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        panel_pelanggan.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        btn_tambah_pelanggan3.setText("Cetak");
        btn_tambah_pelanggan3.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan3ActionPerformed(evt);
            }
        });
        panel_pelanggan.add(btn_tambah_pelanggan3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 270, 83, 29));

        getContentPane().add(panel_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        panel_transaksi.setBackground(new java.awt.Color(247, 247, 247));
        panel_transaksi.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_transaksiMouseDragged(evt);
            }
        });
        panel_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_transaksiMousePressed(evt);
            }
        });
        panel_transaksi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 51, 153));
        jLabel19.setText("DATA TRANSAKSI");
        panel_transaksi.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel2.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_transaksi.setBackground(new java.awt.Color(247, 247, 247));
        tabel_transaksi.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_transaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_transaksi.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_transaksi.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tabel_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_transaksiMouseClicked(evt);
            }
        });
        panel_tabel2.setViewportView(tabel_transaksi);

        panel_transaksi.add(panel_tabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 710, 280));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 51, 153));
        jLabel17.setText("Tanggal Mulai");
        panel_transaksi.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 140, 30));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 51, 153));
        jLabel18.setText("Tanggal Selesai");
        panel_transaksi.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 140, 30));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 51, 153));
        jLabel37.setText("Pelanggan");
        panel_transaksi.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 140, 30));

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 51, 153));
        jLabel38.setText("Layanan");
        panel_transaksi.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 140, 30));

        btn_tambah_pelanggan1.setText("Tambah");
        btn_tambah_pelanggan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan1ActionPerformed(evt);
            }
        });
        panel_transaksi.add(btn_tambah_pelanggan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 80, 83, 29));

        btn_edit_pelanggan1.setText("Edit");
        btn_edit_pelanggan1.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit_pelanggan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit_pelanggan1ActionPerformed(evt);
            }
        });
        panel_transaksi.add(btn_edit_pelanggan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 120, 83, 29));

        btn_clear_pelanggan1.setText("Bersihkan");
        btn_clear_pelanggan1.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_clear_pelanggan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clear_pelanggan1ActionPerformed(evt);
            }
        });
        panel_transaksi.add(btn_clear_pelanggan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 200, 83, 29));

        btn_hapus_pelanggan1.setText("Hapus");
        btn_hapus_pelanggan1.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus_pelanggan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_pelanggan1ActionPerformed(evt);
            }
        });
        panel_transaksi.add(btn_hapus_pelanggan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 83, 29));

        jButton10.setBackground(new java.awt.Color(0, 51, 153));
        jButton10.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("X ");
        jButton10.setBorder(null);
        jButton10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton10.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        panel_transaksi.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));
        panel_transaksi.add(tanggalMulai, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 370, -1));
        panel_transaksi.add(tanggalSelesai, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 120, 370, -1));
        panel_transaksi.add(cPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 370, -1));
        panel_transaksi.add(cLayanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 200, 370, -1));

        getContentPane().add(panel_transaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        panel_report.setBackground(new java.awt.Color(247, 247, 247));
        panel_report.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_reportMouseDragged(evt);
            }
        });
        panel_report.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_reportMousePressed(evt);
            }
        });
        panel_report.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 51, 153));
        jLabel28.setText("LAPORAN APLIKASI");
        panel_report.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        btn_tambah_pelanggan2.setText("Tampilkan");
        btn_tambah_pelanggan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan2ActionPerformed(evt);
            }
        });
        panel_report.add(btn_tambah_pelanggan2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, 83, 29));

        jButton11.setBackground(new java.awt.Color(0, 51, 153));
        jButton11.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("X ");
        jButton11.setBorder(null);
        jButton11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        panel_report.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 51, 153));
        jLabel22.setText("Tanggal Mulai");
        panel_report.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 140, 30));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 51, 153));
        jLabel23.setText("Tanggal Selesai");
        panel_report.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 140, 30));
        panel_report.add(tanggalMulaiReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 370, -1));
        panel_report.add(tanggalSelesaiReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 120, 370, -1));

        btn_tambah_pelanggan5.setText("Cetak");
        btn_tambah_pelanggan5.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan5ActionPerformed(evt);
            }
        });
        panel_report.add(btn_tambah_pelanggan5, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 150, 83, 29));

        panel_tabel5.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_report.setBackground(new java.awt.Color(247, 247, 247));
        tabel_report.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_report.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_report.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_report.setSelectionBackground(new java.awt.Color(96, 83, 150));
        panel_tabel5.setViewportView(tabel_report);

        panel_report.add(panel_tabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 710, 320));

        getContentPane().add(panel_report, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        panel_jadwal.setBackground(new java.awt.Color(247, 247, 247));
        panel_jadwal.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_jadwalMouseDragged(evt);
            }
        });
        panel_jadwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_jadwalMousePressed(evt);
            }
        });
        panel_jadwal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 51, 153));
        jLabel24.setText("JADWAL KEGIATAN");
        panel_jadwal.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel3.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_jadwal.setBackground(new java.awt.Color(247, 247, 247));
        tabel_jadwal.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_jadwal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_jadwal.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_jadwal.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tabel_jadwal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_jadwalMouseClicked(evt);
            }
        });
        panel_tabel3.setViewportView(tabel_jadwal);

        panel_jadwal.add(panel_tabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 710, 220));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(0, 51, 153));
        jLabel39.setText("Pelanggan");
        panel_jadwal.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 140, 30));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 51, 153));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("-");
        panel_jadwal.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 270, 20, 20));

        btn_tambah_jadwal.setText("Tambah");
        btn_tambah_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_tambah_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 80, 83, 29));

        btn_edit_jadwal.setText("Edit");
        btn_edit_jadwal.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_edit_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 120, 83, 29));

        btn_clear_jadwal.setText("Bersihkan");
        btn_clear_jadwal.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_clear_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clear_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_clear_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 200, 83, 29));

        btn_hapus_jadwal.setText("Hapus");
        btn_hapus_jadwal.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_hapus_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 83, 29));

        jButton12.setBackground(new java.awt.Color(0, 51, 153));
        jButton12.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("X ");
        jButton12.setBorder(null);
        jButton12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        panel_jadwal.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));
        panel_jadwal.add(cPelangganJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 90, 370, -1));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(0, 51, 153));
        jLabel41.setText("Kegiatan");
        panel_jadwal.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 140, 30));
        panel_jadwal.add(tanggalJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, 370, -1));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(0, 51, 153));
        jLabel44.setText("Waktu");
        panel_jadwal.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 140, 30));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 51, 153));
        jLabel45.setText("Filter : ");
        panel_jadwal.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 50, 20));
        panel_jadwal.add(tanggalSelesaiJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 270, 130, -1));
        panel_jadwal.add(tanggalMulaiJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 270, 130, -1));

        btn_tampilkan_jadwal.setText("Tampilkan");
        btn_tampilkan_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilkan_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_tampilkan_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 260, 83, 29));
        panel_jadwal.add(txtKegiatanJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 120, 370, -1));

        btn_cetak_jadwal.setText("Cetak");
        btn_cetak_jadwal.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_cetak_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetak_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_cetak_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 260, 83, 29));

        getContentPane().add(panel_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        panel_pembayaran.setBackground(new java.awt.Color(247, 247, 247));
        panel_pembayaran.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_pembayaranMouseDragged(evt);
            }
        });
        panel_pembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_pembayaranMousePressed(evt);
            }
        });
        panel_pembayaran.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 51, 153));
        jLabel25.setText("DATA PEMBAYARAN");
        panel_pembayaran.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel6.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_pembayaran_detail.setBackground(new java.awt.Color(247, 247, 247));
        tabel_pembayaran_detail.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_pembayaran_detail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_pembayaran_detail.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_pembayaran_detail.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tabel_pembayaran_detail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_pembayaran_detailMouseClicked(evt);
            }
        });
        panel_tabel6.setViewportView(tabel_pembayaran_detail);

        panel_pembayaran.add(panel_tabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, 710, 190));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(0, 51, 153));
        jLabel43.setText("Pelanggan");
        panel_pembayaran.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 140, 20));

        jButton13.setBackground(new java.awt.Color(0, 51, 153));
        jButton13.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("X ");
        jButton13.setBorder(null);
        jButton13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton13.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        panel_pembayaran.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        cPelangganPembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cPelangganPembayaranActionPerformed(evt);
            }
        });
        panel_pembayaran.add(cPelangganPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 390, 30));

        btn_cetak_pembayaran.setText("Cetak");
        btn_cetak_pembayaran.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_cetak_pembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetak_pembayaranActionPerformed(evt);
            }
        });
        panel_pembayaran.add(btn_cetak_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 60, 83, 29));

        panel_tabel7.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tabel_pembayaran.setBackground(new java.awt.Color(247, 247, 247));
        tabel_pembayaran.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tabel_pembayaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Allan", "XLS", "2hrs", "$200"},
                {"Brian", "React", "1hr", "$100 per hr"},
                {"Romeo", "C#", "3 Days", "$1000"},
                {"Alex", "C++ ", "10 hrs", "$50 per hr"}
            },
            new String [] {
                "Name", "Project", "Time", "Cost"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tabel_pembayaran.setGridColor(new java.awt.Color(247, 247, 247));
        tabel_pembayaran.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tabel_pembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_pembayaranMouseClicked(evt);
            }
        });
        panel_tabel7.setViewportView(tabel_pembayaran);

        panel_pembayaran.add(panel_tabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, 710, 190));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 51, 153));
        jLabel46.setText("Detail Pembayaran : ");
        panel_pembayaran.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 330, 130, 20));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(0, 51, 153));
        jLabel47.setText("Transaksi : ");
        panel_pembayaran.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 80, 20));

        getContentPane().add(panel_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 530));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_closeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_closeMouseClicked
        // TODO add your handling code here:
        logout();
    }//GEN-LAST:event_btn_closeMouseClicked

    private void btn_nav_layananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_layananMouseClicked
        selectMenu("layanan");
    }//GEN-LAST:event_btn_nav_layananMouseClicked

    private void btn_nav_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pelangganMouseClicked
        selectMenu("pelanggan");
    }//GEN-LAST:event_btn_nav_pelangganMouseClicked

    private void btn_nav_tansaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_tansaksiMouseClicked
        selectMenu("transaksi");
    }//GEN-LAST:event_btn_nav_tansaksiMouseClicked

    private void btn_nav_reportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_reportMouseClicked
        selectMenu("report");
    }//GEN-LAST:event_btn_nav_reportMouseClicked

    private void btn_nav_layananMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_layananMouseEntered
        // TODO add your handling code here:
 
    }//GEN-LAST:event_btn_nav_layananMouseEntered

    private void btn_nav_pelangganMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pelangganMouseEntered
        // TODO add your handling code here:
         
    }//GEN-LAST:event_btn_nav_pelangganMouseEntered

    private void btn_nav_tansaksiMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_tansaksiMouseEntered
        // TODO add your handling code here:
     
    }//GEN-LAST:event_btn_nav_tansaksiMouseEntered

    private void btn_nav_reportMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_reportMouseEntered
        // TODO add your handling code here:]
         
    }//GEN-LAST:event_btn_nav_reportMouseEntered

    private void btn_nav_layananMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_layananMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_layananMouseExited

    private void btn_nav_pelangganMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pelangganMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_pelangganMouseExited

    private void btn_nav_tansaksiMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_tansaksiMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_tansaksiMouseExited

    private void btn_nav_reportMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_reportMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_reportMouseExited

    private void panel_dashboardMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_dashboardMousePressed
        // TODO add your handling code here:
          xx = evt.getX();
        xy = evt.getY();
    }//GEN-LAST:event_panel_dashboardMousePressed

    private void panel_dashboardMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_dashboardMouseDragged
        // TODO add your handling code here:
         int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x-xx,y-xy);
    }//GEN-LAST:event_panel_dashboardMouseDragged

    private void lbl_layananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_layananMouseClicked
        selectMenu("layanan");
    }//GEN-LAST:event_lbl_layananMouseClicked

    private void lbl_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_pelangganMouseClicked
       selectMenu("pelanggan");
    }//GEN-LAST:event_lbl_pelangganMouseClicked

    private void lbl_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_transaksiMouseClicked
         selectMenu("transaksi");
    }//GEN-LAST:event_lbl_transaksiMouseClicked

    private void lbl_reportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_reportMouseClicked
        selectMenu("report");
    }//GEN-LAST:event_lbl_reportMouseClicked

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
//    panel_pelanggan.setVisible(false);
//    panel_report.setVisible(false);
//    panel_transaksi.setVisible(false);
//    panel_layanan.setVisible(false);
//    panel_dashboard.setVisible(true);
    selectMenu("dashboard");
    }//GEN-LAST:event_jLabel21MouseClicked

    private void panel_pelangganMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pelangganMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pelangganMouseDragged

    private void panel_pelangganMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pelangganMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pelangganMousePressed

    private void panel_transaksiMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_transaksiMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_transaksiMouseDragged

    private void panel_transaksiMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_transaksiMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_transaksiMousePressed

    private void panel_reportMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_reportMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_reportMouseDragged

    private void panel_reportMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_reportMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_reportMousePressed

    private void panel_layananMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_layananMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_layananMouseDragged

    private void panel_layananMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_layananMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_layananMousePressed

    private void btn_edit_pelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit_pelangganActionPerformed
        model.Pelanggan pelanggan = new model.Pelanggan();
        pelanggan.setNik(txtNIKPelanggan.getText());
        pelanggan.setNamaPic(txtNamaPelanggan.getText());
        pelanggan.setNamaInstansi(txtInstansiPelanggan.getText());
        pelanggan.setNoTlp(txtTeleponPelanggan.getText());
        pelanggan.setEmail(txtEmailPelanggan.getText());
        try {
            pelangganRepository.update(pelanggan.toMap(), MapCustom.of("id_pelanggan", pelangganRepository.selectedData(tabel_pelanggan).getIdPelanggan()));
            clearFormPelanggan();
            selectMenu("pelanggan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data Pelanggan! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_edit_pelangganActionPerformed

    private void btn_clear_pelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clear_pelangganActionPerformed
        clearFormPelanggan();
    }//GEN-LAST:event_btn_clear_pelangganActionPerformed

    private void btn_hapus_pelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus_pelangganActionPerformed
        try {
            pelangganRepository.delete(MapCustom.of("id_pelanggan", pelangganRepository.selectedData(tabel_pelanggan).getIdPelanggan()));
            clearFormPelanggan();
            selectMenu("pelanggan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Pelanggan! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_hapus_pelangganActionPerformed

    private void txtEmailPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailPelangganActionPerformed

    private void txtNamaPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaPelangganActionPerformed

    private void txtNIKPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNIKPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNIKPelangganActionPerformed

    private void txtInstansiPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInstansiPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInstansiPelangganActionPerformed

    private void txtTeleponPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTeleponPelangganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTeleponPelangganActionPerformed

    private void btn_edit_pelanggan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit_pelanggan1ActionPerformed
        model.Transaksi transaksi = new model.Transaksi();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        transaksi.setTanggalMulai(simpleDateFormat.format(tanggalMulai.getDate()));
        transaksi.setTanggalSelesai(simpleDateFormat.format(tanggalSelesai.getDate()));
        transaksi.setIdPelanggan(pelangganOptions.get(cPelanggan.getSelectedIndex()).getIdPelanggan());
        transaksi.setIdLayanan(layananOptions.get(cLayanan.getSelectedIndex()).getIdLayanan());
        transaksi.setIdSales(Login.sessionSales.getIdSales());
        transaksi.setTotalHarga(layananOptions.get(cLayanan.getSelectedIndex()).getHarga());
        try {
            transaksiRepository.update(transaksi.toMap(), MapCustom.of("id_transaksi", transaksiRepository.selectedData(tabel_transaksi).getIdTransaksi()));
            clearFormTransaksi();
            selectMenu("transaksi");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Transaksi! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_edit_pelanggan1ActionPerformed

    private void btn_clear_pelanggan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clear_pelanggan1ActionPerformed
        clearFormTransaksi();
    }//GEN-LAST:event_btn_clear_pelanggan1ActionPerformed

    private void btn_hapus_pelanggan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus_pelanggan1ActionPerformed
        try {
            transaksiRepository.delete(MapCustom.of("id_transaksi", transaksiRepository.selectedData(tabel_transaksi).getIdTransaksi()));
            clearFormTransaksi();
            selectMenu("transaksi");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Transaksi! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_hapus_pelanggan1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        logout();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        logout();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        logout();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        logout();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btn_tambah_pelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelangganActionPerformed
        model.Pelanggan pelanggan = new model.Pelanggan();
        pelanggan.setNik(txtNIKPelanggan.getText());
        pelanggan.setNamaPic(txtNamaPelanggan.getText());
        pelanggan.setNamaInstansi(txtInstansiPelanggan.getText());
        pelanggan.setNoTlp(txtTeleponPelanggan.getText());
        pelanggan.setEmail(txtEmailPelanggan.getText());
        try {
            pelangganRepository.save(pelanggan);
            clearFormPelanggan();
            selectMenu("pelanggan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Pelanggan! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah_pelangganActionPerformed

    private void tabel_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_pelangganMouseClicked
        model.Pelanggan pelanggan = pelangganRepository.selectedData(tabel_pelanggan);
        txtNIKPelanggan.setText(pelanggan.getNik());
        txtNamaPelanggan.setText(pelanggan.getNamaPic());
        txtInstansiPelanggan.setText(pelanggan.getNamaInstansi());
        txtTeleponPelanggan.setText(pelanggan.getNoTlp());
        txtEmailPelanggan.setText(pelanggan.getEmail());
    }//GEN-LAST:event_tabel_pelangganMouseClicked

    private void btn_tambah_pelanggan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan1ActionPerformed
        model.Transaksi transaksi = new model.Transaksi();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        transaksi.setTanggalMulai(simpleDateFormat.format(tanggalMulai.getDate()));
        transaksi.setTanggalSelesai(simpleDateFormat.format(tanggalSelesai.getDate()));
        transaksi.setIdPelanggan(pelangganOptions.get(cPelanggan.getSelectedIndex()).getIdPelanggan());
        transaksi.setIdLayanan(layananOptions.get(cLayanan.getSelectedIndex()).getIdLayanan());
        transaksi.setIdSales(Login.sessionSales.getIdSales());
        transaksi.setTotalHarga(layananOptions.get(cLayanan.getSelectedIndex()).getHarga());
        transaksi.setStatus(Transaksi.STATUS_PROSES);
        try {
            transaksiRepository.save(transaksi);
            clearFormTransaksi();
            selectMenu("transaksi");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Transaksi! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan1ActionPerformed

    private void tabel_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_transaksiMouseClicked
        try {
            model.Transaksi transaksi = transaksiRepository.selectedData(tabel_transaksi);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            tanggalMulai.setDate(simpleDateFormat.parse(transaksi.getTanggalMulai()));
            tanggalSelesai.setDate(simpleDateFormat.parse(transaksi.getTanggalSelesai()));
            int indexPelanggan = 0;
            for (int i = 0; i < pelangganOptions.size(); i++) {
                Pelanggan get = pelangganOptions.get(i);
                if (get.getIdPelanggan() == transaksi.getIdPelanggan()) {
                    indexPelanggan = i;
                    break;
                }
            }
            cPelanggan.setSelectedIndex(indexPelanggan);
            int indexLayanan = 0;
            for (int i = 0; i < layananOptions.size(); i++) {
                Layanan get = layananOptions.get(i);
                if (get.getIdLayanan() == transaksi.getIdLayanan()) {
                    indexLayanan = i;
                    break;
                }
            }
            cLayanan.setSelectedIndex(indexLayanan);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
//        txtNIKPelanggan.setText(pelanggan.getNik());
//        txtNamaPelanggan.setText(pelanggan.getNamaPic());
//        txtInstansiPelanggan.setText(pelanggan.getNamaInstansi());
//        txtTeleponPelanggan.setText(pelanggan.getNoTlp());
//        txtEmailPelanggan.setText(pelanggan.getEmail());
    }//GEN-LAST:event_tabel_transaksiMouseClicked

    private void jPanel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel8MouseClicked
        logout();
    }//GEN-LAST:event_jPanel8MouseClicked

    private void lbl_pelangganMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_pelangganMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_lbl_pelangganMouseEntered

    private void btn_tambah_pelanggan3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan3ActionPerformed
        try {
            File pelanggan = new File("src/report/pelanggan.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), null, Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan3ActionPerformed

    private void btn_tambah_pelanggan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan4ActionPerformed
        try {
            File pelanggan = new File("src/report/layanan.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), null, Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan4ActionPerformed

    private void btn_tambah_pelanggan5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan5ActionPerformed
        try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            File pelanggan = new File("src/report/transaksi.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), MapCustom.of("tanggalMulai", simpleDateFormat.format(tanggalMulaiReport.getDate()), "tanggalSelesai", simpleDateFormat.format(tanggalSelesaiReport.getDate())), Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan5ActionPerformed

    private void btn_tambah_pelanggan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan2ActionPerformed
        tampilReport();
    }//GEN-LAST:event_btn_tambah_pelanggan2ActionPerformed

    private void btn_nav_jadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_jadwalMouseClicked
        selectMenu("jadwal");
    }//GEN-LAST:event_btn_nav_jadwalMouseClicked

    private void btn_nav_jadwalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_jadwalMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_jadwalMouseEntered

    private void btn_nav_jadwalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_jadwalMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_jadwalMouseExited

    private void lbl_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_pembayaranMouseClicked
        selectMenu("pembayaran");
    }//GEN-LAST:event_lbl_pembayaranMouseClicked

    private void btn_nav_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pembayaranMouseClicked
        selectMenu("pembayaran");
    }//GEN-LAST:event_btn_nav_pembayaranMouseClicked

    private void btn_nav_pembayaranMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pembayaranMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_pembayaranMouseEntered

    private void btn_nav_pembayaranMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pembayaranMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_pembayaranMouseExited

    private void tabel_jadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_jadwalMouseClicked
        try {
            model.JadwalSales jadwal = jadwalSalesRepository.selectedData(tabel_jadwal);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int indexPelanggan = 0;
            for (int i = 0; i < pelangganOptions.size(); i++) {
                Pelanggan get = pelangganOptions.get(i);
                if (get.getIdPelanggan() == jadwal.getIdPelanggan()) {
                    indexPelanggan = i;
                    break;
                }
            }
            cPelangganJadwal.setSelectedIndex(indexPelanggan);
            txtKegiatanJadwal.setText(jadwal.getKegiatan());
            tanggalJadwal.setDate(simpleDateFormat.parse(jadwal.getWaktu()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_tabel_jadwalMouseClicked

    private void btn_tambah_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_jadwalActionPerformed
        model.JadwalSales jadwal = new model.JadwalSales();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        jadwal.setIdPelanggan(pelangganOptions.get(cPelangganJadwal.getSelectedIndex()).getIdPelanggan());
        jadwal.setIdSales(Login.sessionSales.getIdSales());
        jadwal.setKegiatan(txtKegiatanJadwal.getText());
        jadwal.setWaktu(simpleDateFormat.format(tanggalJadwal.getDate()));
        try {
            jadwalSalesRepository.save(jadwal);
            clearFormJadwal();
            selectMenu("jadwal");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Jadwal! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah_jadwalActionPerformed

    private void btn_edit_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit_jadwalActionPerformed
        model.JadwalSales jadwal = new model.JadwalSales();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        jadwal.setIdPelanggan(pelangganOptions.get(cPelangganJadwal.getSelectedIndex()).getIdPelanggan());
        jadwal.setIdSales(Login.sessionSales.getIdSales());
        jadwal.setKegiatan(txtKegiatanJadwal.getText());
        jadwal.setWaktu(simpleDateFormat.format(tanggalJadwal.getDate()));
        try {
            jadwalSalesRepository.update(jadwal.toMap(), MapCustom.of("id_jadwal", jadwalSalesRepository.selectedData(tabel_jadwal).getIdJadwal()));
            clearFormJadwal();
            selectMenu("jadwal");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Jadwal! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_edit_jadwalActionPerformed

    private void btn_clear_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clear_jadwalActionPerformed
        clearFormJadwal();
    }//GEN-LAST:event_btn_clear_jadwalActionPerformed

    private void btn_hapus_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus_jadwalActionPerformed
        try {
            jadwalSalesRepository.delete(MapCustom.of("id_jadwal", jadwalSalesRepository.selectedData(tabel_jadwal).getIdJadwal()));
            clearFormJadwal();
            selectMenu("jadwal");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Jadwal! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_hapus_jadwalActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        logout();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void panel_jadwalMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_jadwalMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_jadwalMouseDragged

    private void panel_jadwalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_jadwalMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_jadwalMousePressed

    private void tabel_pembayaran_detailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_pembayaran_detailMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabel_pembayaran_detailMouseClicked

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        logout();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void panel_pembayaranMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pembayaranMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pembayaranMouseDragged

    private void panel_pembayaranMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pembayaranMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pembayaranMousePressed

    private void txtTeleponPelangganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTeleponPelangganKeyPressed

    }//GEN-LAST:event_txtTeleponPelangganKeyPressed

    private void txtTeleponPelangganKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTeleponPelangganKeyTyped
//        String[] numbers = {"0","1","2","3","4","5","6","7","8","9"};
//        if (!ArrayUtils.contains(numbers, evt.getKeyChar()+"")) {
//            evt.consume();
//        }
    }//GEN-LAST:event_txtTeleponPelangganKeyTyped

    private void txtTeleponPelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTeleponPelangganKeyReleased

    }//GEN-LAST:event_txtTeleponPelangganKeyReleased

    private void lbl_jadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_jadwalMouseClicked
        selectMenu("jadwal");
    }//GEN-LAST:event_lbl_jadwalMouseClicked

    private void btn_tampilkan_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilkan_jadwalActionPerformed
        tampilJadwal();
    }//GEN-LAST:event_btn_tampilkan_jadwalActionPerformed

    private void btn_cetak_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetak_jadwalActionPerformed
        try {
            File jadwalSales = new File("src/report/jadwal_sales.jasper");
            int idSales = Login.sessionSales.getIdSales();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = simpleDateFormat.format(tanggalMulaiJadwal.getDate());
            String endTime = simpleDateFormat.format(tanggalSelesaiJadwal.getDate());
            JasperPrint jp = JasperFillManager.fillReport(jadwalSales.getPath(), MapCustom.of(
                    "param_id_sales", idSales,
                    "param_start_date", startTime,
                    "param_end_date", endTime
            ), Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_cetak_jadwalActionPerformed

    private void btn_cetak_pembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetak_pembayaranActionPerformed
        if (tabel_pembayaran.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Harap pilih data transaksi terlebih dahulu");
        } else {
            try {
                File jadwalSales = new File("src/report/pembayaran.jasper");
                model.Transaksi data = pembayaranRepository.selectedData(tabel_pembayaran);
                JasperPrint jp = JasperFillManager.fillReport(jadwalSales.getPath(), MapCustom.of(
                        "id_transaksi", data.getIdTransaksi(),
                        "tanggal_mulai", data.getTanggalMulai(),
                        "tanggal_selesai", data.getTanggalSelesai(),
                        "pelanggan", data.getPelanggan().getNamaPic()+" - "+data.getPelanggan().getNamaInstansi(),
                        "layanan", data.getLayanan().getNama(),
                        "total_harga", data.getTotalHarga()
                ), Koneksi.Koneksi.koneksidb());
                JasperViewer.viewReport(jp, false);
            } catch (JRException e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_btn_cetak_pembayaranActionPerformed

    private void tabel_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_pembayaranMouseClicked
        try {
            model.Transaksi data = pembayaranRepository.selectedData(tabel_pembayaran);
            ArrayList<model.Pembayaran> rows = pembayaranDetailRepository.all(data.getIdTransaksi());
            pembayaranDetailRepository.renderDataTable(tabel_pembayaran_detail, rows);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_tabel_pembayaranMouseClicked

    private void cPelangganPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cPelangganPembayaranActionPerformed
        tampilPembayaran();
    }//GEN-LAST:event_cPelangganPembayaranActionPerformed

    private void tabel_dashboard_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_dashboard_transaksiMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tabel_dashboard_transaksiMouseClicked

    int xx ,xy;
    
    void logout(){
        this.dispose();    
        new Login().setVisible(true);
    }
       
    //bad idea
    
    
    private void onClick(JPanel panel)
    {
        panel.setBackground(new Color(102,153,255));
    }
    
     private void onLeaveClick(JPanel panel)
    {
        panel.setBackground(Color.white);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btn_cetak_jadwal;
    private com.k33ptoo.components.KButton btn_cetak_pembayaran;
    private com.k33ptoo.components.KButton btn_clear_jadwal;
    private com.k33ptoo.components.KButton btn_clear_pelanggan;
    private com.k33ptoo.components.KButton btn_clear_pelanggan1;
    private javax.swing.JLabel btn_close;
    private com.k33ptoo.components.KButton btn_edit_jadwal;
    private com.k33ptoo.components.KButton btn_edit_pelanggan;
    private com.k33ptoo.components.KButton btn_edit_pelanggan1;
    private com.k33ptoo.components.KButton btn_hapus_jadwal;
    private com.k33ptoo.components.KButton btn_hapus_pelanggan;
    private com.k33ptoo.components.KButton btn_hapus_pelanggan1;
    private javax.swing.JPanel btn_nav_jadwal;
    private javax.swing.JPanel btn_nav_layanan;
    private javax.swing.JPanel btn_nav_pelanggan;
    private javax.swing.JPanel btn_nav_pembayaran;
    private javax.swing.JPanel btn_nav_report;
    private javax.swing.JPanel btn_nav_tansaksi;
    private com.k33ptoo.components.KButton btn_tambah_jadwal;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan1;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan2;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan3;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan4;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan5;
    private com.k33ptoo.components.KButton btn_tampilkan_jadwal;
    private javax.swing.JComboBox<String> cLayanan;
    private javax.swing.JComboBox<String> cPelanggan;
    private javax.swing.JComboBox<String> cPelangganJadwal;
    private javax.swing.JComboBox<String> cPelangganPembayaran;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel lbl_dashboard_pelanggan;
    private javax.swing.JLabel lbl_dashboard_transaksi;
    private javax.swing.JLabel lbl_jadwal;
    private javax.swing.JLabel lbl_layanan;
    private javax.swing.JLabel lbl_pelanggan;
    private javax.swing.JLabel lbl_pembayaran;
    private javax.swing.JLabel lbl_report;
    private javax.swing.JLabel lbl_transaksi;
    private javax.swing.JPanel nav_panel;
    private javax.swing.JPanel panel_dashboard;
    private javax.swing.JPanel panel_jadwal;
    private javax.swing.JPanel panel_layanan;
    private javax.swing.JPanel panel_pelanggan;
    private javax.swing.JPanel panel_pembayaran;
    private javax.swing.JPanel panel_report;
    private javax.swing.JScrollPane panel_tabel1;
    private javax.swing.JScrollPane panel_tabel2;
    private javax.swing.JScrollPane panel_tabel3;
    private javax.swing.JScrollPane panel_tabel4;
    private javax.swing.JScrollPane panel_tabel5;
    private javax.swing.JScrollPane panel_tabel6;
    private javax.swing.JScrollPane panel_tabel7;
    private javax.swing.JScrollPane panel_tabel8;
    private javax.swing.JPanel panel_transaksi;
    private javax.swing.JTable tabel_dashboard_transaksi;
    private javax.swing.JTable tabel_jadwal;
    private javax.swing.JTable tabel_layanan;
    private javax.swing.JTable tabel_pelanggan;
    private javax.swing.JTable tabel_pembayaran;
    private javax.swing.JTable tabel_pembayaran_detail;
    private javax.swing.JTable tabel_report;
    private javax.swing.JTable tabel_transaksi;
    private com.toedter.calendar.JDateChooser tanggalJadwal;
    private com.toedter.calendar.JDateChooser tanggalMulai;
    private com.toedter.calendar.JDateChooser tanggalMulaiJadwal;
    private com.toedter.calendar.JDateChooser tanggalMulaiReport;
    private com.toedter.calendar.JDateChooser tanggalSelesai;
    private com.toedter.calendar.JDateChooser tanggalSelesaiJadwal;
    private com.toedter.calendar.JDateChooser tanggalSelesaiReport;
    private javax.swing.JTextField txtEmailPelanggan;
    private javax.swing.JTextField txtInstansiPelanggan;
    private javax.swing.JTextField txtKegiatanJadwal;
    private javax.swing.JTextField txtNIKPelanggan;
    private javax.swing.JTextField txtNamaPelanggan;
    private javax.swing.JTextField txtTeleponPelanggan;
    // End of variables declaration//GEN-END:variables
}
