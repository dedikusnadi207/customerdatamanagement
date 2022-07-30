package Koneksi;

import java.sql.*;

public class Koneksi {
    private static Connection Koneksi;   
    public static Connection koneksidb(){
        if (Koneksi == null) {
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("koneksi berhasil;");
            } catch (ClassNotFoundException e){
                System.err.println("koneksi gagal " +e);
            }
            String url ="jdbc:mysql://localhost/customerdatamanagement";
            String user="root";
            String pass="root";
            try {
                Koneksi = DriverManager.getConnection(url,user,pass);
                System.out.println("koneksi berhasil");
            } catch (SQLException e) {
                System.err.println("koneksi gagal " +e);
            }
            
        }
        return Koneksi;
    }
}
