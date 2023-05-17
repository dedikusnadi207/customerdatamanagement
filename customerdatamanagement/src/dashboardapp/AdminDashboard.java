/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboardapp;

import java.awt.Color;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
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
import model.JadwalSales;
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
public class AdminDashboard extends javax.swing.JFrame {
    /**
     * Creates new form Home
     */
    AdminRepository adminRepository;
    SalesRepository salesRepository;
    LayananRepository layananRepository;
    InformasiPerusahaanRepository informasiPerusahaanRepository;
    PelangganRepository pelangganRepository;
    TransaksiRepository reportRepository;
    TransaksiRepository pembayaranRepository;
    PembayaranRepository pembayaranDetailRepository;
    JadwalSalesRepository jadwalSalesRepository;
    ArrayList<model.Sales> salesOptions;
    ArrayList<model.Pelanggan> pelangganOptions;
    public AdminDashboard() {
        initComponents();
        adminRepository = new AdminRepository();
        salesRepository = new SalesRepository();
        layananRepository = new LayananRepository();
        informasiPerusahaanRepository = new InformasiPerusahaanRepository();
        pelangganRepository = new PelangganRepository();
        reportRepository = new TransaksiRepository();
        pembayaranRepository = new TransaksiRepository();
        pembayaranDetailRepository = new PembayaranRepository();
        jadwalSalesRepository = new JadwalSalesRepository(true);
        selectMenu("dashboard");
        JTextField[] fields = {txtHargaLayanan, txtJumlahBayarPembayaranDetail};
        validator.handleNumberOnly(fields);
    }
    int infoId;
    private void selectMenu(String menu) {
        panel_dashboard.setVisible(menu.equals("dashboard"));
        Map<String, JPanel> mapPanel = MapCustom.of("admin", panel_admin, "sales", panel_sales, "layanan", panel_layanan, "pelanggan", panel_pelanggan, "report", panel_report, "jadwal", panel_jadwal, "pembayaran", panel_pembayaran, "pembayaran_detail", panel_pembayaran_detail);
        Map<String, JPanel> mapBtnNav = MapCustom.of("admin", btn_nav_admin, "sales", btn_nav_sales, "layanan", btn_nav_layanan, "pelanggan", btn_nav_pelanggan, "report", btn_nav_report, "jadwal", btn_nav_jadwal, "pembayaran", btn_nav_pembayaran, "pembayaran_detail", btn_nav_pembayaran);
        Map<String, Repository> mapRepositories = MapCustom.of("admin", adminRepository, "sales", salesRepository, "layanan", layananRepository, "pelanggan", pelangganRepository, "report", reportRepository, "jadwal", jadwalSalesRepository, "pembayaran", pembayaranRepository, "pembayaran_detail", pembayaranDetailRepository);
        Map<String, JTable> mapTable = MapCustom.of("admin", tbl_admin, "sales", tbl_sales, "layanan", tbl_layanan, "pelanggan", tabel_pelanggan, "report", tabel_report, "jadwal", tabel_jadwal, "pembayaran", tabel_pembayaran, "pembayaran_detail", tabel_pembayaran_detail);
        try {
            if (menu.equals("dashboard")) {
                lbl_dashboard_layanan.setText(layananRepository.count()+"");
                lbl_dashboard_sales.setText(salesRepository.count()+"");
            } else if (menu.equals("jadwal")) {
                salesOptions = salesRepository.all();
                cSalesJadwal.removeAllItems();
                cSalesJadwal.addItem("--- Semua Sales ---");
                salesOptions.forEach((sales) -> {
                    cSalesJadwal.addItem(sales.getNama());
                });
            } else if (menu.equals("pembayaran")) {
                pelangganOptions = pelangganRepository.all();
                cPelangganPembayaran.removeAllItems();
                cPelangganPembayaran.addItem("--- Semua Pelanggan ---");
                pelangganOptions.forEach((pelanggan) -> {
                    cPelangganPembayaran.addItem(pelanggan.getNamaPic() + " - " + pelanggan.getNamaInstansi());
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        mapPanel.keySet().forEach((key) -> {
            if (key.equals(menu)) {
                mapPanel.get(key).setVisible(true);
                onClick(mapBtnNav.get(key));
                if (key.equals("report")) {
                    tanggalMulaiReport.setDate(DateUtils.addDays(new Date(),-7));
                    tanggalSelesaiReport.setDate(new Date());
                    tampilReport();
                } else if (key.equals("jadwal")) {
                    tanggalMulaiJadwal.setDate(DateUtils.addDays(new Date(),-30));
                    tanggalSelesaiJadwal.setDate(new Date());
                    tampilJadwal();
                } else if (key.equals("pembayaran")) {
                    tanggalMulaiPembayaran.setDate(DateUtils.addDays(new Date(),-30));
                    tanggalSelesaiPembayaran.setDate(new Date());
                    tampilPembayaran();
                } else if (key.equals("pembayaran_detail")) {
                    try {
                        model.Transaksi data = pembayaranRepository.selectedData(tabel_pembayaran);
                        ArrayList<model.Pembayaran> rows = pembayaranDetailRepository.all(data.getIdTransaksi());
                        pembayaranDetailRepository.renderDataTable(tabel_pembayaran_detail, rows);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Gagal menampilkan data Pembayaran! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    mapRepositories.get(key).renderDataTable(mapTable.get(key));
                }
            } else {
                mapPanel.get(key).setVisible(false);
                if (!key.equals("pembayaran_detail")) {
                    onLeaveClick(mapBtnNav.get(key));
                }
            }
        });
    }
    
    void tampilJadwal() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = simpleDateFormat.format(tanggalMulaiJadwal.getDate());
            String endTime = simpleDateFormat.format(tanggalSelesaiJadwal.getDate());
            ArrayList<JadwalSales> jadwalSales = new ArrayList<>();
            if (cSalesJadwal.getSelectedIndex() == 0) {
                jadwalSales = jadwalSalesRepository.all(startTime, endTime);
            } else {
                int idSales = salesOptions.get(cSalesJadwal.getSelectedIndex() - 1).getIdSales();
                jadwalSales = jadwalSalesRepository.all(idSales, startTime, endTime);
            }
            jadwalSalesRepository.renderDataTable(tabel_jadwal, jadwalSales);
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = simpleDateFormat.format(tanggalMulaiPembayaran.getDate());
            String endTime = simpleDateFormat.format(tanggalSelesaiPembayaran.getDate());
            ArrayList<model.Transaksi> rows = new ArrayList<>();
            if (cPelangganPembayaran.getSelectedIndex() == 0) {
                rows = pembayaranRepository.all(startTime, endTime);
            } else {
                int idPelanggan = pelangganOptions.get(cPelangganPembayaran.getSelectedIndex() - 1).getIdPelanggan();
                rows = pembayaranRepository.all(idPelanggan, startTime, endTime);
            }
            pembayaranRepository.renderDataTable(tabel_pembayaran, rows);
        } catch (Exception ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void clearFormAdmin(){
        txtNamaAdmin.setText("");
        txtEmailAdmin.setText("");
        txtPasswordAdmin.setText("");
        tbl_admin.clearSelection();
    }
    void clearFormSales(){
        txtNamaSales.setText("");
        txtEmailSales.setText("");
        txtPasswordSales.setText("");
        tbl_sales.clearSelection();
    }
    void clearFormPembayaranDetail(){
        tanggalBayarPembayaranDetail.setDate(null);
        txtJumlahBayarPembayaranDetail.setText("");
        txtKeteranganPembayaranDetail.setText("");
        tabel_pembayaran_detail.clearSelection();
    }
    void clearFormLayanan(){
        txtNamaLayanan.setText("");
        txtDeskripsiLayanan.setText("");
        txtHargaLayanan.setText("");
        tbl_layanan.clearSelection();
    }
    void logout(){
        this.dispose();
        new Login().setVisible(true);
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
        btn_nav_admin = new javax.swing.JPanel();
        lbl_admin = new javax.swing.JLabel();
        btn_nav_sales = new javax.swing.JPanel();
        lbl_sales = new javax.swing.JLabel();
        btn_nav_layanan = new javax.swing.JPanel();
        lbl_layanan = new javax.swing.JLabel();
        btn_nav_pembayaran = new javax.swing.JPanel();
        lbl_pembayaran = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        btn_nav_pelanggan = new javax.swing.JPanel();
        lbl_pelanggan = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        btn_nav_report = new javax.swing.JPanel();
        lbl_transaksi = new javax.swing.JLabel();
        btn_nav_jadwal = new javax.swing.JPanel();
        lbl_jadwal = new javax.swing.JLabel();
        panel_dashboard = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        nav_panel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lbl_dashboard_layanan = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lbl_dashboard_sales = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        panel_admin = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        panel_tabel1 = new javax.swing.JScrollPane();
        tbl_admin = new javax.swing.JTable();
        btn_tambah1 = new com.k33ptoo.components.KButton();
        btn_edit1 = new com.k33ptoo.components.KButton();
        btn_hapus1 = new com.k33ptoo.components.KButton();
        btn_hapus2 = new com.k33ptoo.components.KButton();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();
        label3 = new java.awt.Label();
        txtPasswordAdmin = new javax.swing.JPasswordField();
        txtNamaAdmin = new javax.swing.JTextField();
        txtEmailAdmin = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        btn_tambah_pelanggan4 = new com.k33ptoo.components.KButton();
        panel_sales = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        panel_tabel2 = new javax.swing.JScrollPane();
        tbl_sales = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        btn_tambah5 = new com.k33ptoo.components.KButton();
        btn_edit5 = new com.k33ptoo.components.KButton();
        btn_hapus9 = new com.k33ptoo.components.KButton();
        btn_hapus10 = new com.k33ptoo.components.KButton();
        label13 = new java.awt.Label();
        label14 = new java.awt.Label();
        label15 = new java.awt.Label();
        txtPasswordSales = new javax.swing.JPasswordField();
        txtNamaSales = new javax.swing.JTextField();
        txtEmailSales = new javax.swing.JTextField();
        btn_tambah_pelanggan5 = new com.k33ptoo.components.KButton();
        panel_layanan = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        panel_tabel3 = new javax.swing.JScrollPane();
        tbl_layanan = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        btn_tambah6 = new com.k33ptoo.components.KButton();
        btn_edit6 = new com.k33ptoo.components.KButton();
        btn_hapus11 = new com.k33ptoo.components.KButton();
        btn_hapus12 = new com.k33ptoo.components.KButton();
        label16 = new java.awt.Label();
        label17 = new java.awt.Label();
        label18 = new java.awt.Label();
        txtNamaLayanan = new javax.swing.JTextField();
        txtDeskripsiLayanan = new javax.swing.JTextField();
        txtHargaLayanan = new javax.swing.JTextField();
        btn_tambah_pelanggan6 = new com.k33ptoo.components.KButton();
        panel_pembayaran = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        btn_tampilkan_pembayaran = new com.k33ptoo.components.KButton();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        tanggalMulaiPembayaran = new com.toedter.calendar.JDateChooser();
        tanggalSelesaiPembayaran = new com.toedter.calendar.JDateChooser();
        jLabel37 = new javax.swing.JLabel();
        cPelangganPembayaran = new javax.swing.JComboBox<>();
        panel_tabel8 = new javax.swing.JScrollPane();
        tabel_pembayaran = new javax.swing.JTable();
        btn_detail_pembayaran = new com.k33ptoo.components.KButton();
        panel_pembayaran_detail = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jButton12 = new javax.swing.JButton();
        panel_tabel9 = new javax.swing.JScrollPane();
        tabel_pembayaran_detail = new javax.swing.JTable();
        lbl_kembali_pembayaran = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btn_tambah2 = new com.k33ptoo.components.KButton();
        btn_edit2 = new com.k33ptoo.components.KButton();
        btn_hapus3 = new com.k33ptoo.components.KButton();
        btn_hapus4 = new com.k33ptoo.components.KButton();
        jLabel32 = new javax.swing.JLabel();
        tanggalBayarPembayaranDetail = new com.toedter.calendar.JDateChooser();
        jLabel33 = new javax.swing.JLabel();
        txtJumlahBayarPembayaranDetail = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtKeteranganPembayaranDetail = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        tanggalMulaiPembayaranDetail = new com.toedter.calendar.JDateChooser();
        tanggalSelesaiPembayaranDetail = new com.toedter.calendar.JDateChooser();
        txtTotalHargaPembayaranDetail = new javax.swing.JTextField();
        txtPelangganPembayaranDetail = new javax.swing.JTextField();
        txtLayananPembayaranDetail = new javax.swing.JTextField();
        btn_tambah_pelanggan8 = new com.k33ptoo.components.KButton();
        panel_pelanggan = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        panel_tabel4 = new javax.swing.JScrollPane();
        tabel_pelanggan = new javax.swing.JTable();
        btn_cetak_pelanggan = new com.k33ptoo.components.KButton();
        panel_report = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        btn_tambah_pelanggan2 = new com.k33ptoo.components.KButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        tanggalMulaiReport = new com.toedter.calendar.JDateChooser();
        tanggalSelesaiReport = new com.toedter.calendar.JDateChooser();
        btn_tambah_pelanggan7 = new com.k33ptoo.components.KButton();
        panel_tabel5 = new javax.swing.JScrollPane();
        tabel_report = new javax.swing.JTable();
        jButton14 = new javax.swing.JButton();
        panel_jadwal = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        panel_tabel6 = new javax.swing.JScrollPane();
        tabel_jadwal = new javax.swing.JTable();
        btn_cetak_jadwal = new com.k33ptoo.components.KButton();
        jLabel45 = new javax.swing.JLabel();
        tanggalMulaiJadwal = new com.toedter.calendar.JDateChooser();
        tanggalSelesaiJadwal = new com.toedter.calendar.JDateChooser();
        jLabel43 = new javax.swing.JLabel();
        cSalesJadwal = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        btn_tampilkan_jadwal = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("APLIKASI CUSTOMER DATA MANAGEMENT");
        setBackground(new java.awt.Color(204, 204, 204));
        setLocationByPlatform(true);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_nav_admin.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_admin.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_admin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_adminMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_adminMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_adminMouseExited(evt);
            }
        });

        lbl_admin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_admin.setText("Admin");
        lbl_admin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_adminMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_adminLayout = new javax.swing.GroupLayout(btn_nav_admin);
        btn_nav_admin.setLayout(btn_nav_adminLayout);
        btn_nav_adminLayout.setHorizontalGroup(
            btn_nav_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_adminLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_admin)
                .addGap(0, 91, Short.MAX_VALUE))
        );
        btn_nav_adminLayout.setVerticalGroup(
            btn_nav_adminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_adminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_admin, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_admin, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 175, 190, -1));

        btn_nav_sales.setBackground(new java.awt.Color(255, 255, 255));
        btn_nav_sales.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_nav_sales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_nav_salesMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn_nav_salesMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn_nav_salesMouseExited(evt);
            }
        });

        lbl_sales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_sales.setText("Sales");
        lbl_sales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_salesMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_salesLayout = new javax.swing.GroupLayout(btn_nav_sales);
        btn_nav_sales.setLayout(btn_nav_salesLayout);
        btn_nav_salesLayout.setHorizontalGroup(
            btn_nav_salesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_salesLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_sales)
                .addGap(0, 99, Short.MAX_VALUE))
        );
        btn_nav_salesLayout.setVerticalGroup(
            btn_nav_salesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_salesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_sales, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_sales, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 227, 190, -1));

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

        jPanel1.add(btn_nav_layanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 279, 190, -1));

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
        lbl_pembayaran.setText("Pembayaran");
        lbl_pembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_pembayaranMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_pembayaranLayout = new javax.swing.GroupLayout(btn_nav_pembayaran);
        btn_nav_pembayaran.setLayout(btn_nav_pembayaranLayout);
        btn_nav_pembayaranLayout.setHorizontalGroup(
            btn_nav_pembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_pembayaranLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_pembayaran)
                .addGap(0, 59, Short.MAX_VALUE))
        );
        btn_nav_pembayaranLayout.setVerticalGroup(
            btn_nav_pembayaranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_pembayaranLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_pembayaran, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 190, -1));
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 39, -1, -1));

        jLabel21.setFont(new java.awt.Font("Impact", 0, 16)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 102, 102));
        jLabel21.setText("ADMIN DASHBOARD");
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 130, -1));

        jPanel14.setBackground(new java.awt.Color(0, 204, 204));

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

        jPanel1.add(btn_nav_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 330, 190, -1));

        jPanel13.setBackground(new java.awt.Color(0, 102, 102));

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

        lbl_transaksi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbl_transaksi.setText("Transaksi");
        lbl_transaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_transaksiMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btn_nav_reportLayout = new javax.swing.GroupLayout(btn_nav_report);
        btn_nav_report.setLayout(btn_nav_reportLayout);
        btn_nav_reportLayout.setHorizontalGroup(
            btn_nav_reportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_nav_reportLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(lbl_transaksi)
                .addGap(0, 76, Short.MAX_VALUE))
        );
        btn_nav_reportLayout.setVerticalGroup(
            btn_nav_reportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_reportLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_transaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_report, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 380, 190, 60));

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
        lbl_jadwal.setText("Jadwal Sales");
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
                .addGap(59, 59, 59)
                .addComponent(lbl_jadwal)
                .addGap(0, 59, Short.MAX_VALUE))
        );
        btn_nav_jadwalLayout.setVerticalGroup(
            btn_nav_jadwalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btn_nav_jadwalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_jadwal, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(btn_nav_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 438, 190, 60));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 190, 580));

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

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 102));
        jLabel5.setText("APLIKASI PENGELOLAAN DATA PELANGGAN");
        panel_dashboard.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        nav_panel.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 153));
        jLabel7.setText("Jumlah Layanan");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        lbl_dashboard_layanan.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_dashboard_layanan.setForeground(new java.awt.Color(0, 51, 153));
        lbl_dashboard_layanan.setText("500");

        jPanel11.setBackground(new java.awt.Color(0, 204, 204));

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
                .addComponent(lbl_dashboard_layanan, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_dashboard_layanan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(83, 83, 83))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_dashboard_sales.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_dashboard_sales.setForeground(new java.awt.Color(0, 51, 153));
        lbl_dashboard_sales.setText("50");
        jPanel4.add(lbl_dashboard_sales, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 22, -1, -1));

        jPanel10.setBackground(new java.awt.Color(0, 204, 204));

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
        jLabel10.setText("Jumlah Sales");

        javax.swing.GroupLayout nav_panelLayout = new javax.swing.GroupLayout(nav_panel);
        nav_panel.setLayout(nav_panelLayout);
        nav_panelLayout.setHorizontalGroup(
            nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(nav_panelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addGroup(nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(nav_panelLayout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nav_panelLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(196, Short.MAX_VALUE))
        );
        nav_panelLayout.setVerticalGroup(
            nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, nav_panelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(nav_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(nav_panelLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(nav_panelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        panel_dashboard.add(nav_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, 480, 150));

        jButton9.setBackground(new java.awt.Color(0, 102, 102));
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
        panel_dashboard.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        getContentPane().add(panel_dashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

        panel_admin.setBackground(new java.awt.Color(247, 247, 247));
        panel_admin.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_adminMouseDragged(evt);
            }
        });
        panel_admin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_adminMousePressed(evt);
            }
        });
        panel_admin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 102));
        jLabel6.setText("DATA ADMIN APLIKASI");
        panel_admin.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel1.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tbl_admin.setBackground(new java.awt.Color(247, 247, 247));
        tbl_admin.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbl_admin.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_admin.setGridColor(new java.awt.Color(247, 247, 247));
        tbl_admin.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tbl_admin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_adminMouseClicked(evt);
            }
        });
        panel_tabel1.setViewportView(tbl_admin);

        panel_admin.add(panel_tabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 710, 240));

        btn_tambah1.setText("Tambah");
        btn_tambah1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah1ActionPerformed(evt);
            }
        });
        panel_admin.add(btn_tambah1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 83, 29));

        btn_edit1.setText("Edit");
        btn_edit1.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit1ActionPerformed(evt);
            }
        });
        panel_admin.add(btn_edit1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 120, 83, 29));

        btn_hapus1.setText("Hapus");
        btn_hapus1.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus1ActionPerformed(evt);
            }
        });
        panel_admin.add(btn_hapus1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, 83, 29));

        btn_hapus2.setText("Bersihkan");
        btn_hapus2.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_hapus2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus2ActionPerformed(evt);
            }
        });
        panel_admin.add(btn_hapus2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 200, 83, 29));

        label1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label1.setText("Password");
        panel_admin.add(label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, -1, -1));

        label2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label2.setText("Nama");
        panel_admin.add(label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        label3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label3.setText("Email");
        panel_admin.add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));
        panel_admin.add(txtPasswordAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 320, -1));

        txtNamaAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaAdminActionPerformed(evt);
            }
        });
        panel_admin.add(txtNamaAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 320, -1));

        txtEmailAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailAdminActionPerformed(evt);
            }
        });
        panel_admin.add(txtEmailAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 320, -1));

        jButton8.setBackground(new java.awt.Color(0, 102, 102));
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
        panel_admin.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        btn_tambah_pelanggan4.setText("Cetak");
        btn_tambah_pelanggan4.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan4ActionPerformed(evt);
            }
        });
        panel_admin.add(btn_tambah_pelanggan4, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 240, 83, 29));

        getContentPane().add(panel_admin, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

        panel_sales.setBackground(new java.awt.Color(247, 247, 247));
        panel_sales.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_salesMouseDragged(evt);
            }
        });
        panel_sales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_salesMousePressed(evt);
            }
        });
        panel_sales.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 102, 102));
        jLabel13.setText("DATA SALES APLIKASI");
        panel_sales.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel2.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tbl_sales.setBackground(new java.awt.Color(247, 247, 247));
        tbl_sales.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbl_sales.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_sales.setGridColor(new java.awt.Color(247, 247, 247));
        tbl_sales.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tbl_sales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_salesMouseClicked(evt);
            }
        });
        panel_tabel2.setViewportView(tbl_sales);

        panel_sales.add(panel_tabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 710, 280));

        jButton7.setBackground(new java.awt.Color(0, 102, 102));
        jButton7.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("X ");
        jButton7.setBorder(null);
        jButton7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        panel_sales.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        btn_tambah5.setText("Tambah");
        btn_tambah5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah5ActionPerformed(evt);
            }
        });
        panel_sales.add(btn_tambah5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 83, 29));

        btn_edit5.setText("Edit");
        btn_edit5.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit5ActionPerformed(evt);
            }
        });
        panel_sales.add(btn_edit5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 120, 83, 29));

        btn_hapus9.setText("Hapus");
        btn_hapus9.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus9ActionPerformed(evt);
            }
        });
        panel_sales.add(btn_hapus9, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, 83, 29));

        btn_hapus10.setText("Bersihkan");
        btn_hapus10.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_hapus10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus10ActionPerformed(evt);
            }
        });
        panel_sales.add(btn_hapus10, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 200, 83, 29));

        label13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label13.setText("Password");
        panel_sales.add(label13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, -1, -1));

        label14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label14.setText("Nama");
        panel_sales.add(label14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        label15.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label15.setText("Email");
        panel_sales.add(label15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));
        panel_sales.add(txtPasswordSales, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 320, -1));

        txtNamaSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaSalesActionPerformed(evt);
            }
        });
        panel_sales.add(txtNamaSales, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 320, -1));

        txtEmailSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailSalesActionPerformed(evt);
            }
        });
        panel_sales.add(txtEmailSales, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 320, -1));

        btn_tambah_pelanggan5.setText("Cetak");
        btn_tambah_pelanggan5.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan5ActionPerformed(evt);
            }
        });
        panel_sales.add(btn_tambah_pelanggan5, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 240, 83, 29));

        getContentPane().add(panel_sales, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

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

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 102, 102));
        jLabel14.setText("DATA LAYANAN APLIKASI");
        panel_layanan.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        panel_tabel3.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        tbl_layanan.setBackground(new java.awt.Color(247, 247, 247));
        tbl_layanan.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tbl_layanan.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_layanan.setGridColor(new java.awt.Color(247, 247, 247));
        tbl_layanan.setSelectionBackground(new java.awt.Color(96, 83, 150));
        tbl_layanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_layananMouseClicked(evt);
            }
        });
        panel_tabel3.setViewportView(tbl_layanan);

        panel_layanan.add(panel_tabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 710, 320));

        jButton6.setBackground(new java.awt.Color(0, 102, 102));
        jButton6.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("X ");
        jButton6.setBorder(null);
        jButton6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        panel_layanan.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        btn_tambah6.setText("Tambah");
        btn_tambah6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah6ActionPerformed(evt);
            }
        });
        panel_layanan.add(btn_tambah6, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 80, 83, 29));

        btn_edit6.setText("Edit");
        btn_edit6.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit6ActionPerformed(evt);
            }
        });
        panel_layanan.add(btn_edit6, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 120, 83, 29));

        btn_hapus11.setText("Hapus");
        btn_hapus11.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus11ActionPerformed(evt);
            }
        });
        panel_layanan.add(btn_hapus11, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, 83, 29));

        btn_hapus12.setText("Bersihkan");
        btn_hapus12.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_hapus12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus12ActionPerformed(evt);
            }
        });
        panel_layanan.add(btn_hapus12, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 200, 83, 29));

        label16.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label16.setText("Harga");
        panel_layanan.add(label16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, -1, -1));

        label17.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label17.setText("Nama Layanan");
        panel_layanan.add(label17, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        label18.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        label18.setText("Deskripsi");
        panel_layanan.add(label18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        txtNamaLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNamaLayananActionPerformed(evt);
            }
        });
        panel_layanan.add(txtNamaLayanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 320, -1));

        txtDeskripsiLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDeskripsiLayananActionPerformed(evt);
            }
        });
        panel_layanan.add(txtDeskripsiLayanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 320, -1));

        txtHargaLayanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHargaLayananActionPerformed(evt);
            }
        });
        panel_layanan.add(txtHargaLayanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 190, 320, -1));

        btn_tambah_pelanggan6.setText("Cetak");
        btn_tambah_pelanggan6.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan6ActionPerformed(evt);
            }
        });
        panel_layanan.add(btn_tambah_pelanggan6, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 240, 83, 29));

        getContentPane().add(panel_layanan, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

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

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 102, 102));
        jLabel19.setText("DATA PEMBAYARAN");
        panel_pembayaran.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        jButton10.setBackground(new java.awt.Color(0, 102, 102));
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
        panel_pembayaran.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        btn_tampilkan_pembayaran.setText("Tampilkan");
        btn_tampilkan_pembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilkan_pembayaranActionPerformed(evt);
            }
        });
        panel_pembayaran.add(btn_tampilkan_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 190, 83, 29));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 102, 102));
        jLabel24.setText("Tanggal Mulai");
        panel_pembayaran.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 140, 30));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 102, 102));
        jLabel25.setText("Tanggal Selesai");
        panel_pembayaran.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 140, 30));
        panel_pembayaran.add(tanggalMulaiPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 370, -1));
        panel_pembayaran.add(tanggalSelesaiPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 120, 370, -1));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 102, 102));
        jLabel37.setText("Pelanggan");
        panel_pembayaran.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 140, 30));
        panel_pembayaran.add(cPelangganPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 370, -1));

        panel_tabel8.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

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
        panel_tabel8.setViewportView(tabel_pembayaran);

        panel_pembayaran.add(panel_tabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 710, 250));

        btn_detail_pembayaran.setText("Detail");
        btn_detail_pembayaran.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_detail_pembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_detail_pembayaranActionPerformed(evt);
            }
        });
        panel_pembayaran.add(btn_detail_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 500, 83, 29));

        getContentPane().add(panel_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

        panel_pembayaran_detail.setBackground(new java.awt.Color(247, 247, 247));
        panel_pembayaran_detail.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panel_pembayaran_detailMouseDragged(evt);
            }
        });
        panel_pembayaran_detail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panel_pembayaran_detailMousePressed(evt);
            }
        });
        panel_pembayaran_detail.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 102, 102));
        jLabel26.setText("DATA DETAIL PEMBAYARAN");
        panel_pembayaran_detail.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 410, 20));

        jButton12.setBackground(new java.awt.Color(0, 102, 102));
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
        panel_pembayaran_detail.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        panel_tabel9.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

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
        panel_tabel9.setViewportView(tabel_pembayaran_detail);

        panel_pembayaran_detail.add(panel_tabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 710, 230));

        lbl_kembali_pembayaran.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_kembali_pembayaran.setForeground(new java.awt.Color(0, 0, 204));
        lbl_kembali_pembayaran.setText("Kembali");
        lbl_kembali_pembayaran.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_kembali_pembayaran.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_kembali_pembayaranMouseClicked(evt);
            }
        });
        panel_pembayaran_detail.add(lbl_kembali_pembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Form Input"));

        btn_tambah2.setText("Tambah");
        btn_tambah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah2ActionPerformed(evt);
            }
        });

        btn_edit2.setText("Edit");
        btn_edit2.setkStartColor(new java.awt.Color(0, 51, 153));
        btn_edit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_edit2ActionPerformed(evt);
            }
        });

        btn_hapus3.setText("Hapus");
        btn_hapus3.setkStartColor(new java.awt.Color(255, 0, 0));
        btn_hapus3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus3ActionPerformed(evt);
            }
        });

        btn_hapus4.setText("Bersihkan");
        btn_hapus4.setkStartColor(new java.awt.Color(255, 102, 51));
        btn_hapus4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapus4ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(0, 102, 102));
        jLabel32.setText("Tanggal Bayar");

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 102, 102));
        jLabel33.setText("Jumlah Bayar");

        txtJumlahBayarPembayaranDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtJumlahBayarPembayaranDetailActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 102, 102));
        jLabel34.setText("Keterangan");

        txtKeteranganPembayaranDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKeteranganPembayaranDetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(tanggalBayarPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtJumlahBayarPembayaranDetail)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKeteranganPembayaranDetail)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_tambah2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_edit2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus3, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_hapus4, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btn_tambah2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_edit2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtJumlahBayarPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addComponent(btn_hapus3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(btn_hapus4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tanggalBayarPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtKeteranganPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 18, Short.MAX_VALUE))
        );

        panel_pembayaran_detail.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, 380, 190));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Transaksi"));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 102, 102));
        jLabel27.setText("Tanggal Mulai");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 102, 102));
        jLabel29.setText("Tanggal Selesai");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 102, 102));
        jLabel38.setText("Pelanggan");

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 102, 102));
        jLabel30.setText("Layanan");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 102, 102));
        jLabel31.setText("Total Harga");

        tanggalMulaiPembayaranDetail.setEnabled(false);

        tanggalSelesaiPembayaranDetail.setEnabled(false);

        txtTotalHargaPembayaranDetail.setEditable(false);
        txtTotalHargaPembayaranDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalHargaPembayaranDetailActionPerformed(evt);
            }
        });

        txtPelangganPembayaranDetail.setEditable(false);
        txtPelangganPembayaranDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPelangganPembayaranDetailActionPerformed(evt);
            }
        });

        txtLayananPembayaranDetail.setEditable(false);
        txtLayananPembayaranDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLayananPembayaranDetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(tanggalMulaiPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(tanggalSelesaiPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txtPelangganPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txtLayananPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(txtTotalHargaPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(tanggalMulaiPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(tanggalSelesaiPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txtPelangganPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txtLayananPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txtTotalHargaPembayaranDetail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_pembayaran_detail.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 320, 190));

        btn_tambah_pelanggan8.setText("Cetak");
        btn_tambah_pelanggan8.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan8ActionPerformed(evt);
            }
        });
        panel_pembayaran_detail.add(btn_tambah_pelanggan8, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 520, 83, 29));

        getContentPane().add(panel_pembayaran_detail, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

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

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 102, 102));
        jLabel15.setText("DATA PELANGGAN");
        panel_pelanggan.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        jButton11.setBackground(new java.awt.Color(0, 102, 102));
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
        panel_pelanggan.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        panel_tabel4.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

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
        panel_tabel4.setViewportView(tabel_pelanggan);

        panel_pelanggan.add(panel_tabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 710, 260));

        btn_cetak_pelanggan.setText("Cetak");
        btn_cetak_pelanggan.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_cetak_pelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetak_pelangganActionPerformed(evt);
            }
        });
        panel_pelanggan.add(btn_cetak_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, 83, 29));

        getContentPane().add(panel_pelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

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
        jLabel28.setForeground(new java.awt.Color(0, 102, 102));
        jLabel28.setText("LAPORAN TRANSAKSI");
        panel_report.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        btn_tambah_pelanggan2.setText("Tampilkan");
        btn_tambah_pelanggan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan2ActionPerformed(evt);
            }
        });
        panel_report.add(btn_tambah_pelanggan2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, 83, 29));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 102, 102));
        jLabel22.setText("Tanggal Mulai");
        panel_report.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 140, 30));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 102, 102));
        jLabel23.setText("Tanggal Selesai");
        panel_report.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 140, 30));
        panel_report.add(tanggalMulaiReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 80, 370, -1));
        panel_report.add(tanggalSelesaiReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 120, 370, -1));

        btn_tambah_pelanggan7.setText("Cetak");
        btn_tambah_pelanggan7.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_tambah_pelanggan7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambah_pelanggan7ActionPerformed(evt);
            }
        });
        panel_report.add(btn_tambah_pelanggan7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 150, 83, 29));

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

        jButton14.setBackground(new java.awt.Color(0, 102, 102));
        jButton14.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jButton14.setForeground(new java.awt.Color(255, 255, 255));
        jButton14.setText("X ");
        jButton14.setBorder(null);
        jButton14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jButton14.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        panel_report.add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        getContentPane().add(panel_report, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

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

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 102, 102));
        jLabel16.setText("DATA JADWAL SALES");
        panel_jadwal.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 23, 410, 37));

        jButton13.setBackground(new java.awt.Color(0, 102, 102));
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
        panel_jadwal.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 0, 190, 40));

        panel_tabel6.setBackground(new java.awt.Color(247, 247, 247));
        panel_tabel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

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
        panel_tabel6.setViewportView(tabel_jadwal);

        panel_jadwal.add(panel_tabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 710, 300));

        btn_cetak_jadwal.setText("Cetak");
        btn_cetak_jadwal.setkStartColor(new java.awt.Color(204, 0, 204));
        btn_cetak_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_cetak_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_cetak_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, 83, 29));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 102, 102));
        jLabel45.setText("Sales : ");
        panel_jadwal.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 70, 20));
        panel_jadwal.add(tanggalMulaiJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 100, 130, -1));
        panel_jadwal.add(tanggalSelesaiJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 100, 130, -1));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(0, 102, 102));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("-");
        panel_jadwal.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 100, 20, 20));
        panel_jadwal.add(cSalesJadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 370, -1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 102, 102));
        jLabel46.setText("Waktu : ");
        panel_jadwal.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 50, 20));

        btn_tampilkan_jadwal.setText("Tampilkan");
        btn_tampilkan_jadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilkan_jadwalActionPerformed(evt);
            }
        });
        panel_jadwal.add(btn_tampilkan_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 83, 29));

        getContentPane().add(panel_jadwal, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 770, 580));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_nav_adminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_adminMouseClicked
        selectMenu("admin");
    }//GEN-LAST:event_btn_nav_adminMouseClicked

    private void btn_nav_salesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_salesMouseClicked
        selectMenu("sales");
    }//GEN-LAST:event_btn_nav_salesMouseClicked

    private void btn_nav_layananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_layananMouseClicked
        selectMenu("layanan");
    }//GEN-LAST:event_btn_nav_layananMouseClicked

    private void btn_nav_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pembayaranMouseClicked
        selectMenu("pembayaran");
    }//GEN-LAST:event_btn_nav_pembayaranMouseClicked

    private void btn_nav_adminMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_adminMouseEntered
        // TODO add your handling code here:
 
    }//GEN-LAST:event_btn_nav_adminMouseEntered

    private void btn_nav_salesMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_salesMouseEntered
        // TODO add your handling code here:
         
    }//GEN-LAST:event_btn_nav_salesMouseEntered

    private void btn_nav_layananMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_layananMouseEntered
        // TODO add your handling code here:
     
    }//GEN-LAST:event_btn_nav_layananMouseEntered

    private void btn_nav_pembayaranMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pembayaranMouseEntered
        // TODO add your handling code here:]
         
    }//GEN-LAST:event_btn_nav_pembayaranMouseEntered

    private void btn_nav_adminMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_adminMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_adminMouseExited

    private void btn_nav_salesMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_salesMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_salesMouseExited

    private void btn_nav_layananMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_layananMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_layananMouseExited

    private void btn_nav_pembayaranMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pembayaranMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_pembayaranMouseExited

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

    private void lbl_adminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_adminMouseClicked
       
    }//GEN-LAST:event_lbl_adminMouseClicked

    private void lbl_salesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_salesMouseClicked
        
    }//GEN-LAST:event_lbl_salesMouseClicked

    private void lbl_layananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_layananMouseClicked
        
    }//GEN-LAST:event_lbl_layananMouseClicked

    private void lbl_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_pembayaranMouseClicked
        selectMenu("pembayaran");
    }//GEN-LAST:event_lbl_pembayaranMouseClicked

    private void jLabel21MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel21MouseClicked
        selectMenu("dashboard");
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel21MouseClicked

    private void btn_edit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit1ActionPerformed
        model.Admin admin = new model.Admin();
        admin.setNama(txtNamaAdmin.getText());
        admin.setEmail(txtEmailAdmin.getText());
        admin.setPassword(new String(txtPasswordAdmin.getPassword()));
        try {
            adminRepository.update(admin.toMap(), MapCustom.of("id_admin", adminRepository.selectedData(tbl_admin).getIdAdmin()));
            clearFormAdmin();
            selectMenu("admin");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data Admin! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_edit1ActionPerformed

    private void btn_hapus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus1ActionPerformed
        try {
            adminRepository.delete(MapCustom.of("id_admin", adminRepository.selectedData(tbl_admin).getIdAdmin()));
            clearFormAdmin();
            selectMenu("admin");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Admin! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_hapus1ActionPerformed

    private void panel_adminMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_adminMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_adminMouseDragged

    private void panel_adminMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_adminMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_adminMousePressed

    private void btn_hapus2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus2ActionPerformed
        clearFormAdmin();
    }//GEN-LAST:event_btn_hapus2ActionPerformed

    private void panel_salesMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_salesMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_salesMouseDragged

    private void panel_salesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_salesMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_salesMousePressed

    private void panel_layananMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_layananMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_layananMouseDragged

    private void panel_layananMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_layananMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_layananMousePressed

    private void btn_tambah1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah1ActionPerformed
        model.Admin admin = new model.Admin();
        admin.setNama(txtNamaAdmin.getText());
        admin.setEmail(txtEmailAdmin.getText());
        admin.setPassword(new String(txtPasswordAdmin.getPassword()));
        try {
            adminRepository.save(admin);
            clearFormAdmin();
            selectMenu("admin");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Admin! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah1ActionPerformed

    private void txtNamaAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaAdminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaAdminActionPerformed

    private void txtEmailAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailAdminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailAdminActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        logout();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        logout();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        logout();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        logout();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void tbl_adminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_adminMouseClicked
        model.Admin admin = adminRepository.selectedData(tbl_admin);
        txtNamaAdmin.setText(admin.getNama());
        txtEmailAdmin.setText(admin.getEmail());
        txtPasswordAdmin.setText(admin.getPassword());
    }//GEN-LAST:event_tbl_adminMouseClicked

    private void tbl_salesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_salesMouseClicked
        model.Sales sales = salesRepository.selectedData(tbl_sales);
        txtNamaSales.setText(sales.getNama());
        txtEmailSales.setText(sales.getEmail());
        txtPasswordSales.setText(sales.getPassword());
    }//GEN-LAST:event_tbl_salesMouseClicked

    private void tbl_layananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_layananMouseClicked
        model.Layanan layanan = layananRepository.selectedData(tbl_layanan);
        txtNamaLayanan.setText(layanan.getNama());
        txtDeskripsiLayanan.setText(layanan.getDeskripsi());
        System.out.println(layanan.getHarga());
        txtHargaLayanan.setText(Integer.toString(layanan.getHarga()));
    }//GEN-LAST:event_tbl_layananMouseClicked

    private void btn_tambah5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah5ActionPerformed
        model.Sales sales = new model.Sales();
        sales.setNama(txtNamaSales.getText());
        sales.setEmail(txtEmailSales.getText());
        sales.setPassword(new String(txtPasswordSales.getPassword()));
        try {
            salesRepository.save(sales);
            clearFormSales();
            selectMenu("sales");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Sales! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah5ActionPerformed

    private void btn_edit5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit5ActionPerformed
        model.Sales sales = new model.Sales();
        sales.setNama(txtNamaSales.getText());
        sales.setEmail(txtEmailSales.getText());
        sales.setPassword(new String(txtPasswordSales.getPassword()));
        try {
            salesRepository.update(sales.toMap(), MapCustom.of("id_sales", salesRepository.selectedData(tbl_sales).getIdSales()));
            clearFormSales();
            selectMenu("sales");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data Sales! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_edit5ActionPerformed

    private void btn_hapus9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus9ActionPerformed
        try {
            salesRepository.delete(MapCustom.of("id_sales", salesRepository.selectedData(tbl_sales).getIdSales()));
            clearFormSales();
            selectMenu("sales");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Sales! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_hapus9ActionPerformed

    private void btn_hapus10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus10ActionPerformed
        clearFormSales();
    }//GEN-LAST:event_btn_hapus10ActionPerformed

    private void txtNamaSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaSalesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaSalesActionPerformed

    private void txtEmailSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailSalesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailSalesActionPerformed

    private void btn_tambah6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah6ActionPerformed
        model.Layanan layanan = new model.Layanan();
        layanan.setNama(txtNamaLayanan.getText());
        layanan.setDeskripsi(txtDeskripsiLayanan.getText());
        try {
            layanan.setHarga(Integer.parseInt(txtHargaLayanan.getText()));
            layananRepository.save(layanan);
            clearFormLayanan();
            selectMenu("layanan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Layanan! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah6ActionPerformed

    private void btn_edit6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit6ActionPerformed
        model.Layanan layanan = new model.Layanan();
        layanan.setNama(txtNamaLayanan.getText());
        layanan.setDeskripsi(txtDeskripsiLayanan.getText());
        try {
            layanan.setHarga(Integer.parseInt(txtHargaLayanan.getText()));
            layananRepository.update(layanan.toMap(), MapCustom.of("id_layanan", layananRepository.selectedData(tbl_layanan).getIdLayanan()));
            clearFormLayanan();
            selectMenu("layanan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal mengubah data Layanan! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_edit6ActionPerformed

    private void btn_hapus11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus11ActionPerformed
        try {
            layananRepository.delete(MapCustom.of("id_layanan", layananRepository.selectedData(tbl_layanan).getIdLayanan()));
            clearFormLayanan();
            selectMenu("layanan");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Layanan! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_hapus11ActionPerformed

    private void btn_hapus12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus12ActionPerformed
        clearFormLayanan();
    }//GEN-LAST:event_btn_hapus12ActionPerformed

    private void txtNamaLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNamaLayananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNamaLayananActionPerformed

    private void txtDeskripsiLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDeskripsiLayananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeskripsiLayananActionPerformed

    private void txtHargaLayananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHargaLayananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHargaLayananActionPerformed

    private void btn_tambah_pelanggan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan4ActionPerformed
        try {
            File pelanggan = new File("src/report/admin.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), null, Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan4ActionPerformed

    private void btn_tambah_pelanggan5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan5ActionPerformed
        try {
            File pelanggan = new File("src/report/sales.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), null, Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan5ActionPerformed

    private void btn_tambah_pelanggan6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan6ActionPerformed
        try {
            File pelanggan = new File("src/report/layanan.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), null, Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan6ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        logout();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void panel_pembayaranMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pembayaranMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pembayaranMouseDragged

    private void panel_pembayaranMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pembayaranMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pembayaranMousePressed

    private void lbl_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_pelangganMouseClicked
        selectMenu("pelanggan");
    }//GEN-LAST:event_lbl_pelangganMouseClicked

    private void btn_nav_pelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pelangganMouseClicked
        selectMenu("pelanggan");
    }//GEN-LAST:event_btn_nav_pelangganMouseClicked

    private void btn_nav_pelangganMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pelangganMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_pelangganMouseEntered

    private void btn_nav_pelangganMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_pelangganMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_pelangganMouseExited

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        logout();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void panel_pelangganMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pelangganMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pelangganMouseDragged

    private void panel_pelangganMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pelangganMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pelangganMousePressed

    private void btn_cetak_pelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetak_pelangganActionPerformed
        try {
            File pelanggan = new File("src/report/pelanggan.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), null, Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_cetak_pelangganActionPerformed

    private void btn_tambah_pelanggan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan2ActionPerformed
        tampilReport();
    }//GEN-LAST:event_btn_tambah_pelanggan2ActionPerformed

    private void btn_tambah_pelanggan7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan7ActionPerformed
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            File pelanggan = new File("src/report/transaksi.jasper");
            JasperPrint jp = JasperFillManager.fillReport(pelanggan.getPath(), MapCustom.of("tanggalMulai", simpleDateFormat.format(tanggalMulaiReport.getDate()), "tanggalSelesai", simpleDateFormat.format(tanggalSelesaiReport.getDate())), Koneksi.Koneksi.koneksidb());
            JasperViewer.viewReport(jp, false);
        } catch (JRException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_tambah_pelanggan7ActionPerformed

    private void panel_reportMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_reportMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_reportMouseDragged

    private void panel_reportMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_reportMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_reportMousePressed

    private void lbl_transaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_transaksiMouseClicked
        selectMenu("report");
    }//GEN-LAST:event_lbl_transaksiMouseClicked

    private void btn_nav_reportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_reportMouseClicked
        selectMenu("report");
    }//GEN-LAST:event_btn_nav_reportMouseClicked

    private void btn_nav_reportMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_reportMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_reportMouseEntered

    private void btn_nav_reportMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_reportMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_reportMouseExited

    private void lbl_jadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_jadwalMouseClicked
        selectMenu("jadwal");
    }//GEN-LAST:event_lbl_jadwalMouseClicked

    private void btn_nav_jadwalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_jadwalMouseClicked
        selectMenu("jadwal");
    }//GEN-LAST:event_btn_nav_jadwalMouseClicked

    private void btn_nav_jadwalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_jadwalMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_jadwalMouseEntered

    private void btn_nav_jadwalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_nav_jadwalMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_nav_jadwalMouseExited

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        logout();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void btn_cetak_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_cetak_jadwalActionPerformed
        try {
            File jadwalSales = new File("src/report/jadwal_sales.jasper");
            int idSales = 0;
            if (cSalesJadwal.getSelectedIndex() > 0) {
                idSales = salesOptions.get(cSalesJadwal.getSelectedIndex() - 1).getIdSales();
            }
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

    private void panel_jadwalMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_jadwalMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_jadwalMouseDragged

    private void panel_jadwalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_jadwalMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_jadwalMousePressed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        logout();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void btn_tampilkan_jadwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilkan_jadwalActionPerformed
        tampilJadwal();
    }//GEN-LAST:event_btn_tampilkan_jadwalActionPerformed

    private void btn_tampilkan_pembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilkan_pembayaranActionPerformed
        tampilPembayaran();
    }//GEN-LAST:event_btn_tampilkan_pembayaranActionPerformed

    private void tabel_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_pembayaranMouseClicked
        
    }//GEN-LAST:event_tabel_pembayaranMouseClicked

    private void btn_detail_pembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_detail_pembayaranActionPerformed
        if (tabel_pembayaran.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Pilih Data Transaksi Terlebih Dahulu");
        } else {
            try {
                model.Transaksi data = pembayaranRepository.selectedData(tabel_pembayaran);
                SimpleDateFormat sdfDb = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdfDb.parse(data.getTanggalMulai());
                Date endDate = sdfDb.parse(data.getTanggalSelesai());
                tanggalMulaiPembayaranDetail.setDate(startDate);
                tanggalSelesaiPembayaranDetail.setDate(endDate);
                txtPelangganPembayaranDetail.setText(data.getPelanggan().getNamaPic()+" - "+data.getPelanggan().getNamaInstansi());
                txtLayananPembayaranDetail.setText(data.getLayanan().getNama());
                txtTotalHargaPembayaranDetail.setText(data.getTotalHarga()+"");
                selectMenu("pembayaran_detail");
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }//GEN-LAST:event_btn_detail_pembayaranActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        logout();
    }//GEN-LAST:event_jButton12ActionPerformed

    private void tabel_pembayaran_detailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_pembayaran_detailMouseClicked
        try {
            model.Pembayaran pembayaran = pembayaranDetailRepository.selectedData(tabel_pembayaran_detail);
            SimpleDateFormat sdfDb = new SimpleDateFormat("yyyy-MM-dd");
            Date tglBayar = sdfDb.parse(pembayaran.getTglBayar());
            tanggalBayarPembayaranDetail.setDate(tglBayar);
            txtJumlahBayarPembayaranDetail.setText(pembayaran.getJumlahPembayaran()+"");
            txtKeteranganPembayaranDetail.setText(pembayaran.getKeterangan());
        } catch (ParseException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_tabel_pembayaran_detailMouseClicked

    private void panel_pembayaran_detailMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pembayaran_detailMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pembayaran_detailMouseDragged

    private void panel_pembayaran_detailMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_pembayaran_detailMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_panel_pembayaran_detailMousePressed

    private void lbl_kembali_pembayaranMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_kembali_pembayaranMouseClicked
//        selectMenu("pembayaran");
        panel_pembayaran_detail.setVisible(false);
        panel_pembayaran.setVisible(true);
    }//GEN-LAST:event_lbl_kembali_pembayaranMouseClicked

    private void txtPelangganPembayaranDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPelangganPembayaranDetailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPelangganPembayaranDetailActionPerformed

    private void txtLayananPembayaranDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLayananPembayaranDetailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLayananPembayaranDetailActionPerformed

    private void txtTotalHargaPembayaranDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalHargaPembayaranDetailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalHargaPembayaranDetailActionPerformed

    private void btn_tambah2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah2ActionPerformed
        try {
            model.Transaksi data = pembayaranRepository.selectedData(tabel_pembayaran);
            model.Pembayaran pembayaran = new model.Pembayaran();
            pembayaran.setIdAdmin(Admin.sessionAdmin.getIdAdmin());
            pembayaran.setIdTransaksi(data.getIdTransaksi());
            pembayaran.setJumlahPembayaran(Integer.parseInt(txtJumlahBayarPembayaranDetail.getText()));
            pembayaran.setKeterangan(txtKeteranganPembayaranDetail.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pembayaran.setTglBayar(sdf.format(tanggalBayarPembayaranDetail.getDate()));
            pembayaranDetailRepository.save(pembayaran);
            clearFormPembayaranDetail();
            selectMenu("pembayaran_detail");
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Pembayaran! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_tambah2ActionPerformed

    private void btn_edit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_edit2ActionPerformed
        try {
            model.Transaksi data = pembayaranRepository.selectedData(tabel_pembayaran);
            model.Pembayaran pembayaran = new model.Pembayaran();
            pembayaran.setIdAdmin(Admin.sessionAdmin.getIdAdmin());
            pembayaran.setIdTransaksi(data.getIdTransaksi());
            pembayaran.setJumlahPembayaran(Integer.parseInt(txtJumlahBayarPembayaranDetail.getText()));
            pembayaran.setKeterangan(txtKeteranganPembayaranDetail.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            pembayaran.setTglBayar(sdf.format(tanggalBayarPembayaranDetail.getDate()));
            pembayaranDetailRepository.update(pembayaran.toMap(), MapCustom.of("id_pembayaran", pembayaranDetailRepository.selectedData(tabel_pembayaran_detail).getIdPembayaran()));
            clearFormPembayaranDetail();
            selectMenu("pembayaran_detail");
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan data Pembayaran! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_edit2ActionPerformed

    private void btn_hapus3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus3ActionPerformed
        try {
            pembayaranDetailRepository.delete(MapCustom.of("id_pembayaran", pembayaranDetailRepository.selectedData(tabel_pembayaran_detail).getIdPembayaran()));
            clearFormPembayaranDetail();
            selectMenu("pembayaran_detail");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus data Pembayaran! : "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_hapus3ActionPerformed

    private void btn_hapus4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapus4ActionPerformed
        clearFormPembayaranDetail();
    }//GEN-LAST:event_btn_hapus4ActionPerformed

    private void txtJumlahBayarPembayaranDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtJumlahBayarPembayaranDetailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtJumlahBayarPembayaranDetailActionPerformed

    private void txtKeteranganPembayaranDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKeteranganPembayaranDetailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKeteranganPembayaranDetailActionPerformed

    private void btn_tambah_pelanggan8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambah_pelanggan8ActionPerformed
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
    }//GEN-LAST:event_btn_tambah_pelanggan8ActionPerformed

    int xx ,xy;
    
       
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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btn_cetak_jadwal;
    private com.k33ptoo.components.KButton btn_cetak_pelanggan;
    private com.k33ptoo.components.KButton btn_detail_pembayaran;
    private com.k33ptoo.components.KButton btn_edit1;
    private com.k33ptoo.components.KButton btn_edit2;
    private com.k33ptoo.components.KButton btn_edit5;
    private com.k33ptoo.components.KButton btn_edit6;
    private com.k33ptoo.components.KButton btn_hapus1;
    private com.k33ptoo.components.KButton btn_hapus10;
    private com.k33ptoo.components.KButton btn_hapus11;
    private com.k33ptoo.components.KButton btn_hapus12;
    private com.k33ptoo.components.KButton btn_hapus2;
    private com.k33ptoo.components.KButton btn_hapus3;
    private com.k33ptoo.components.KButton btn_hapus4;
    private com.k33ptoo.components.KButton btn_hapus9;
    private javax.swing.JPanel btn_nav_admin;
    private javax.swing.JPanel btn_nav_jadwal;
    private javax.swing.JPanel btn_nav_layanan;
    private javax.swing.JPanel btn_nav_pelanggan;
    private javax.swing.JPanel btn_nav_pembayaran;
    private javax.swing.JPanel btn_nav_report;
    private javax.swing.JPanel btn_nav_sales;
    private com.k33ptoo.components.KButton btn_tambah1;
    private com.k33ptoo.components.KButton btn_tambah2;
    private com.k33ptoo.components.KButton btn_tambah5;
    private com.k33ptoo.components.KButton btn_tambah6;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan2;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan4;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan5;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan6;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan7;
    private com.k33ptoo.components.KButton btn_tambah_pelanggan8;
    private com.k33ptoo.components.KButton btn_tampilkan_jadwal;
    private com.k33ptoo.components.KButton btn_tampilkan_pembayaran;
    private javax.swing.JComboBox<String> cPelangganPembayaran;
    private javax.swing.JComboBox<String> cSalesJadwal;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private java.awt.Label label1;
    private java.awt.Label label13;
    private java.awt.Label label14;
    private java.awt.Label label15;
    private java.awt.Label label16;
    private java.awt.Label label17;
    private java.awt.Label label18;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private javax.swing.JLabel lbl_admin;
    private javax.swing.JLabel lbl_dashboard_layanan;
    private javax.swing.JLabel lbl_dashboard_sales;
    private javax.swing.JLabel lbl_jadwal;
    private javax.swing.JLabel lbl_kembali_pembayaran;
    private javax.swing.JLabel lbl_layanan;
    private javax.swing.JLabel lbl_pelanggan;
    private javax.swing.JLabel lbl_pembayaran;
    private javax.swing.JLabel lbl_sales;
    private javax.swing.JLabel lbl_transaksi;
    private javax.swing.JPanel nav_panel;
    private javax.swing.JPanel panel_admin;
    private javax.swing.JPanel panel_dashboard;
    private javax.swing.JPanel panel_jadwal;
    private javax.swing.JPanel panel_layanan;
    private javax.swing.JPanel panel_pelanggan;
    private javax.swing.JPanel panel_pembayaran;
    private javax.swing.JPanel panel_pembayaran_detail;
    private javax.swing.JPanel panel_report;
    private javax.swing.JPanel panel_sales;
    private javax.swing.JScrollPane panel_tabel1;
    private javax.swing.JScrollPane panel_tabel2;
    private javax.swing.JScrollPane panel_tabel3;
    private javax.swing.JScrollPane panel_tabel4;
    private javax.swing.JScrollPane panel_tabel5;
    private javax.swing.JScrollPane panel_tabel6;
    private javax.swing.JScrollPane panel_tabel8;
    private javax.swing.JScrollPane panel_tabel9;
    private javax.swing.JTable tabel_jadwal;
    private javax.swing.JTable tabel_pelanggan;
    private javax.swing.JTable tabel_pembayaran;
    private javax.swing.JTable tabel_pembayaran_detail;
    private javax.swing.JTable tabel_report;
    private com.toedter.calendar.JDateChooser tanggalBayarPembayaranDetail;
    private com.toedter.calendar.JDateChooser tanggalMulaiJadwal;
    private com.toedter.calendar.JDateChooser tanggalMulaiPembayaran;
    private com.toedter.calendar.JDateChooser tanggalMulaiPembayaranDetail;
    private com.toedter.calendar.JDateChooser tanggalMulaiReport;
    private com.toedter.calendar.JDateChooser tanggalSelesaiJadwal;
    private com.toedter.calendar.JDateChooser tanggalSelesaiPembayaran;
    private com.toedter.calendar.JDateChooser tanggalSelesaiPembayaranDetail;
    private com.toedter.calendar.JDateChooser tanggalSelesaiReport;
    private javax.swing.JTable tbl_admin;
    private javax.swing.JTable tbl_layanan;
    private javax.swing.JTable tbl_sales;
    private javax.swing.JTextField txtDeskripsiLayanan;
    private javax.swing.JTextField txtEmailAdmin;
    private javax.swing.JTextField txtEmailSales;
    private javax.swing.JTextField txtHargaLayanan;
    private javax.swing.JTextField txtJumlahBayarPembayaranDetail;
    private javax.swing.JTextField txtKeteranganPembayaranDetail;
    private javax.swing.JTextField txtLayananPembayaranDetail;
    private javax.swing.JTextField txtNamaAdmin;
    private javax.swing.JTextField txtNamaLayanan;
    private javax.swing.JTextField txtNamaSales;
    private javax.swing.JPasswordField txtPasswordAdmin;
    private javax.swing.JPasswordField txtPasswordSales;
    private javax.swing.JTextField txtPelangganPembayaranDetail;
    private javax.swing.JTextField txtTotalHargaPembayaranDetail;
    // End of variables declaration//GEN-END:variables
}
