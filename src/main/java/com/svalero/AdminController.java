package com.svalero;

import com.svalero.DAO.CiudadDAO;
import com.svalero.DAO.OperarioDAO;
import com.svalero.DAO.ParqueDAO;
import com.svalero.DAO.TareaDAO;
import com.svalero.domain.Operario;
import com.svalero.domain.Tarea;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.AlertUtils;
import util.R;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController {

    public TextField tfOperario;
    public TextField tfPassword;
    public Button btnAddOperario, btnUpdateOperario, btnDeleteOperario, btnExit, btnEditMode;
    public TableView<Operario> tvOperario;

    private OperarioDAO operarioDAO;
    private Operario selectedOperario;
    private Operario operario;

    public void connect() throws SQLException, IOException, ClassNotFoundException {
        operarioDAO = new OperarioDAO();
        operarioDAO.connect();
        setTable();
        refreshTable();
    }

    public void refreshTable(){
        try {
            loadTable();
            cleanFields();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadTable() throws SQLException, IOException, ClassNotFoundException {

        //editModeParque(false);
        //editModeTarea(false);
        tvOperario.getItems().clear();
        List<Operario> operarios = null;
        try {
            operarios = operarioDAO.getAllOperario();
            tvOperario.setItems(FXCollections.observableArrayList((operarios)));
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al cargar los datos en la tabla");
        }
    }

    public void setTable() throws SQLException, IOException, ClassNotFoundException {
        Field[] fields = Operario.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals("id"))
                continue;
            TableColumn<Operario, String> column = new TableColumn<>(field.getName());
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            tvOperario.getColumns().add(column);
        }
        tvOperario.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public AdminController(){}

    public boolean existsOperario(Operario operario){
        boolean exists = true;
        try {
            exists = operarioDAO.existsOperario(operario);
        } catch (SQLException sqle) {
            AlertUtils.showAlert("El operario ya existe");
        }
        return exists;
    }
    @FXML
    public void addOperario(ActionEvent event){
        String nombreOperario = tfOperario.getText();
        String passwordOperario = tfPassword.getText();
        if (nombreOperario.isEmpty() || passwordOperario.isEmpty()){
            AlertUtils.showAlert("Rellena todos los campos");
            return;
        }
        boolean exists;
        operario = new Operario(nombreOperario, passwordOperario);
        exists = existsOperario(operario);
        if (exists) {
            AlertUtils.showError("El operario ya existe");
            return;
        }
        try {
            operarioDAO.insertOperario(operario);
        } catch (SQLException sqle){
            AlertUtils.showError("Error al insertar operario");
        }
        refreshTable();

    }

    @FXML
    public void updateOperario(ActionEvent event){
        String nombreOperario = tfOperario.getText();
        String passwordOperario = tfPassword.getText();
        if (nombreOperario.isEmpty() || passwordOperario.isEmpty()){
            AlertUtils.showAlert("Rellena todos los campos");
            return;
        }
        if (nombreOperario.equals("admin") || passwordOperario.equals("admin")) {
            AlertUtils.showAlert("NO PUEDES ACTUALIZAR AL ADMIN");
            return;
        }
        selectedOperario = tvOperario.getSelectionModel().getSelectedItem();
        try {
            operario = new Operario(nombreOperario, passwordOperario);
            operarioDAO.updateOperario(selectedOperario, operario);
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al actualizar el operario");
        }
        refreshTable();
    }

    @FXML
    public void deleteOperario(ActionEvent event){
        selectedOperario = tvOperario.getSelectionModel().getSelectedItem();
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Eliminar operario");
        conf.setContentText("¿Realizar la acción?");
        Optional<ButtonType> res = conf.showAndWait();
        if (res.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) return;
        if (selectedOperario == null){
            AlertUtils.showAlert("Selecciona un operario");
            return;
        }
        if (selectedOperario.getNombre().equals("admin") || selectedOperario.getPassword().equals("admin")) {
            AlertUtils.showAlert("NO PUEDES BORRAR AL ADMIN");
            return;
        }
        try {
            operarioDAO.deleteOperario(selectedOperario);
        } catch (SQLException sqle) {
            AlertUtils.showError("Error al eliminar el operario");
        }
        refreshTable();
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

            Stage admindStage = (Stage) tvOperario.getScene().getWindow();
            admindStage.close();
        } catch (IOException ioe){
            AlertUtils.showError("Error al salir de la pantalla");
        }
    }

    @FXML
    public void getOperarioData(Event event){
        selectedOperario = tvOperario.getSelectionModel().getSelectedItem();
        loadOperario(selectedOperario);
    }

    public void loadOperario(Operario operario) {
        tfOperario.setText(operario.getNombre());
        tfPassword.setText(operario.getPassword());
    }

    public void cleanFields(){
        tfOperario.clear();
        tfPassword.clear();
    }

}
