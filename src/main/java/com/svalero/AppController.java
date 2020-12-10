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
        NEW_TAREA, MODIFY_TAREA, DELETE_TAREA
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
            editModeParque(false);
            editModeTarea(false);
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
        //editModeParque(false);
        //editModeTarea(false);
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
        //editModeParque(false);
        //editModeTarea(false);
        List<Ciudad> ciudades = null;
        try {
            ciudades = ciudadDAO.getAllCiudad();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }
        cbCiudad.setItems(FXCollections.observableList(ciudades));
    }

    private void loadListView(){
        //editModeParque(false);
        //editModeTarea(false);
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
        lblStatus.setText("");
    }

    //Botón "Editar parques"
    @FXML
    public void newParque(ActionEvent event){
        cleanFields();
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Activar modo editable");
        conf.setContentText("¿Deseas activar el modo de edición de parques?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        editModeParque(true);
        btnSave.setDisable(true);
        btnNewTarea.setDisable(true);
        lvParques.setDisable(true);
        tfParque.setDisable(true);
        cbCiudad.setDisable(true);
    }

    //Botón "Guardar"
    @FXML
    public void onSave(ActionEvent event){
        String nombreParque = tfParque.getText();
        Ciudad ciudad  = cbCiudad.getSelectionModel().getSelectedItem();
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        int idParque;
        String nombreTarea = tfTarea.getText();
        String descripcion = taDescripcion.getText();
        selectedTarea = tvTarea.getSelectionModel().getSelectedItem();
        try {
            int idCiudad;
            //System.out.println(id);
            Parque parque;
            Tarea tarea;
            switch (accion){
                case NEW_PARQUE:
                    idCiudad = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
                    parque = new Parque(nombreParque, idCiudad);
                    parqueDAO.insertParque(parque);
                    setLabelStatus("Parque añadido correctamente");
                    break;
                case MODIFY_PARQUE:
                    idCiudad = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
                    parque = new Parque(nombreParque, idCiudad);
                    parqueDAO.updateParque(selectedParque, parque);
                    setLabelStatus("Parque actualizado correctamente");
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
                case NEW_TAREA:
                    idParque = selectedParque.getIdParque();
                    tarea = new Tarea(idParque, nombreTarea, descripcion);
                    tareaDAO.insertTarea(tarea);
                    setLabelStatus("Tarea añadida correctamente");
                    break;
                case MODIFY_TAREA:
                    idParque = selectedTarea.getIdParque();
                    tarea = new Tarea(idParque, nombreTarea, descripcion);
                    tareaDAO.updateTarea(selectedTarea, tarea);
                    break;
                case DELETE_TAREA:
                    Alert confi = new Alert(Alert.AlertType.CONFIRMATION);
                    confi.setTitle("Eliminar tarea");
                    confi.setContentText("¿Realizar la acción?");
                    Optional<ButtonType> resp = confi.showAndWait();
                    if (resp.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
                        tareaDAO.deleteTarea(selectedTarea);
                        setLabelStatus("Tarea eliminada correctamente");
                    break;
            }
        } catch (SQLException sqle){
            AlertUtils.showError("Error al manejar los datos del parque");
        }
        cleanFields();
        loadListView();
        loadTable();
    }

    //Botón "Editar tareas"
    @FXML
    public void newTarea(ActionEvent event){
        cleanFields();
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Activar modo editable");
        conf.setContentText("¿Deseas activar el modo de edición de tareas?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        editModeTarea(true);
        btnSave.setDisable(true);
        btnNewParque.setDisable(true);
        tfTarea.setDisable(true);
        taDescripcion.setDisable(true);
    }

    //Botón "Nuevo parque"
    @FXML
    public void addParque(ActionEvent event) {
        cleanFields();
        AlertUtils.showAlert("Escribe el nombre del parque y selecciona una ciudad");
        editModeParque(true);
        accion = Accion.NEW_PARQUE;
        btnModifyParque.setDisable(true);
        btnDeleteParque.setDisable(true);
        btnAddParque.setDisable(true);
    }

    //Botón "Modificar parque"
    @FXML
    public void modifyParque(ActionEvent event) {
        editModeParque(true);
        AlertUtils.showAlert("Selecciona un parque de la lista para modificarlo");
        accion = Accion.MODIFY_PARQUE;
        btnModifyParque.setDisable(true);
        btnDeleteParque.setDisable(true);
        btnAddParque.setDisable(true);
        lvParques.setDisable(false);
    }

    //Botón "Eliminar parque"
    @FXML
    public void deleteParque(ActionEvent event){
        editModeParque(true);
        //Parque parque = lvParques.getSelectionModel().getSelectedItem();
        AlertUtils.showAlert("Selecciona un parque de la lista para eliminarlo");
        accion = Accion.DELETE_PARQUE;
        btnModifyParque.setDisable(true);
        btnDeleteParque.setDisable(true);
        btnAddParque.setDisable(true);
        tfParque.setDisable(true);
        cbCiudad.setDisable(true);
        lvParques.setDisable(false);
    }

    //Botón "Nueva tarea"
    @FXML
    public void addTarea(ActionEvent event){
        cleanFields();
        editModeTarea(true);
        accion = Accion.NEW_TAREA;
        btnModifyTarea.setDisable(true);
        btnDeleteTarea.setDisable(true);
        btnAddTarea.setDisable(true);
        lvParques.setDisable(false);
    }

    @FXML
    public void modifyTarea(ActionEvent event){
        AlertUtils.showAlert("Selecciona una tarea de la tabla para modificarla");
        editModeTarea(true);
        accion = Accion.MODIFY_TAREA;
        btnModifyTarea.setDisable(true);
        btnDeleteTarea.setDisable(true);
        btnAddTarea.setDisable(true);
        tvTarea.setDisable(false);
    }

    @FXML
    public void deleteTarea(ActionEvent event){
        editModeTarea(true);
        //Parque parque = lvParques.getSelectionModel().getSelectedItem();
        AlertUtils.showAlert("Selecciona una tarea de la tabla para eliminarla");
        accion = Accion.DELETE_TAREA;
        tfTarea.setDisable(true);
        taDescripcion.setDisable(true);
        tvTarea.setDisable(false);
        btnDeleteTarea.setDisable(true);
        btnModifyTarea.setDisable(true);
        btnAddTarea.setDisable(true);
        btnCancel.setDisable(false);
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
        btnCancel.setDisable(!edit);
        btnSave.setDisable(!edit);
        tfParque.setDisable(!edit);
        cbCiudad.setDisable(!edit);
        lvParques.setDisable(true);
        tvTarea.setDisable(true);
   }

    private void editModeTarea(boolean edit){
        btnNewTarea.setDisable(edit);
        btnAddTarea.setDisable(!edit);
        btnModifyTarea.setDisable(!edit);
        btnDeleteTarea.setDisable(!edit);
        btnCancel.setDisable(!edit);
        btnSave.setDisable(!edit);
        tfTarea.setDisable(!edit);
        taDescripcion.setDisable(!edit);
        lvParques.setDisable(true);
        tvTarea.setDisable(true);
    }
}