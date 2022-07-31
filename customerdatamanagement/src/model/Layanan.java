/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        this.nama = rs.getString("name");
        this.deskripsi = rs.getString("deskripsi");
        this.harga = rs.getInt("harga");
    }
    
    public int getIdLayanan(){
        return this.idLayanan;
    };
    public String getNama() {
        return this.nama;
    };
    public String getDeskripsi() {
        return this.deskripsi;
    };
    public int getHarga() {
        return this.harga;
    };

}
