package repository;

import java.sql.Types;
import java.util.Map;
import model.Sales;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class SalesRepository extends Repository<Sales> {

    @Override
    public String tableName() {
        return "sales";
    }

    @Override
    public Map<String, Integer> fields() {
        return Map.of(
            "id_sales", Types.INTEGER,
            "nama", Types.VARCHAR,
            "email", Types.VARCHAR,
            "password", Types.VARCHAR
        );
    }

    @Override
    public Class model() {
        return Sales.class;
    }

    @Override
    public String[] tableHeaders() {
        return new String[]{"ID", "Nama", "Email"};
    }

    @Override
    public Object[] renderItem(Sales item) {
        return new Object[]{item.getIdSales(), item.getNama(), item.getEmail()};
    }
    
}
