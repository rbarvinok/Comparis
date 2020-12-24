package ua.comparis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import ua.comparis.javaclass.domain.SourceDD;
import ua.comparis.javaclass.domain.SourceNMEA;
import ua.comparis.javaclass.servisClass.AlertAndInform;
import ua.comparis.javaclass.servisClass.FileChooserRun;
import ua.comparis.javaclass.servisClass.GetSettings;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Math.abs;
import static ua.comparis.controller.Controller.*;
import static ua.comparis.javaclass.servisClass.FileChooserRun.selectedOpenFile;

public class AccuracyController implements Initializable {

    public static double trueLat;
    public static double trueLong;
    public static double trueAlt;
    public static int count;
    public static boolean isAccuracySource = false;
    //public String sourceType = "UNKNOWN";
    public String sourceType = "DD";
    //public String sourceType = "NMEA";

    private int latD;
    private int latM;
    private double latS;
    private int longD;
    private int longM;
    private double longS;

    AlertAndInform inform = new AlertAndInform();
    GetSettings getSettings = new GetSettings();
    FileChooserRun fileChooserRun = new FileChooserRun();


    @FXML
    public TextField latInput, longInput, altInput, localZoneInput;
    public Button cancelBtn, dmsButton;
    public Button NMEABtn, relatedFile;
    @FXML
    public ImageView imgView;

    @SneakyThrows
    @Override
    public void initialize( URL location, ResourceBundle resources ) {
        getSettings.getSettings();
        latInput.setText(String.valueOf(trueLat));
        longInput.setText(String.valueOf(trueLong));
        altInput.setText(String.valueOf(trueAlt));
        localZoneInput.setText(String.valueOf(localZone));

    }

    public void onClickSave( ActionEvent event ) throws IOException {

        if (latInput.getText().equals("") || longInput.getText().equals("")) {
            inform.hd = "Помилка!";
            inform.hd = "Невірний формат даних\n";
            inform.ct = "Поле для вводу не може бути пустим та має містити тільки цифрові значення \n";
            inform.alert();
            latInput.setText("");
        }

        if (dmsButton.getText().equals("Градуси")) {
            getDMC();
            DMStoDD();
            imgView.setVisible(true);

        } else if (dmsButton.getText().equals("Градуси, мінути, секунди")) {
            try {
                trueLat = Double.parseDouble(latInput.getText().replace(",", "."));
                trueLong = Double.parseDouble(longInput.getText().replace(",", "."));
                trueAlt = Double.parseDouble(altInput.getText().replace(",", "."));
                imgView.setVisible(true);
            } catch (NumberFormatException e) {
                inform.hd = "Помилка!";
                inform.hd = "Невірний формат даних\n";
                inform.ct = "Поле для вводу не може бути пустим та має містити тільки цифрові значення \n";
                inform.alert();
                latInput.setText("");
            }
        }

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("settings.txt", false), "UTF-8");
        osw.write("GMT=" + localZone + "\n");
        osw.write("Latitude=" + trueLat + "\n");
        osw.write("Longitude=" + trueLong + "\n");
        osw.write("Altitude=" + trueAlt + "\n");
        osw.close();
    }

    public void onClickReset( ActionEvent actionEvent ) {
        latInput.setText("0.0");
        longInput.setText("0.0");
        altInput.setText("0.0");
    }

    public void onClickDMS( ActionEvent actionEvent ) {
        if (dmsButton.getText().equals("Градуси, мінути, секунди")) {
            dmsButton.setText("Градуси");

            latD = (int) abs(trueLat);
            latM = (int) (abs(trueLat - latD) * 60);
            latS = ((trueLat - latD) * 60 - latM) * 60;

            longD = (int) abs(trueLong);
            longM = (int) (abs(trueLong - longD) * 60);
            longS = ((trueLong - longD) * 60 - longM) * 60;

            latInput.setText(latD + "°" + latM + "'" + latS + "\"");
            longInput.setText(longD + "°" + longM + "'" + longS + "\"");
        } else {

            if (dmsButton.getText().equals("Градуси")) {
                dmsButton.setText("Градуси, мінути, секунди");
                DMStoDD();
                latInput.setText(String.valueOf(trueLat));
                longInput.setText(String.valueOf(trueLong));
            }
        }
    }

    public void getDMC() {
        String line;

        line = latInput.getText()
                .replaceAll(",", ".")
                .replaceAll("°", ";")
                .replaceAll("'", ";")
                .replaceAll("\"", ";");

        String[] split = line.split(";");
        latD = Integer.parseInt(split[0]);
        latM = Integer.parseInt(split[1]);
        latS = Double.parseDouble(split[2]);

        line = longInput.getText()
                .replaceAll(",", ".")
                .replaceAll("°", ";")
                .replaceAll("'", ";")
                .replaceAll("\"", ";");

        split = line.split(";");
        longD = Integer.parseInt(split[0]);
        longM = Integer.parseInt(split[1]);
        longS = Double.parseDouble(split[2]);
    }

    private void DMStoDD() {
        getDMC();
//        trueLat = Math.rint((latD + latM / 60 + latS / 60 / 60) * 100000000) / 100000000;
        trueLat = latD + Double.valueOf(latM) / 60 + latS / 60 / 60;
        trueLong = longD + Double.valueOf(longM) / 60 + longS / 60 / 60;
    }

    @SneakyThrows
    public void onClickNMEA( ActionEvent actionEvent ) {
        fileChooserRun.openFileChooser();
        openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
        openDirectory = selectedOpenFile.getParent();

        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split(",");

            if (line.split(",")[0].equals("$GNGGA")) {
                lineNumber++;

                SourceNMEA source = new SourceNMEA(
                        split[1],
                        split[2],
                        split[4],
                        Double.parseDouble(split[9]));
                sourceNMEA.add(source);
            }
        }
        fileReader.close();
        count = lineNumber;

        sourceType = "NMEA";
        isAccuracySource = true;

        Stage stage = (Stage) NMEABtn.getScene().getWindow();
        stage.close();
    }

    public void onClickRelatedFile( ActionEvent actionEvent ) throws IOException {
        fileChooserRun.openFileChooser();
        openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
        openDirectory = selectedOpenFile.getParent();

        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            line = line
                    .replaceAll(",", ".")
                    .replaceAll(";", ",");
            String[] split = line.split(",");
            //String[] split = line.split(";");
            if (split.length <= 2 || lineNumber < 3) {
                lineNumber++;
                continue;
            }
            lineNumber++;

            SourceDD source = new SourceDD(
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]));
            sourceDD.add(source);
        }
        fileReader.close();
        count = lineNumber;
        sourceType = "DD";
        isAccuracySource = true;

        Stage stage = (Stage) relatedFile.getScene().getWindow();
        stage.close();
    }

    public boolean getSours (){
        return isAccuracySource;
    }

    public void onClickCancel( ActionEvent actionEvent ) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
