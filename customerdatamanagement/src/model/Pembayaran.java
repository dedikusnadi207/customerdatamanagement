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
public class Pembayaran extends BaseModel{
    private int idPembayaran;
    private int idTransaksi;
    private Transaksi transaksi;
    private int jumlahPembayaran;
    private String tglBayar;
    private String keterangan;
    private int idAdmin;
    private Admin admin;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.idPembayaran = rs.getInt("id_pembayaran");
        this.idTransaksi = rs.getInt("id_transaksi");
        this.jumlahPembayaran = rs.getInt("jumlah_pembayaran");
        this.tglBayar = rs.getString("tgl_bayar");
        this.keterangan = rs.getString("keterangan");
        this.idAdmin = rs.getInt("id_admin");
        if (isColumnExist(rs, "tanggal_mulai")) {
            Transaksi t = new Transaksi();
            t.fillData(rs);
            this.transaksi = t;
        }
        
        Admin a = new Admin();
        a.fillData(rs);
        this.admin = a;
    }
    
    public int getIdPembayaran(){ return this.idPembayaran; };
    public int getIdTransaksi(){ return this.idTransaksi; };
    public int getJumlahPembayaran(){ return this.jumlahPembayaran; };
    public String getTglBayar() { return this.tglBayar; };
    public String getKeterangan() { return this.keterangan; };
    public int getIdAdmin() { return this.idAdmin; };
    public Admin getAdmin() { return this.admin; };
    
    public void setIdPembayaran(int v){ this.idPembayaran = v; };
    public void setIdTransaksi(int v){ this.idTransaksi = v; };
    public void setJumlahPembayaran(int v){ this.jumlahPembayaran = v; };
    public void setTglBayar(String v) { this.tglBayar = v; };
    public void setKeterangan(String v) { this.keterangan = v; };
    public void setIdAdmin(int v) { this.idAdmin = v; };



    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (this.idPembayaran != 0) { result.put("id_pembayaran", this.idPembayaran); }
        if (this.idTransaksi != 0) { result.put("id_transaksi", this.idTransaksi); }
        if (this.jumlahPembayaran != 0) { result.put("jumlah_pembayaran", this.jumlahPembayaran); }
        if (this.tglBayar != null && !this.tglBayar.equals("")) { result.put("tgl_bayar", this.tglBayar); }
        if (this.keterangan != null && !this.keterangan.equals("")) { result.put("keterangan", this.keterangan); }
        if (this.idAdmin != 0) { result.put("id_admin", this.idAdmin); }
        return result;
    }

}
