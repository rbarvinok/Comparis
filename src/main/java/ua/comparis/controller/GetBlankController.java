package ua.comparis.controller;

import javafx.event.ActionEvent;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static ua.comparis.controller.Controller.urlComparis;

public class GetBlankController {
    public void getCompare(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк порівняння координат.csv"));
    }

    public void getOGZ(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк ОГЗ.csv"));
    }

    public void getDMStoDD(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк ГМС в Градуси.csv"));
    }

    public void getDDtoDMS(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(urlComparis + "/blank/Бланк Градуси в ГМС.csv"));
    }


}
