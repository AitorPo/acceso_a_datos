package com.svalero.DAO;

import com.svalero.domain.Ciudad;
import util.Constants;
import util.R;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CiudadDAO {

    private Connection conn;

    public void connect() throws IOException, ClassNotFoundException, SQLException {
        Properties config = new Properties();
        config.load(R.getProperties("db.properties"));
        String host = config.getProperty("host");
        String port = config.getProperty("port");
        String dbName = config.getProperty("db_name");
        String dbUsername = config.getProperty("db_username");
        String dbPassword = config.getProperty("db_password");
        String dbDriver = config.getProperty("db_driver");
        String dbUrl = config.getProperty("db_url");

        Class.forName(dbDriver);
        conn = DriverManager.getConnection(dbUrl + host + ":" + port + "/" + dbName + "?serverTimezone=UTC",
                dbUsername, dbPassword);
    }

    public void disconnect() throws SQLException {
        conn.close();
    }

    public void insertCiudad(Ciudad ciudad) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsCiudad.INSERT);
        ps.setString(1, ciudad.getNombreCiudad());
        ps.setString(2, ciudad.getComunidadAutonoma());
        ps.executeUpdate();
    }

    public void deleteCiudad(Ciudad ciudad) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsCiudad.DELETE);
        ps.setString(1, ciudad.getNombreCiudad());
        ps.executeUpdate();
    }

    public void updateCiudad(Ciudad originalCiudad, Ciudad newCiudad) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsCiudad.UPDATE);
        ps.setString(1, newCiudad.getNombreCiudad());
        ps.setInt(2, originalCiudad.getIdCiudad());
        ps.executeUpdate();
    }

    public boolean existsCiudad(Ciudad ciudad) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsCiudad.EXISTS);
        ps.setString(1, ciudad.getNombreCiudad());
        ResultSet rs = ps.executeQuery();

        return rs.next();
    }

    public int getIdCiudad(String ciudad) throws SQLException{
        int idCiudad = 0;
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsCiudad.SELECT_ID_BY_NAME);
        ps.setString(1, ciudad);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            idCiudad = rs.getInt(1);
        }
        return idCiudad;
    }

    public List<Ciudad> getAllCiudad() throws SQLException {
        List<Ciudad> ciudades = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsCiudad.SELECT_NAME);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            Ciudad ciudad = new Ciudad();
            String nombre = rs.getString(1);
            ciudad.setNombreCiudad(nombre);
            System.out.println(nombre);
            ciudades.add(ciudad);
        }
        return ciudades;
    }
}


