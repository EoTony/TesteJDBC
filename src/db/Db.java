package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Db {
    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null) {
                Properties prop = loadproperties();
                String url = prop.getProperty("db.url");
                String user = prop.getProperty("db.usuario");
                String password = prop.getProperty("db.senha");
                conn = DriverManager.getConnection(url, user, password);
            }
            System.out.println("Conexão concluida com sucesso!");
            return conn;
        }
        catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
        System.out.println("conexão encerrada com sucesso!");
    }

    private static Properties loadproperties(){
        try(FileInputStream fis = new FileInputStream("db.properties")){
            Properties prop = new Properties();
            prop.load(fis);
            return prop;
        }
        catch(IOException e){
            throw new DbException(e.getMessage());
        }
    }
    public static void closeStatement(Statement st){
        try{
            if(st != null){
                st.close();;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }
    public static void closeResultSet(ResultSet rs){
        try{
            if(rs != null){
                rs.close();
            }
        }
        catch(SQLException e){
            throw new DbException(e.getMessage());
        }
    }
}

