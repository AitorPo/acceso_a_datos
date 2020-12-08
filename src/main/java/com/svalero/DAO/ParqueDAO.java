package com.svalero.DAO;

import com.svalero.domain.Ciudad;
import com.svalero.domain.Parque;
import util.Constants;
import util.R;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParqueDAO {
    private Connection conn;

    public void connect() throws IOException, ClassNotFoundException, SQLException {
        Properties config = new Properties();
        config.load(R.getProperties("db.properties"));
        //Recogemos los datos del fichero properties
        String host = config.getProperty("host");
        String port = config.getProperty("port");
        String dbName = config.getProperty("db_name");
        String dbUsername = config.getProperty("db_username");
        String dbPassword = config.getProperty("db_password");
        String dbDriver = config.getProperty("db_driver");
        String dbUrl = config.getProperty("db_url");
        String timezone = config.getProperty("timezone");

        Class.forName(dbDriver);
        conn = DriverManager.getConnection(dbUrl + host + ":" + port + "/" + dbName + timezone,
                dbUsername, dbPassword);
    }

    public void disconnect() throws SQLException {
        conn.close();
    }

    public void insertParque(Parque parque) throws SQLException{
        PreparedStatement ps = null;

        ps = conn.prepareStatement(Constants.ConstantsParque.INSERT);
        ps.setString(1, parque.getNombreParque());
        ps.setInt(2, parque.getIdCiudad());

        ps.executeUpdate();
    }

    public void deleteParque(Parque parque) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsParque.DELETE);
        ps.executeUpdate();
    }

    public void updateParque(Parque parqueOriginal, Parque parqueNuevo) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsParque.UPDATE);
        ps.setString(1, parqueNuevo.getNombreParque());
        ps.setInt(2, parqueOriginal.getIdCiudad());
        ps.executeUpdate();
    }

    public boolean existsParque(Parque parque) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsParque.EXISTS);
        ps.setString(1, parque.getNombreParque());
        ResultSet rs = ps.executeQuery();

        return rs.next();
    }

    public List<Parque> getAllParque() throws SQLException {
        List<Parque> parques = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsParque.SELECT_NAME);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            Parque parque = new Parque();
            String nombre = rs.getString(1);
            parque.setNombreParque(nombre);
            //System.out.println(nombre);
            parques.add(parque);
        }
        return parques;
    }
}
