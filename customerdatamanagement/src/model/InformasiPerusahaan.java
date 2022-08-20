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
public class InformasiPerusahaan extends BaseModel{
    private int id;
    private String namaPerusahaan;
    private String alamat;
    private String noTlp;
    private String email;
    private String logo;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.namaPerusahaan = rs.getString("nama_perusahaan");
        this.alamat = rs.getString("alamat");
        this.noTlp = rs.getString("no_tlp");
        this.email = rs.getString("email");
        this.logo = rs.getString("logo");
    }
    
    public int getId(){ return this.id; };
    public String getNamaPerusahaan() { return this.namaPerusahaan; };
    public String getAlamat() { return this.alamat; };
    public String getNoTlp() { return this.noTlp; };
    public String getEmail() { return this.email; };
    public String getLogo() { return this.logo; };

    public void setId(int id){ this.id = id; };
    public void setNamaPerusahaan(String namaPerusahaan) { this.namaPerusahaan = namaPerusahaan; };
    public void setAlamat(String alamat) { this.alamat = alamat; };
    public void setNoTlp(String noTlp) { this.noTlp = noTlp; };
    public void setEmail(String email) { this.email = email; };
    public void setLogo(String logo) { this.logo = logo; };


    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        if (this.id != 0) { result.put("id", this.id); }
        if (this.namaPerusahaan != null && !this.namaPerusahaan.equals("")) { result.put("nama_perusahaan", this.namaPerusahaan); }
        if (this.alamat != null && !this.alamat.equals("")) { result.put("alamat", this.alamat); }
        if (this.noTlp != null && !this.noTlp.equals("")) { result.put("no_tlp", this.noTlp); }
        if (this.email != null && !this.email.equals("")) { result.put("email", this.email); }
        if (this.logo != null && !this.logo.equals("")) { result.put("logo", this.logo); }
        return result;
    }

}
