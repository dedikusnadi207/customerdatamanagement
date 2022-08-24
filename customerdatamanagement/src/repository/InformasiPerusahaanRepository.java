package repository;

import java.sql.Types;
import java.util.Map;
import model.InformasiPerusahaan;
import model.Layanan;
import utils.MapCustom;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class InformasiPerusahaanRepository extends Repository<InformasiPerusahaan> {

    @Override
    public String tableName() {
        return "informasi_perusahaan";
    }

    @Override
    public Map<String, Integer> fields() {
        return MapCustom.of(
            "id", Types.INTEGER,
            "nama_perusahaan", Types.VARCHAR,
            "alamat", Types.VARCHAR,
            "no_tlp", Types.VARCHAR,
            "email", Types.VARCHAR,
            "logo", Types.VARCHAR
        );
    }

    @Override
    public Class<InformasiPerusahaan> model() {
        return InformasiPerusahaan.class;
    }

    @Override
    public String[] tableHeaders() {
        return null;
    }

    @Override
    public Object[] renderItem(InformasiPerusahaan item) {
        return null;
    }
}
