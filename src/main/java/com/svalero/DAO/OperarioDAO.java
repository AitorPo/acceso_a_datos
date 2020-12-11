package com.svalero.DAO;

import com.svalero.domain.Operario;
import com.svalero.domain.Tarea;
import util.Constants;
import util.R;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OperarioDAO {
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

    public void insertOperario(Operario operario) throws SQLException{
        PreparedStatement ps = null;
        ps = conn.prepareStatement(Constants.ConstantsOperario.INSERT);
        ps.setString(1, operario.getNombre());
        ps.setString(2, operario.getPassword());
        ps.executeUpdate();
    }

    public void deleteOperario(Operario operario) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsOperario.DELETE);
        ps.setString(1, operario.getNombre());
        ps.executeUpdate();
    }

    public void updateOperario(Operario operarioOriginal, Operario newOperario) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsOperario.UPDATE);
        ps.setString(1, newOperario.getNombre());
        ps.setString(2, newOperario.getPassword());
        ps.setInt(3, operarioOriginal.getIdOperario());
        ps.executeUpdate();
    }

    public boolean existsOperario(Operario operario) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsOperario.EXISTS);
        ps.setString(1, operario.getNombre());
        ps.setString(2, operario.getPassword());
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public List<Operario> getAllOperario() throws SQLException{
        List<Operario> operarios = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM operario");
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Operario operario = new Operario();
            operario.setIdOperario(rs.getInt(1));
            operario.setNombre(rs.getString(2));
            operario.setPassword(rs.getString(3));
            operarios.add(operario);
            //System.out.println(operario.toString());
        }
        return operarios;
    }
}
