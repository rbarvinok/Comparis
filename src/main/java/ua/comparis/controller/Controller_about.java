package ua.comparis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class Controller_about {
    @FXML
    public Button exitAbout;

    public void OnClick_btnAboutExit(ActionEvent actionEvent) {
        Stage stage=(Stage) exitAbout.getScene().getWindow();
        stage.close();
    }
}
