package com.svalero;

import com.svalero.DAO.OperarioDAO;
import com.svalero.domain.Operario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.AlertUtils;
import util.R;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    public TextField tfUsername;
    public PasswordField pfPassword;
    public Label lblLogin;


    public LoginController(){}

    @FXML
    public void onLogin(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        OperarioDAO operarioDAO = new OperarioDAO();
        Operario operario = new Operario();
        operarioDAO.connect();
        operario.setNombre(tfUsername.getText());
        operario.setPassword(pfPassword.getText());
        System.out.println(operario.toString());
        System.out.println(pfPassword.getText());
        try {
            boolean exist = operarioDAO.existsOperario(operario);
            if(exist){
                Stage stage = new Stage();
                AppController controller = new AppController();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(R.getUi("parques.fxml"));
                loader.setController(controller);
                VBox vBox = loader.load();

                Scene scene = new Scene(vBox);
                stage.setScene(scene);
                stage.show();

                Stage loginStage = (Stage) tfUsername.getScene().getWindow();
                loginStage.close();
            }

        /*try {
            if (tfUsername.getText().equals("user") && pfPassword.getText().equals("pass")) {
                lblLogin.setText("Success");
                Stage stage = new Stage();
                AppController controller = new AppController();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(R.getUi("parques.fxml"));
                loader.setController(controller);
                VBox vBox = loader.load();

                Scene scene = new Scene(vBox);
                stage.setScene(scene);
                stage.show();

                Stage loginStage = (Stage) tfUsername.getScene().getWindow();
                loginStage.close();
            }*/ else if (tfUsername.getText().equals("admin1") && pfPassword.getText().equals("admin1")){
                Stage stage = new Stage();
                AdminController adminController = new AdminController();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(R.getUi("admin.fxml"));
                loader.setController(adminController);
                Parent root = loader.load();

                adminController.connect();
                adminController.refreshTable();
                adminController.setTable();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                Stage loginStage = (Stage) tfUsername.getScene().getWindow();
                loginStage.close();
            } else {
                lblLogin.setText("Failed");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
