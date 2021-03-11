package ua.comparis.controller;

import javafx.event.ActionEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static ua.comparis.controller.Controller.urlComparis;

public class GetBlankController {
    public void getCompare(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк порівняння координат.xlsx"));
    }

    public void getOGZ(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк ОГЗ.xlsx"));
    }

    public void getPGZ(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк ПГЗ.xlsx"));
    }

    public void getDMStoDD(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк ГМС.xlsx"));
    }

    public void getDDtoDMS(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк Градуси.xlsx"));
    }

    public void getCK42toWGS84(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк CK-42.xlsx"));
    }
}
