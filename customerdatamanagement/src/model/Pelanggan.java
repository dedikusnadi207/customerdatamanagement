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
public class Pelanggan extends BaseModel{
    private int idPelanggan;
    private String nik;
    private String namaPic;
    private String namaInstansi;
    private String noTlp;
    private String email;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.idPelanggan = rs.getInt("id_pelanggan");
        this.nik = rs.getString("nik");
        this.namaPic = rs.getString("nama_pic");
        this.namaInstansi = rs.getString("nama_instansi");
        this.noTlp = rs.getString("no_tlp");
        this.email = rs.getString("email");
    }
    
    public int getIdPelanggan(){ return this.idPelanggan; };
    public String getNik(){ return this.nik; };
    public String getNamaPic(){ return this.namaPic; };
    public String getNamaInstansi(){ return this.namaInstansi; };
    public String getNoTlp(){ return this.noTlp; };
    public String getEmail(){ return this.email; };

    public void setIdPelanggan(int idPelanggan){ this.idPelanggan = idPelanggan; }
    public void setNik(String nik){ this.nik = nik; }
    public void setNamaPic(String namaPic){ this.namaPic = namaPic; }
    public void setNamaInstansi(String namaInstansi){ this.namaInstansi = namaInstansi; }
    public void setNoTlp(String noTlp){ this.noTlp = noTlp; }
    public void setEmail(String email){ this.email = email; }


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (this.idPelanggan != 0) { result.put("id_pelanggan", this.idPelanggan); }
        if (this.nik != null && !this.nik.equals("")) { result.put("nik", this.nik); }
        if (this.namaPic != null && !this.namaPic.equals("")) { result.put("nama_pic", this.namaPic); }
        if (this.namaInstansi != null && !this.namaInstansi.equals("")) { result.put("nama_instansi", this.namaInstansi); }
        if (this.noTlp != null && !this.noTlp.equals("")) { result.put("no_tlp", this.noTlp); }
        if (this.email != null && !this.email.equals("")) { result.put("email", this.email); }
        return result;
    }

}
