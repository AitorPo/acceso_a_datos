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
import java.util.Properties;

public class LoginController {

    public TextField tfUsername;
    public PasswordField pfPassword;
    public Label lblLogin;


    public LoginController(){}

    @FXML
    public void onLogin(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        OperarioDAO operarioDAO = new OperarioDAO();
        Properties config = new Properties();
        String admin = null;
        String adminPassword = null;
        operarioDAO.connect();
        try {
            config.load(R.getProperties("db.properties"));
             admin = config.getProperty("admin_user");
             adminPassword = config.getProperty("admin_pass");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Operario operario = new Operario();
        operario.setNombre(tfUsername.getText());
        operario.setPassword(pfPassword.getText());
        boolean exist = operarioDAO.existsOperario(operario);

        try {
            if (exist && !tfUsername.getText().equals(admin)) {
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
            } else if(tfUsername.getText().equals(admin) && pfPassword.getText().equals(adminPassword)) {
                Stage stage = new Stage();
                AdminController controller = new AdminController();
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(R.getUi("admin.fxml"));
                loader.setController(controller);
                Parent root = loader.load();
                controller.connect();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                Stage loginStage = (Stage) tfUsername.getScene().getWindow();
                loginStage.close();
            } else {
                lblLogin.setText("OPERARIO INCORRECTO");
            }

        }catch (SQLException sqle) {
            AlertUtils.showError("ERROR EN EL LOGIN");
        }

    }
}
