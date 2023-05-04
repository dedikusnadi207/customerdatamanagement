package repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;
import model.Pembayaran;
import utils.MapCustom;
import Koneksi.Koneksi;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
public class PembayaranRepository extends Repository<Pembayaran> {

    @Override
    public String tableName() {
        return "pembayaran";
    }

    @Override
    public Map<String, Integer> fields() {
        return MapCustom.of(
            "id_pembayaran", Types.INTEGER,
            "id_transaksi", Types.INTEGER,
            "jumlah_pembayaran", Types.INTEGER,
            "tgl_bayar", Types.VARCHAR,
            "keterangan", Types.VARCHAR,
            "id_admin", Types.INTEGER
        );
    }

    @Override
    public Class<Pembayaran> model() {
        return Pembayaran.class;
    }

    @Override
    public String[] tableHeaders() {
        return new String[]{"ID", "Tanggal", "Jumlah", "Keterangan", "Admin"};
    }

    @Override
    public Object[] renderItem(Pembayaran item) {
        return new Object[]{
            item.getIdPembayaran(),
            item.getTglBayar(),
            item.getJumlahPembayaran(),
            item.getKeterangan(),
            item.getAdmin().getNama()
        };
    }
    
    public ArrayList<Pembayaran> all(int idTransaksi) throws Exception {
         String sql = "SELECT * FROM pembayaran "
                 + "INNER JOIN admin on admin.id_admin = pembayaran.id_admin "
                 + "WHERE id_transaksi = ?";
        PreparedStatement st = Koneksi.koneksidb().prepareStatement(sql);
        st.setInt(1, idTransaksi);
        ResultSet rs = st.executeQuery();

        return super.generateResult(rs);
    }
}
