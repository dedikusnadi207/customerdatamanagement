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
}
