package repository;

import java.sql.Types;
import java.util.Map;
import model.Pelanggan;
import utils.MapCustom;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class PelangganRepository extends Repository<Pelanggan> {

    @Override
    public String tableName() {
        return "pelanggan";
    }

    @Override
    public Map<String, Integer> fields() {
        return MapCustom.of(
            "id_pelanggan", Types.INTEGER,
            "nik", Types.VARCHAR,
            "nama_pic", Types.VARCHAR,
            "nama_instansi", Types.VARCHAR,
            "no_tlp", Types.VARCHAR,
            "email", Types.VARCHAR
        );
    }

    @Override
    public Class<Pelanggan> model() {
        return Pelanggan.class;
    }

    @Override
    public String[] tableHeaders() {
        return new String[]{"ID", "NIK", "Nama PIC", "Nama Instansi", "No Telepon", "Email"};
    }

    @Override
    public Object[] renderItem(Pelanggan item) {
        return new Object[]{item.getIdPelanggan(), item.getNik(), item.getNamaPic(), item.getNamaInstansi(), item.getNoTlp(), item.getEmail()};
    }
}
