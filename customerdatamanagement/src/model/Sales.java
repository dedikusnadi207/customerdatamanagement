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
public class Sales extends BaseModel {
    private int idSales;
    private String nama;
    private String email;
    private String password;

    @Override
    public void fillData(ResultSet rs) throws SQLException {
        this.idSales = rs.getInt("id_sales");
        this.nama = rs.getString("nama");
        this.email = rs.getString("email");
        this.password = rs.getString("password");
    }
    
    public int getIdSales() {
        return this.idSales;
    }
    public String getNama() {
        return this.nama;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
}
