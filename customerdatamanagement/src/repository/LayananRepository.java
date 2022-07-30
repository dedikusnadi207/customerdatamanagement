package repository;

import java.sql.Types;
import java.util.Map;
import model.Layanan;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class LayananRepository extends Repository<Layanan> {

    @Override
    public String tableName() {
        return "layanan";
    }

    @Override
    public Map<String, Integer> fields() {
        return Map.of(
            "id_layanan", Types.INTEGER,
            "nama", Types.VARCHAR,
            "deskripsi", Types.VARCHAR,
            "harga", Types.INTEGER
        );
    }

    @Override
    public Class<Layanan> model() {
        return Layanan.class;
    }
}
