package com.svalero;

import com.svalero.DAO.CiudadDAO;
import com.svalero.DAO.ParqueDAO;
import com.svalero.domain.Ciudad;
import com.svalero.domain.Parque;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.AlertUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class AppController implements Initializable {

    //Elementos de la GUI
    public TextField tfParque;
    public TextField tfTarea;
    public TextArea taDescripcion;
    public ComboBox<Ciudad> cbCiudad;
    public Label lblStatus;
    public ListView<Parque> lvParques;
    public Button btnAddParque, btnAddTarea, btnModifyParque, btnModifyTarea, btnDeleteTarea;

    public AppController() {
    }

    private enum Accion {
        MODIFY
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
            loadListView();
            loadComboBox();
        } catch (IOException ioe) {
            AlertUtils.showError("Error al cargar la configuración de la BD");
        } catch (ClassNotFoundException cnfe) {
            AlertUtils.showError("Error al iniciar la aplicación. Algo salió mal");
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al conectar con la BD");
        }
    }

    private void loadComboBox()  {
        List<Ciudad> ciudades = null;
        try {
            ciudades = ciudadDAO.getAllCiudad();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }
        cbCiudad.setItems(FXCollections.observableList(ciudades));
    }

    private void loadListView(){
        lvParques.getItems().clear();
        List<Parque> parques = null;
        try {
            parques = parqueDAO.getAllParque();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }
        lvParques.setItems(FXCollections.observableList(parques));
    }

    private void setLabelStatus(String message){
        lblStatus.setText(message);
    }

    private void cleanFields(){
        tfParque.clear();
        tfTarea.clear();
        //Reseteamos la elecciópn del ComboBox
        cbCiudad.valueProperty().set(null);
    }

    @FXML
    public void addParque(ActionEvent event) {
        String nombre = tfParque.getText();
        Ciudad ciudad  = cbCiudad.getSelectionModel().getSelectedItem();
        Parque parque;
        int id = 0;
        try {
            id = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
            //System.out.println(id);
            parque = new Parque(nombre, id);
            parqueDAO.insertParque(parque);
        } catch (SQLException sqle){
            AlertUtils.showError("Error al añadir el parque");
        }
        cleanFields();
        loadListView();
        setLabelStatus("Parque añadido correctamente");
    }

    @FXML
    public void modifyParque(ActionEvent event) {
        parqueSeleccionado = lvParques.getSelectionModel().getSelectedItem();
        String nombre = null;
        Parque parque;
        int id = 0;
        //System.out.println(parque.toString());
        try {
            nombre = tfParque.getText();
            id = parqueSeleccionado.getIdParque();
            parque = new Parque(nombre, id);
            parqueDAO.updateParque(parqueSeleccionado, parque);
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al actualizar el parque");
        }
        cleanFields();
        loadListView();
        setLabelStatus("Parque actualizado correctamente");
    }

    @FXML
    public void deleteParque(ActionEvent event){
        Parque parque = lvParques.getSelectionModel().getSelectedItem();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar parque");
        confirm.setContentText("¿Realizar la acción?");
        Optional<ButtonType> response = confirm.showAndWait();
        if(response.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        try {
            parqueDAO.deleteParque(parque);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        cleanFields();
        loadListView();
        setLabelStatus("Parque eliminado correctamente");
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

    /**
     * Se cargan el nombre y la ciudad de un parque seleccionado de la ListView
     * en el TextField y el ComboBox respectivamente
     * @param event
     */
    @FXML
    public void getParqueData(Event event){
        parqueSeleccionado = lvParques.getSelectionModel().getSelectedItem();
        loadParque(parqueSeleccionado);
    }

    /**
     * Obtenemos el nombre y la ciudad de un parque dado un objeto Parque
     * @param parque
     */
    public void loadParque(Parque parque){
        String nombreCiudad = null;
        ciudadSeleccionada = new Ciudad();
        try {
            nombreCiudad = parqueDAO.getCiudadParque(parque);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tfParque.setText(parque.getNombreParque());
        ciudadSeleccionada.setNombreCiudad(nombreCiudad);
        cbCiudad.setValue(ciudadSeleccionada);
        //ciudadDAO.getIdCiudad()
    }



}
