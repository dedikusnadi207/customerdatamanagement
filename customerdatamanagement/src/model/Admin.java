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
public class Admin extends BaseModel {
    private int idAdmin;
    private String nama;
    private String email;
    private String password;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.idAdmin = rs.getInt("id_admin");
        this.nama = rs.getString("nama");
        this.email = rs.getString("email");
        this.password = rs.getString("password");
    }
    public int getIdAdmin(){ return this.idAdmin; }
    public String getNama(){ return this.nama; }
    public String getEmail(){ return this.email; }
    public String getPassword(){ return this.password; }
    
    public void setIdAdmin(int idAdmin){ this.idAdmin = idAdmin; }
    public void setNama(String nama){ this.nama = nama; }
    public void setEmail(String email){ this.email = email; }
    public void setPassword(String password){ this.password = password; }

    @Override
    public Map<String, Object> toMap() {
        
        Map<String, Object> result = new HashMap<>();
        if (this.idAdmin != 0) { result.put("id_admin", this.idAdmin); }
        if (this.nama != null && !this.nama.equals("")) { result.put("nama", this.nama); }
        if (this.email != null && !this.email.equals("")) { result.put("email", this.email); }
        if (this.password != null && !this.password.equals("")) { result.put("password", this.password); }
        return result;
    }
}
