package ua.comparis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ua.comparis.javaclass.servisClass.AlertAndInform;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import static ua.comparis.controller.Controller.localZone;

public class SettingsController {
    private int newGMT;
    AlertAndInform inform = new AlertAndInform();

    @FXML
    public TextField GMTInput;

    @FXML
    public Button SaveNewGMT;

    public void onClickNewPressure(ActionEvent event) throws IOException {

        if (GMTInput.getText().equals("")) {
            inform.hd = "Помилка!";
            inform.hd = "Невірний формат даних\n";
            inform.ct = "Поле для вводу не може бути пустим та має містити тільки цифрові значення \n";
            inform.alert();
            GMTInput.setText("");
            return;
        }

        try {
            newGMT = Integer.parseInt(GMTInput.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            inform.hd = "Помилка!";
            inform.hd = "Невірний формат даних\n";
            inform.ct = "Поле для вводу не може бути пустим та має містити тільки цифрові значення \n";
            inform.alert();
            GMTInput.setText("");
            return;
        }

        localZone = "GMT+" + newGMT;

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("settings.txt", false), "UTF-8");
        osw.write("GMT=" + localZone + "\n");
        osw.close();

        Stage stage = (Stage) SaveNewGMT.getScene().getWindow();
        stage.close();
    }


    public void onClickReset(ActionEvent actionEvent) {
        GMTInput.setText("2");

    }
}
