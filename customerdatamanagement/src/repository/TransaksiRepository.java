package repository;

import Koneksi.Koneksi;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import model.Transaksi;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class TransaksiRepository extends Repository<Transaksi> {

    @Override
    public String tableName() {
        return "transaksi";
    }

    @Override
    public Map<String, Integer> fields() {
        return Map.of(
            "id_transaksi", Types.INTEGER,
            "tanggal_mulai", Types.VARCHAR,
            "tanggal_selesai", Types.VARCHAR,
            "id_pelanggan", Types.INTEGER,
            "id_layanan", Types.INTEGER,
            "id_sales", Types.INTEGER,
            "total_harga", Types.INTEGER,
            "created_at", Types.VARCHAR,
            "updated_at", Types.VARCHAR
        );
    }

    @Override
    public Class<Transaksi> model() {
        return Transaksi.class;
    }

    @Override
    public String[] tableHeaders() {
        return new String[]{"ID", "Tanggal Mulai", "Tanggal Selesai", "Nama Instansi", "Nama PIC", "Layanan", "Harga"};
    }

    @Override
    public Object[] renderItem(Transaksi item) {
        return new Object[]{item.getIdTransaksi(), item.getTanggalMulai(), item.getTanggalSelesai(), item.getPelanggan().getNamaInstansi(), item.getPelanggan().getNamaPic(), item.getLayanan().getNama(), item.getTotalHarga()};
    }
    
    @Override
    public ArrayList<Transaksi> all() throws Exception {
        String sql = "SELECT * FROM transaksi "
                + "INNER JOIN pelanggan ON pelanggan.id_pelanggan = transaksi.id_pelanggan "
                + "INNER JOIN layanan on layanan.id_layanan = transaksi.id_layanan "
                + "INNER JOIN sales on sales.id_sales = transaksi.id_sales";
        Statement st = Koneksi.koneksidb().createStatement();
        ResultSet rs = st.executeQuery(sql);
        ArrayList<Transaksi> result = new ArrayList<>();
        while (rs.next()) {
            Transaksi obj = new Transaksi();
            obj.fillData(rs);
            result.add(obj);
        }
        return result;
    }
    
    @Override
    public int save(Transaksi model) throws SQLException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        model.setCreatedAt(simpleDateFormat.format(new Date()));
        setValues(model.toMap());
        return save();
    }
    
    @Override
    public int update(Map<String, Object> values, Map<String, Object> conds) throws SQLException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put("updated_at", simpleDateFormat.format(new Date()));
        setValues(values);
        setConds(conds);
        return update();
    }
}
