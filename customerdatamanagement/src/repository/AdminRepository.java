package repository;

import java.util.Map;
import model.Admin;
import java.sql.Types;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class AdminRepository extends Repository<Admin>{

    @Override
    public Class<Admin> model() {
        return Admin.class;
    }

    @Override
    public String tableName() {
        return "admin";
    }

    @Override
    public Map<String, Integer> fields() {
        return Map.of(
                "id_admin", Types.INTEGER,
                "nama", Types.VARCHAR,
                "email", Types.VARCHAR,
                "password", Types.VARCHAR
        );
    }
    
}
