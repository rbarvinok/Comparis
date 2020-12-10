package ua.comparis.javaclass.servisClass;

import javafx.scene.control.Alert;

public class AlertAndInform {

    public String hd, ct;


    public void inform() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Довідка");
        alert.setHeaderText(hd);
        alert.setContentText(ct);
        alert.showAndWait();
    }

    public void alert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText(hd);
        alert.setContentText(ct);
        alert.showAndWait();
    }
}
