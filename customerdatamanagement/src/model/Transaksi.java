/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 package model;

 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 /**
  *
  * @author dedikusnadi
  */
 public class Transaksi extends BaseModel{
     private int idTransaksi;
     private String tanggalMulai;
     private String tanggalSelesai;
     private int idPelanggan;
     private Pelanggan pelanggan;
     private int idLayanan;
     private Layanan layanan;
     private int idSales;
     private Sales sales;
     private int totalHarga;
     private String createdAt;
     private String updatedAt;
 
     @Override
     public void fillData(ResultSet rs) throws SQLException {
        this.idTransaksi = rs.getInt("id_transaksi");
        this.tanggalMulai = rs.getString("tanggal_mulai");
        this.tanggalSelesai = rs.getString("tanggal_selesai");
        this.idPelanggan = rs.getInt("id_pelanggan");
        this.idLayanan = rs.getInt("id_layanan");
        this.idSales = rs.getInt("id_sales");
        this.totalHarga = rs.getInt("total_harga");
        this.createdAt = rs.getString("created_at");
        this.updatedAt = rs.getString("updated_at");
        
        Pelanggan p = new Pelanggan();
        p.fillData(rs);
        this.pelanggan = p;
        
        Layanan l = new Layanan();
        l.fillData(rs);
        this.layanan = l;
        
        Sales s = new Sales();
        s.fillData(rs);
        this.sales = s;
     }
     
     public int getIdTransaksi(){ return this.idTransaksi; };
     public String getTanggalMulai(){ return this.tanggalMulai; };
     public String getTanggalSelesai(){ return this.tanggalSelesai; };
     public int getIdPelanggan(){ return this.idPelanggan; };
     public Pelanggan getPelanggan(){ return this.pelanggan; };
     public int getIdLayanan(){ return this.idLayanan; };
     public Layanan getLayanan(){ return this.layanan; };
     public int getIdSales(){ return this.idSales; };
     public Sales getSales(){ return this.sales; };
     public int getTotalHarga(){ return this.totalHarga; };
     public String getCreatedAt(){ return this.createdAt; };
     public String getUpdatedAt(){ return this.updatedAt; };
 
     public void setIdTransaksi(int idTransaksi){ this.idTransaksi = idTransaksi; }
     public void setTanggalMulai(String tanggalMulai){ this.tanggalMulai = tanggalMulai; }
     public void setTanggalSelesai(String tanggalSelesai){ this.tanggalSelesai = tanggalSelesai; }
     public void setIdPelanggan(int idPelanggan){ this.idPelanggan = idPelanggan; }
     public void setIdLayanan(int idLayanan){ this.idLayanan = idLayanan; }
     public void setIdSales(int idSales){ this.idSales = idSales; }
     public void setTotalHarga(int totalHarga){ this.totalHarga = totalHarga; }
     public void setCreatedAt(String createdAt){ this.createdAt = createdAt; }
     public void setUpdatedAt(String updatedAt){ this.updatedAt = updatedAt; }
 
     @Override
     public Map<String, Object> toMap() {
         Map<String, Object> result = new HashMap<>();
         if (this.idTransaksi != 0) { result.put("id_transaksi", this.idTransaksi); }
         if (this.tanggalMulai != null && !this.tanggalMulai.equals("")) { result.put("tanggal_mulai", this.tanggalMulai); }
         if (this.tanggalSelesai != null && !this.tanggalSelesai.equals("")) { result.put("tanggal_selesai", this.tanggalSelesai); }
         if (this.idPelanggan != 0) { result.put("id_pelanggan", this.idPelanggan); }
         if (this.idLayanan != 0) { result.put("id_layanan", this.idLayanan); }
         if (this.idSales != 0) { result.put("id_sales", this.idSales); }
         if (this.totalHarga != 0) { result.put("total_harga", this.totalHarga); }
         if (this.createdAt != null && !this.createdAt.equals("")) { result.put("created_at", this.createdAt); }
         if (this.updatedAt != null && !this.updatedAt.equals("")) { result.put("updated_at", this.updatedAt); }
         return result;
     }
 
 }
 