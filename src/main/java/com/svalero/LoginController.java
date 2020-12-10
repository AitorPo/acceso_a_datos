package com.svalero;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.AlertUtils;
import util.R;

import java.io.IOException;

public class LoginController {

    public TextField tfUsername;
    public PasswordField pfPassword;
    public Label lblLogin;

    public LoginController(){}

    @FXML
    public void onLogin(ActionEvent event){
        try {
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
            } else {
                lblLogin.setText("Failed");
            }
        } catch (IOException ioe) {
            AlertUtils.showError("Error al tratar los datos del login");
        }
    }
}
