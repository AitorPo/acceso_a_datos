package com.svalero.DAO;

import com.svalero.domain.Parque;
import com.svalero.domain.Tarea;
import util.Constants;
import util.R;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TareaDAO {
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

    public void insertTarea(Tarea tarea) throws SQLException{
        PreparedStatement ps = null;
        ps = conn.prepareStatement(Constants.ConstantsTarea.INSERT);
        ps.setInt(1, tarea.getIdParque());
        ps.setString(2, tarea.getNombre());
        ps.setString(3, tarea.getDescripcion());
        ps.executeUpdate();
    }

    public void deleteTarea(Tarea tarea) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsTarea.DELETE);
        ps.setString(1, tarea.getNombre());
        ps.executeUpdate();
    }

    public void updateTarea(Tarea originalTarea, Tarea newTarea) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsTarea.UPDATE);
        ps.setString(1, newTarea.getNombre());
        ps.setString(2, newTarea.getDescripcion());
        ps.setInt(3, originalTarea.getIdParque());
        ps.executeUpdate();
    }

    public boolean existsTarea(Tarea tarea) throws SQLException{
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsParque.EXISTS);
        ps.setString(1, tarea.getNombre());
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public List<Tarea> getAllTarea() throws SQLException{
        List<Tarea> tareas = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(Constants.ConstantsTarea.SELECT_ALL);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Tarea tarea = new Tarea();
            tarea.setIdTarea(rs.getInt(1));
            tarea.setIdParque(rs.getInt(2));
            tarea.setNombre(rs.getString(3));
            tarea.setDescripcion(rs.getString(4));
            tareas.add(tarea);
            //System.out.println(tarea.toString());
        }
        return tareas;
    }
}
