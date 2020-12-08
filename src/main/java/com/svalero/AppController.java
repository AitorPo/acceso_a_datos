package com.svalero;

import com.svalero.DAO.CiudadDAO;
import com.svalero.DAO.ParqueDAO;
import com.svalero.domain.Ciudad;
import com.svalero.domain.Parque;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.AlertUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    //Elementos de la GUI
    public TextField tfParque;
    public TextField tfTarea;
    public TextArea taDescripcion;
    public ComboBox<String> cbCiudad;
    public Label lblStatus;
    public ListView lvParques;
    public Button btnAddParque, btnAddTarea, btnModifyParque, btnModifyTarea, btnDeleteTarea;

    public AppController() {
    }

    private enum Accion {
        NEW, MODIFY
    }
    private Accion accion;
    private ParqueDAO parqueDAO;
    private CiudadDAO ciudadDAO;
    private Parque parqueSeleccionado;
    private Ciudad ciudadSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parqueDAO = new ParqueDAO();
        ciudadDAO = new CiudadDAO();
        try {
            ciudadDAO.connect();
            parqueDAO.connect();
            loadComboBox();
        } catch (IOException ioe) {
            AlertUtils.showError("Error al cargar la configuración de la BD");
        } catch (ClassNotFoundException cnfe) {
            AlertUtils.showError("Error al iniciar la aplicación. Algo salió mal");
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al conectar con la BD");
        }

    }

    /*public List<Ciudad> getAllCiudades() throws SQLException {
        CiudadDAO ciudadDAO = new CiudadDAO();
        return ciudadDAO.getAllCiudad();
    }*/
    public void loadComboBox() throws SQLException {
        String[] ciudades = new String[]{"Valencia", "Barcelona", "Madrid"};

        cbCiudad.setItems(FXCollections.observableArrayList(ciudades));

    }

    @FXML
    public void addParque(ActionEvent event) throws SQLException {
        String nombre = tfParque.getText();
        String ciudad = cbCiudad.getSelectionModel().getSelectedItem();

        int id = ciudadDAO.getIdCiudad(ciudad);

        System.out.println(id);
        Parque parque = new Parque(nombre, id);
        parqueDAO.insertParque(parque);

    }

    @FXML
    public void modifyParque(ActionEvent event){

    }

    @FXML
    public void addTarea(ActionEvent event){

    }

    @FXML
    public void modifyTarea(ActionEvent event){

    }

    @FXML
    public void deleteTarea(ActionEvent event){

    }



}
