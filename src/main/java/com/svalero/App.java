package com.svalero;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.R;


public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //AppController controller = new AppController();
        LoginController controller = new LoginController();
        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(R.getUi("login.fxml"));
        loader.setController(controller);
        //VBox vBox = loader.load();
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }

}
