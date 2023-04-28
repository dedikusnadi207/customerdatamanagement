package repository;

import Koneksi.Koneksi;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import model.JadwalSales;
import utils.MapCustom;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class JadwalSalesRepository extends Repository<JadwalSales> {
    private final boolean isAdmin;

    public JadwalSalesRepository(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    @Override
    public String tableName() {
        return "jadwal_sales";
    }

    @Override
    public Map<String, Integer> fields() {
        return MapCustom.of(
            "id_jadwal", Types.INTEGER,
            "id_sales", Types.INTEGER,
            "id_pelanggan", Types.INTEGER,
            "kegiatan", Types.VARCHAR,
            "waktu", Types.VARCHAR
        );
    }

    @Override
    public Class<JadwalSales> model() {
        return JadwalSales.class;
    }

    @Override
    public String[] tableHeaders() {
        if (isAdmin) {
            return new String[]{"ID", "Sales", "Pelanggan", "Kegiatan", "Waktu"};
        }
        return new String[]{"ID", "Pelanggan", "Kegiatan", "Waktu"};
    }

    @Override
    public Object[] renderItem(JadwalSales item) {
        if (isAdmin) {
            return new Object[]{item.getIdJadwal(), item.getSales().getNama(), item.getPelanggan().getNamaPic()+" - "+item.getPelanggan().getNamaInstansi(), item.getKegiatan(), item.getWaktu()};
        }
        return new Object[]{item.getIdJadwal(), item.getPelanggan().getNamaPic()+" - "+item.getPelanggan().getNamaInstansi(), item.getKegiatan(), item.getWaktu()};
    }
    
    @Override
    public ArrayList<JadwalSales> all() throws Exception {
        String sql = "SELECT * FROM jadwal_sales "
                + "INNER JOIN pelanggan ON pelanggan.id_pelanggan = jadwal_sales.id_pelanggan "
                + "INNER JOIN sales on sales.id_sales = jadwal_sales.id_sales ";
        
        return super.all(sql);
    }
    
//    public ArrayList<JadwalSales> all(int salesId) throws Exception {
//        String sql = "SELECT * FROM jadwal_sales "
//                + "INNER JOIN pelanggan ON pelanggan.id_pelanggan = jadwal_sales.id_pelanggan "
//                + "INNER JOIN sales on sales.id_sales = jadwal_sales.id_sales ";
//        
//        return super.all(sql, MapCustom.of("jadwal_sales.id_sales", salesId));
//    }
    
    public ArrayList<JadwalSales> all(int salesId, String startTime, String endTime) throws Exception {
        String sql = "SELECT * FROM jadwal_sales "
                + "INNER JOIN pelanggan ON pelanggan.id_pelanggan = jadwal_sales.id_pelanggan "
                + "INNER JOIN sales on sales.id_sales = jadwal_sales.id_sales "
                + "WHERE jadwal_sales.id_sales = ? AND waktu >= ? AND waktu <= ?";
        PreparedStatement st = Koneksi.koneksidb().prepareStatement(sql);
        st.setInt(1, salesId);
        st.setString(2, startTime);
        st.setString(3, endTime);
        ResultSet rs = st.executeQuery();

        return super.generateResult(rs);
    }
    
    public ArrayList<JadwalSales> all(String startTime, String endTime) throws Exception {
        String sql = "SELECT * FROM jadwal_sales "
                + "INNER JOIN pelanggan ON pelanggan.id_pelanggan = jadwal_sales.id_pelanggan "
                + "INNER JOIN sales on sales.id_sales = jadwal_sales.id_sales "
                + "WHERE waktu >= ? AND waktu <= ?";
        PreparedStatement st = Koneksi.koneksidb().prepareStatement(sql);
        st.setString(1, startTime);
        st.setString(2, endTime);
        ResultSet rs = st.executeQuery();

        return super.generateResult(rs);
    }
}
