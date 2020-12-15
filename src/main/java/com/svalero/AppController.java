package com.svalero;

import com.svalero.DAO.CiudadDAO;
import com.svalero.DAO.ParqueDAO;
import com.svalero.DAO.TareaDAO;
import com.svalero.domain.Ciudad;
import com.svalero.domain.Parque;
import com.svalero.domain.Tarea;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.AlertUtils;
import util.R;

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
    public ComboBox<Ciudad> cbFilter;
    public Label lblStatus;
    public ListView<Parque> lvParques;
    public TableView<Tarea> tvTarea;
    public Button btnAddParque, btnAddTarea, btnModifyParque,
            btnModifyTarea, btnDeleteParque, btnDeleteTarea, btnNewTarea,
            btnNewParque, btnCancel, btnSave, btnDeleteAll, btnRecover;

    public AppController() {
    }

    private enum Accion {
        NEW_PARQUE, MODIFY_PARQUE, DELETE_PARQUE,
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

    private void loadTable() {
        //editModeParque(false);
        //editModeTarea(false);
        tvTarea.getItems().clear();
        List<Tarea> tareas = null;
        try {
            tareas = tareaDAO.getAllTarea();
            tvTarea.setItems(FXCollections.observableArrayList(tareas));
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos en la tabla");
        }
    }

    private void setTable() {
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

    private void loadComboBox() {
        List<Ciudad> ciudades = null;
        try {
            ciudades = ciudadDAO.getAllCiudad();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }
        assert ciudades != null;
        cbCiudad.setItems(FXCollections.observableList(ciudades));
        cbFilter.setItems(FXCollections.observableList(ciudades));
    }


    private void loadListView() {
        lvParques.getItems().clear();
        List<Parque> parques = null;
        try {
            parques = parqueDAO.getAllParque();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }
        lvParques.setItems(FXCollections.observableList(parques));
    }

    private void setLabelStatus(String message) {
        lblStatus.setText(message);
    }

    private void cleanFields() {
        tfParque.clear();
        tfTarea.clear();
        taDescripcion.clear();
        //Reseteamos la elecciópn del ComboBox
        cbCiudad.valueProperty().set(null);
        lblStatus.setText("");
    }

    //Botón "Recuperar registro"
    @FXML
    public void onRecover(ActionEvent event){
        if (Parque.recoveredParque == null) return;
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Recuperar último registro borrado");
        conf.setContentText("¿Deseas recuperar el último parque eliminado?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        try {
            parqueDAO.insertParque(Parque.recoveredParque);
            AlertUtils.showAlert("Parque recuperado");
            loadListView();
            loadTable();

        } catch (SQLException sqle) {
            AlertUtils.showError("Error al recuperar el registro");
        }
    }

    //Botón "Borrar TODO"
    public void onDeleteAll(ActionEvent event){
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Eliminar todos los registros");
        conf.setContentText("¿Deseas eliminar todos los parques de la BD?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        try {
            parqueDAO.deleteAll();
            loadListView();
            loadTable();
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al eliminar los datos de la BD");
        }
    }

    //Botón "Buscar"
    @FXML
    public void onFilter(ActionEvent event){
        lvParques.getItems().clear();
        List<Parque> parques = null;
        try{
            parques = parqueDAO.getFilterParque(cbFilter.getSelectionModel().getSelectedItem());

            if (parques.isEmpty()){
                AlertUtils.showAlert("No existen parques en esa ciudad");
                loadListView();
                return;
            } else {
                lvParques.setItems(FXCollections.observableList(parques));
            }
        } catch (SQLException sqlException) {
            AlertUtils.showError("Error al cargar los datos de la aplicación");
        }

    }

    //Botón "Editar parques"
    @FXML
    public void newParque(ActionEvent event) {
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
        btnRecover.setDisable(true);

    }

    //Botón "Guardar"
    @FXML
    public void onSave(ActionEvent event) {
        String nombreParque = tfParque.getText();
        Ciudad ciudad = cbCiudad.getSelectionModel().getSelectedItem();
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        Parque.recoveredParque = selectedParque;
        int idParque;
        boolean exists;
        String nombreTarea = tfTarea.getText();
        String descripcion = taDescripcion.getText();
        selectedTarea = tvTarea.getSelectionModel().getSelectedItem();
        try {
            int idCiudad;
            //System.out.println(id);
            Parque parque;
            Tarea tarea;
            switch (accion) {
                case NEW_PARQUE:
                    if (ciudad == null){
                        AlertUtils.showError("Selecciona una ciudad");
                        return;
                    }
                    idCiudad = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
                    parque = new Parque(nombreParque, idCiudad);
                    exists = parqueDAO.existsParque(parque);
                    if (exists) {
                        AlertUtils.showError("El parque ya existe");
                        return;
                    }
                    parqueDAO.insertParque(parque);
                    setLabelStatus("Parque añadido correctamente");
                    break;
                case MODIFY_PARQUE:
                    if (selectedParque == null) {
                        AlertUtils.showError("Selecciona un parque para poder modificarlo");
                        return;
                    }
                    idCiudad = ciudadDAO.getIdCiudad(ciudad.getNombreCiudad());
                    parque = new Parque(nombreParque, idCiudad);
                    parqueDAO.updateParque(selectedParque, parque);
                    setLabelStatus("Parque actualizado correctamente");
                    break;
                case DELETE_PARQUE:
                    if (selectedParque == null){
                        AlertUtils.showError("Selecciona un parque para poder eliminarlo");
                        return;
                    }
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
                    conf.setTitle("Eliminar parque");
                    conf.setContentText("¿Realizar la acción?");
                    Optional<ButtonType> res = conf.showAndWait();
                    if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
                    parqueDAO.deleteParque(selectedParque);
                    setLabelStatus("Parque eliminado correctamente");
                    break;
                case NEW_TAREA:
                    if (selectedParque == null){
                        AlertUtils.showError("Selecciona un parque de la lista para añadir una tarea");
                        return;
                    } else {
                        if (tfTarea.getText().isEmpty()){
                            AlertUtils.showError("Ponle nombre a la tarea");
                            return;
                        } else if (taDescripcion.getText().isEmpty()){
                            AlertUtils.showError("Añade una descripción de la tarea");
                            return;
                        }
                    }
                    idParque = selectedParque.getIdParque();
                    tarea = new Tarea(idParque, nombreTarea, descripcion);
                    tareaDAO.insertTarea(tarea);
                    setLabelStatus("Tarea añadida correctamente");
                    break;
                case MODIFY_TAREA:
                    if (selectedTarea == null) {
                        AlertUtils.showError("Selecciona una tarea para modiciarla");
                        return;
                    }
                    idParque = selectedTarea.getIdParque();
                    tarea = new Tarea(idParque, nombreTarea, descripcion);
                    tareaDAO.updateTarea(selectedTarea, tarea);
                    setLabelStatus("Tarea actualizada correctamente");
                    break;
                case DELETE_TAREA:
                    if(selectedTarea == null){
                        AlertUtils.showError("Selecciona una tarea para eliminarla");
                        return;
                    }
                    Alert confi = new Alert(Alert.AlertType.CONFIRMATION);
                    confi.setTitle("Eliminar tarea");
                    confi.setContentText("¿Realizar la acción?");
                    Optional<ButtonType> resp = confi.showAndWait();
                    if (resp.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
                    tareaDAO.deleteTarea(selectedTarea);
                    setLabelStatus("Tarea eliminada correctamente");
                    break;
            }
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al manejar los datos del parque");
        }
        cleanFields();
        loadListView();
        loadTable();
    }

    //Botón "Editar tareas"
    @FXML
    public void newTarea(ActionEvent event) {
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
    public void deleteParque(ActionEvent event) {
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
    public void addTarea(ActionEvent event) {
        cleanFields();
        editModeTarea(true);
        AlertUtils.showAlert("Selecciona un parque de la lista para añadir una tarea");
        accion = Accion.NEW_TAREA;
        btnModifyTarea.setDisable(true);
        btnDeleteTarea.setDisable(true);
        btnAddTarea.setDisable(true);
        lvParques.setDisable(false);
        btnRecover.setDisable(true);
    }

    @FXML
    public void modifyTarea(ActionEvent event) {
        AlertUtils.showAlert("Selecciona una tarea de la tabla para modificarla");
        editModeTarea(true);
        accion = Accion.MODIFY_TAREA;
        btnModifyTarea.setDisable(true);
        btnDeleteTarea.setDisable(true);
        btnAddTarea.setDisable(true);
        tvTarea.setDisable(false);
    }

    @FXML
    public void deleteTarea(ActionEvent event) {
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

    @FXML
    public void onExit(ActionEvent event){
        try {
            Stage stage = new Stage();
            LoginController loginController = new LoginController();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUi("login.fxml"));
            loader.setController(loginController);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            Stage operariOStage = (Stage) tfTarea.getScene().getWindow();
            operariOStage.close();
        } catch (IOException ioe) {
            AlertUtils.showError("Error al salir de la pantalla");
        }
    }

    /**
     * Se cargan el nombre y la ciudad de un parque seleccionado de la ListView
     * en el TextField y el ComboBox respectivamente
     *
     * @param event
     */
    @FXML
    public void getParqueData(Event event) {
        selectedParque = lvParques.getSelectionModel().getSelectedItem();
        loadParque(selectedParque);
    }

    @FXML
    public void getTareaData(Event event) {
        selectedTarea = tvTarea.getSelectionModel().getSelectedItem();
        loadTarea(selectedTarea);
    }

    @FXML
    private void onCancel(ActionEvent event) {
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
     *
     * @param parque
     */
    public void loadParque(Parque parque) {
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
     *
     * @param tarea
     */
    public void loadTarea(Tarea tarea) {
        tfTarea.setText(tarea.getNombre());
        taDescripcion.setText(tarea.getDescripcion());
    }

    private void editModeParque(boolean edit) {
        btnNewParque.setDisable(edit);
        btnAddParque.setDisable(!edit);
        btnModifyParque.setDisable(!edit);
        btnDeleteParque.setDisable(!edit);
        btnCancel.setDisable(!edit);
        btnSave.setDisable(!edit);
        btnDeleteAll.setDisable(!edit);
        btnRecover.setDisable(!edit);
        tfParque.setDisable(!edit);
        cbCiudad.setDisable(!edit);
        lvParques.setDisable(true);
        tvTarea.setDisable(true);
    }

    private void editModeTarea(boolean edit) {
        btnNewTarea.setDisable(edit);
        btnAddTarea.setDisable(!edit);
        btnModifyTarea.setDisable(!edit);
        btnDeleteTarea.setDisable(!edit);
        btnCancel.setDisable(!edit);
        btnSave.setDisable(!edit);
        btnDeleteAll.setDisable(!edit);
        btnRecover.setDisable(!edit);
        tfTarea.setDisable(!edit);
        taDescripcion.setDisable(!edit);
        lvParques.setDisable(true);
        tvTarea.setDisable(true);
    }
}