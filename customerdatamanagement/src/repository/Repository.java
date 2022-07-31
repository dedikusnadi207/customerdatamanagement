package repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;
import Koneksi.Koneksi;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import model.BaseModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dedikusnadi
 */
abstract class Repository<T extends BaseModel> {
    public abstract Class<T> model();
    public abstract String tableName();
    public abstract Map<String, Integer> fields();
    
    private Map<String, Object> values;
    private Map<String, Object> conds;
    public void setValues(Map<String, Object> values){ this.values = values; }
    public void addValue(String column, Object value) { this.values.put(column, value); }
    public void setConds(Map<String, Object> conds){ this.conds = conds; }
    public void addConds(String column, Object value) { this.conds.put(column, value); }
    private String preparedFields() { return this.values.keySet().stream().collect(Collectors.joining("=?,", "","=?")); }
    private String preparedConds() { return this.conds.keySet().stream().collect(Collectors.joining("=? AND ", "","=?")); }
    
    private PreparedStatement setPreparedStatement(PreparedStatement ps, Map<String, Object> data) throws SQLException {
        int index = 1;
        for (String key : data.keySet()) {
            ps.setObject(index++, data.get(key), this.fields().get(key));
        }
        return ps;
    }
    
    public int save() throws SQLException {
        String sql = "INSERT INTO "+tableName()+" SET "+preparedFields();
        PreparedStatement ps = Koneksi.koneksidb().prepareStatement(sql);
        ps = this.setPreparedStatement(ps, values);
        return ps.executeUpdate();
    }
    
    public int save(Map<String, Object> values) throws SQLException {
        setValues(values);
        return save();
    }
    
    public int update() throws SQLException {
        String sql = "UPDATE "+tableName()+" SET "+preparedFields()+" WHERE "+preparedConds();
        PreparedStatement ps = Koneksi.koneksidb().prepareStatement(sql);
        Map<String, Object> data = values;
        data.putAll(conds);
        ps = this.setPreparedStatement(ps, data);
        return ps.executeUpdate();

    }
    
    public int update(Map<String, Object> values, Map<String, Object> conds) throws SQLException {
        setValues(values);
        setConds(conds);
        return update();
    }
    
    public int delete() throws SQLException {
        String sql = "DELETE FROM "+tableName()+" WHERE "+preparedConds();
        PreparedStatement ps = Koneksi.koneksidb().prepareStatement(sql);
        ps = this.setPreparedStatement(ps, conds);
        return ps.executeUpdate();
    }
    
    public int delete(Map<String, Object> conds) throws SQLException {
        setConds(conds);
        return delete();
    }
    
    public ArrayList<T> all() throws Exception {
        String sql = "SELECT * FROM "+tableName();
        Statement st = Koneksi.koneksidb().createStatement();
        ResultSet rs = st.executeQuery(sql);
        ArrayList<T> result = new ArrayList<>();
        while (rs.next()) {
            T obj = model().getDeclaredConstructor().newInstance();
            obj.fillData(rs);
            result.add(obj);
        }
        return result;
    }
    
    public ArrayList<T> all(Map<String, Object> conds) throws Exception {
        setConds(conds);
        String sql = "SELECT * FROM "+tableName()+" WHERE "+preparedConds();
        PreparedStatement ps = Koneksi.koneksidb().prepareStatement(sql);
        ps = this.setPreparedStatement(ps, conds);
        ResultSet rs = ps.executeQuery();
        ArrayList<T> result = new ArrayList<>();
        while (rs.next()) {
            T obj = model().getDeclaredConstructor().newInstance();
            obj.fillData(rs);
            result.add(obj);
        }
        return result;
    }
    
    public T first() throws Exception {
        String sql = "SELECT * FROM "+tableName();
        Statement st = Koneksi.koneksidb().createStatement();
        ResultSet rs = st.executeQuery(sql);
        if (rs.next()) {
            T obj = model().getDeclaredConstructor().newInstance();
            obj.fillData(rs);
            return obj;
        }
        return null;
    }
    public T first(Map<String, Object> conds) throws Exception {
        setConds(conds);
        String sql = "SELECT * FROM "+tableName()+" WHERE "+preparedConds();
        PreparedStatement ps = Koneksi.koneksidb().prepareStatement(sql);
        ps = this.setPreparedStatement(ps, conds);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            T obj = model().getDeclaredConstructor().newInstance();
            obj.fillData(rs);
            return obj;
        }
        return null;
    }
}