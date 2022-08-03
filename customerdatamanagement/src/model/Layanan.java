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
public class Layanan extends BaseModel{
    private int idLayanan;
    private String nama;
    private String deskripsi;
    private int harga;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.idLayanan = rs.getInt("id_layanan");
        this.nama = rs.getString("nama");
        this.deskripsi = rs.getString("deskripsi");
        this.harga = rs.getInt("harga");
    }
    
    public int getIdLayanan(){ return this.idLayanan; };
    public String getNama() { return this.nama; };
    public String getDeskripsi() { return this.deskripsi; };
    public int getHarga() { return this.harga; };

    public void setIdLayanan(int idLayanan){ this.idLayanan = idLayanan; }
    public void setNama(String nama){ this.nama = nama; }
    public void setDeskripsi(String deskripsi){ this.deskripsi = deskripsi; }
    public void setHarga(int harga){ this.harga = harga; }


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (this.idLayanan != 0) { result.put("id_layanan", this.idLayanan); }
        if (this.nama != null && !this.nama.equals("")) { result.put("nama", this.nama); }
        if (this.deskripsi != null && !this.deskripsi.equals("")) { result.put("deskripsi", this.deskripsi); }
        if (this.harga != 0) { result.put("harga", this.harga); }
        return result;
    }

}
