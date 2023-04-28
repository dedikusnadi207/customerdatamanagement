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
public class JadwalSales extends BaseModel{
    private int idJadwal;
    private int idSales;
    private Sales sales;
    private int idPelanggan;
    private Pelanggan pelanggan;
    private String kegiatan;
    private String waktu;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.idJadwal = rs.getInt("id_jadwal");
        this.idSales = rs.getInt("id_sales");
        this.idPelanggan = rs.getInt("id_pelanggan");
        this.kegiatan = rs.getString("kegiatan");
        this.waktu = rs.getString("waktu");
        
        Sales s = new Sales();
        s.fillData(rs);
        this.sales = s;
        
        Pelanggan p = new Pelanggan();
        p.fillData(rs);
        this.pelanggan = p;
    }
    
    public int getIdJadwal(){ return this.idJadwal; };
    public int getIdSales(){ return this.idSales; };
    public Sales getSales(){ return this.sales; };
    public int getIdPelanggan(){ return this.idPelanggan; };
    public Pelanggan getPelanggan(){ return this.pelanggan; };
    public String getKegiatan(){ return this.kegiatan; };
    public String getWaktu(){ return this.waktu; };

    public void setIdJadwal(int idJadwal){ this.idJadwal = idJadwal; }
    public void setIdSales(int idSales){ this.idSales = idSales; }
    public void setIdPelanggan(int idPelanggan){ this.idPelanggan = idPelanggan; }
    public void setKegiatan(String kegiatan){ this.kegiatan = kegiatan; }
    public void setWaktu(String waktu){ this.waktu = waktu; }


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (this.idJadwal != 0) { result.put("id_jadwal", this.idJadwal); }
        if (this.idSales != 0) { result.put("id_sales", this.idSales); }
        if (this.idPelanggan != 0) { result.put("id_pelanggan", this.idPelanggan); }
        if (this.kegiatan != null && !this.kegiatan.equals("")) { result.put("kegiatan", this.kegiatan); }
        if (this.waktu != null && !this.waktu.equals("")) { result.put("waktu", this.waktu); }
        return result;
    }

}
