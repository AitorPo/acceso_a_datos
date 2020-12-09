package com.svalero;

import com.svalero.DAO.CiudadDAO;
import com.svalero.DAO.ParqueDAO;
import com.svalero.DAO.TareaDAO;
import com.svalero.domain.Ciudad;
import com.svalero.domain.Parque;
import com.svalero.domain.Tarea;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.AlertUtils;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Field;
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
    public TableView<Tarea> tvTarea;
    public Button btnAddParque, btnAddTarea, btnModifyParque,
            btnModifyTarea, btnDeleteParque, btnDeleteTarea, btnNewTarea,
            btnNewParque, btnCancel, btnSave;

    public AppController() {
    }

    private enum Accion {
        NEW_PARQUE,MODIFY_PARQUE, DELETE_PARQUE,
        NEW_TAREA, MODIFY_TAREA
    }
    private Accion accion;
    private ParqueDAO parqueDAO;
    private CiudadDAO ciudadDAO;
    private TareaDAO tareaDAO;
    private Parque selectedParque;
    private Ciudad selectedCiudad;
    private Tarea selectedTarea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parqueDAO = new ParqueDAO();
        ciudadDAO = new CiudadDAO();
        tareaDAO = new TareaDAO();
        try {
            ciudadDAO.connect();
            parqueDAO.connect();
            tareaDAO.connect();
            loadListView();
            loadComboBox();
            loadTable();
            setTable();
           // editModeParque(false);
            //editModeTarea(false);
            btnCancel.setDisable(true);
        } catch (IOException ioe) {
            AlertUtils.showError("Error al cargar la configuración de la BD");
        } catch (ClassNotFoundException cnfe) {
            AlertUtils.showError("Error al iniciar la aplicación. Algo salió mal");
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al conectar con la BD");
        }
    }

    private void loadTable(){
        editModeParque(false);
        editModeTarea(false);
        tvTarea.getItems().clear();
        List<Tarea> tareas = null;
        try {
            tareas = tareaDAO.getAllTarea();
            tvTarea.setItems(FXCollections.observableArrayList(tareas));
        } catch (SQLException sqle){
            AlertUtils.showError("Error al cargar los datos en la tabla");
        }
    }

    private void setTable(){
        Field[] fields = Tarea.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("id"))
                continue;
            TableColumn<Tarea, String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvTarea.getColumns().add(column);
        }
        tvTarea.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadComboBox()  {
        editModeParque(false);
        editModeTarea(false);
        List<Ciudad> ciudades = null;
        try {
            ciudades = ciudadDAO.getAllCiudad();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }
        cbCiudad.setItems(FXCollections.observableList(ciudades));
    }

    private void loadListView(){
        editModeParque(false);
        editModeTarea(false);
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
        taDescripcion.clear();
        //Reseteamos la elecciópn del ComboBox
        cbCiudad.valueProperty().set(null);
    }

    @FXML
    public void newParque(ActionEvent event){
        cleanFields();
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Activar modo editable");
        conf.setContentText("¿Deseas activar el modo de edición?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        editModeParque(true);
        editModeTarea(false);
        btnNewTarea.setDisable(true);
        tfParque.setDisable(true);
        cbCiudad.setDisable(true);
    }

    @FXML
    public void onSave(ActionEvent event){
        String nombre = tfParque.getText();
        Ciudad ciudad  = cbCiudad.getSelectionModel().getSelectedItem();
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        try {
            int idCiudad = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
            //System.out.println(id);
            Parque parque = new Parque(nombre, idCiudad);
            switch (accion){
                case NEW_PARQUE:
                    parqueDAO.insertParque(parque);
                    setLabelStatus("Parque añadido correctamente");
                    break;
                case MODIFY_PARQUE:
                    parqueDAO.updateParque(selectedParque, parque);
                    setLabelStatus("Parque añadido correctamente");
                    break;
                case DELETE_PARQUE:
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
                    conf.setTitle("Eliminar parque");
                    conf.setContentText("¿Realizar la acción?");
                    Optional<ButtonType> res = conf.showAndWait();
                    if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
                    parqueDAO.deleteParque(selectedParque);
                    setLabelStatus("Parque eliminado correctamente");
                    break;
            }
        } catch (SQLException sqle){
            AlertUtils.showError("Error al manejar los datos del parque");
        }
        cleanFields();
        loadListView();
    }

    @FXML
    public void newTarea(ActionEvent event){
        cleanFields();
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Activar modo editable");
        conf.setContentText("¿Deseas activar el modo de edición?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        editModeTarea(true);
        editModeParque(false);
        btnNewParque.setDisable(true);
        accion = Accion.NEW_TAREA;
    }

    //Botón "Nuevo parque"
    @FXML
    public void addParque(ActionEvent event) {
        cleanFields();
        editModeParque(true);
        accion = Accion.NEW_PARQUE;
        btnModifyParque.setDisable(true);
        btnDeleteParque.setDisable(true);
        btnCancel.setDisable(false);
        /*String nombre = tfParque.getText();
        Ciudad ciudad  = cbCiudad.getSelectionModel().getSelectedItem();
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        try {
            int idCiudad = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
            //System.out.println(id);
            Parque parque = new Parque(nombre, idCiudad);
            switch (accion){
                case NEW_PARQUE:
                    parqueDAO.insertParque(parque);
                    setLabelStatus("Parque añadido correctamente");
                    break;
                case MODIFY_PARQUE:
                    parqueDAO.updateParque(selectedParque, parque);
                    break;
            }

        } catch (SQLException sqle){
            AlertUtils.showError("Error al manejar los datos del parque");
        }
        cleanFields();
        loadListView();*/
    }

    //Botón "Modificar parque"
    @FXML
    public void modifyParque(ActionEvent event) {
        editModeParque(true);
        accion = Accion.MODIFY_PARQUE;
        btnAddParque.setDisable(true);
        btnDeleteParque.setDisable(true);
        btnCancel.setDisable(false);
        /*
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        String nombre = null;
        Parque parque;
        int id = 0;
        //System.out.println(parque.toString());
        try {
            nombre = tfParque.getText();
            id = selectedParque.getIdCiudad();
            parque = new Parque(nombre, id);
            System.out.println(parque.toString());
            parqueDAO.updateParque(selectedParque, parque);
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al actualizar el parque");
        }
        cleanFields();
        loadListView();
        setLabelStatus("Parque actualizado correctamente");*/
    }

    @FXML
    public void deleteParque(ActionEvent event){
        editModeParque(true);
        //Parque parque = lvParques.getSelectionModel().getSelectedItem();
        AlertUtils.showAlert("Selecciona un parque de la lista para eliminarlo");
        accion = Accion.DELETE_PARQUE;
        btnModifyParque.setDisable(true);
        btnAddParque.setDisable(true);
        btnCancel.setDisable(false);
        /*
        try {
            parqueDAO.deleteParque(parque);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        cleanFields();
        loadListView();
        loadTable();
        setLabelStatus("Parque eliminado correctamente");*/
    }

    @FXML
    public void addTarea(ActionEvent event){
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        selectedTarea = tvTarea.getSelectionModel().getSelectedItem();
        int idParque = selectedTarea.getIdParque();
        String nombre = tfTarea.getText();
        String descripcion = taDescripcion.getText();
        if (tfParque.getText().isEmpty()){
            AlertUtils.showAlert("Selecciona un parque sobre el que añadir la tarea");
            return;
        }
        if (tfTarea.getText().isEmpty() || taDescripcion.getText().isEmpty()){
            AlertUtils.showAlert("Rellena los datos de la tarea");
            return;
        }
        Tarea tarea = new Tarea(idParque, nombre, descripcion);
        try{

            tareaDAO.insertTarea(tarea);
            setLabelStatus("Tarea añadida correctamente");
        } catch (SQLException sqle){
            AlertUtils.showError("Error al manejar los datos de la tarea");
        } catch (NullPointerException npe){
            AlertUtils.showError("Error al manejar los datos de la tarea");
        }
        cleanFields();
        loadListView();
        loadTable();
        btnCancel.setDisable(true);
    }

    @FXML
    public void modifyTarea(ActionEvent event){
        selectedTarea = tvTarea.getSelectionModel().getSelectedItem();
        String nombre = tfTarea.getText();
        String descripcion = taDescripcion.getText();
        int id = selectedTarea.getIdParque();

        Tarea tarea = new Tarea(id, nombre, descripcion);
        try {
            tareaDAO.updateTarea(selectedTarea, tarea);//nombre descrip where id_parque = ?
        } catch (SQLException throwables) {
            AlertUtils.showError("Error al actualizar los datos de la tarea");
        }
        cleanFields();
        loadTable();
        setLabelStatus("Tarea actualizada correctamente");
    }

    @FXML
    public void deleteTarea(ActionEvent event){
       Tarea tarea = tvTarea.getSelectionModel().getSelectedItem();
       Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
       conf.setTitle("Eliminar tarea");
       conf.setContentText("¿Realizar la acción?");
       Optional<ButtonType> res = conf.showAndWait();
       if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
       try {
           tareaDAO.deleteTarea(tarea);
       } catch (SQLException sqle) {
           AlertUtils.showError("Error al eliminar la tarea");
       }
       cleanFields();
       loadTable();
       setLabelStatus("Tarea eliminada correctamente");
    }

    /**
     * Se cargan el nombre y la ciudad de un parque seleccionado de la ListView
     * en el TextField y el ComboBox respectivamente
     * @param event
     */
    @FXML
    public void getParqueData(Event event){
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        loadParque(selectedParque);
    }

    @FXML
    public void getTareaData(Event event){
        selectedTarea = tvTarea.getSelectionModel().getSelectedItem();
        loadTarea(selectedTarea);
    }

    @FXML
    private void onCancel(ActionEvent event){
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Cancelar modo editable");
        conf.setContentText("¿Deseas cancelar el modo de edición?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        editModeParque(false);
        editModeTarea(false);
        btnCancel.setDisable(true);
        loadParque(selectedParque);
    }



    /**
     * Obtenemos el nombre y la ciudad de un parque dado un objeto Parque
     * @param parque
     */
    public void loadParque(Parque parque){
        String nombreCiudad = null;
        selectedCiudad = new Ciudad();
        try {
            nombreCiudad = parqueDAO.getCiudadParque(parque);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        tfParque.setText(parque.getNombreParque());
        selectedCiudad.setNombreCiudad(nombreCiudad);
        cbCiudad.setValue(selectedCiudad);
    }
    /**
     * Obtenemos los datos de una tarea dado un objeto Tarea
     * @param tarea
     */
    public void loadTarea(Tarea tarea){
       tfTarea.setText(tarea.getNombre());
       taDescripcion.setText(tarea.getDescripcion());
    }

    private void editModeParque(boolean edit){
        btnNewParque.setDisable(edit);
        btnAddParque.setDisable(!edit);
        btnModifyParque.setDisable(!edit);
        btnDeleteParque.setDisable(!edit);
        btnCancel.setDisable(edit);
        btnSave.setDisable(!edit);

        tfParque.setDisable(!edit);
        cbCiudad.setDisable(!edit);

    }

    private void editModeTarea(boolean edit){
        btnNewTarea.setDisable(edit);
        btnAddTarea.setDisable(!edit);
        btnModifyTarea.setDisable(!edit);
        btnDeleteTarea.setDisable(!edit);
        btnCancel.setDisable(edit);

        tfTarea.setDisable(!edit);
        taDescripcion.setDisable(!edit);

    }

}
