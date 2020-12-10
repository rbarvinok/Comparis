package ua.comparis.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ua.comparis.javaclass.domain.*;
import ua.comparis.javaclass.servisClass.AlertAndInform;
import ua.comparis.javaclass.servisClass.FileChooserRun;
import ua.comparis.javaclass.servisClass.GetSettings;
import ua.comparis.javaclass.servisClass.OpenStage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.comparis.javaclass.geo.ConverterCoordinateSystem.*;
import static ua.comparis.javaclass.geo.DDtoDMSConverter.rezultDDtoDMSBulk;
import static ua.comparis.javaclass.geo.DMStoDDConverter.rezultDMStoDDBulk;
import static ua.comparis.javaclass.geo.OgzWGS84Calculator.rezultOGZ84Bulk;
import static ua.comparis.javaclass.servisClass.FileChooserRun.selectedOpenFile;

@Slf4j
public class Controller {
    AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();

    public static String urlComparis = "D:/Comparis";
    public static String localZone = "GMT+2";
    public static String openFile = " ";
    public static String openDirectory;
    public static String status = "UNKNOWN";
    public static String isCalc = "UNKNOWN";
    public String lineCount;
    public String headSourceOGZ84 = "Час,    Широта,    Довгота,    Висота,    Час,    Широта,    Довгота,    Висота \n";
    public String headOgz84 = "Час,    Широта,    Довгота,    Висота,       Час,    Широта,    Довгота,    Висота,    " + "Відстань 2D,    Відстань 3D,      Кут відхилення\n";
    public String headDMStoDD = "Широта,         Довгота,                Широта,          Довгота,          Висота   \n";
    public String headDDtoCK42 = "Широта,   Довгота,          Широта,    Довгота,   Висота,            X,        Y,      H  \n";
    public String headCK42toDD = "X,        Y,      H,         Широта,   Довгота,    Висота \n";

    public static List<SourceOGZ84> sourceOGZ84 = new ArrayList<>();
    public static List<SourceDMS> sourceDMS = new ArrayList<>();
    public static List<SourceDD> sourceDD = new ArrayList<>();
    public static List<Ogz84> rezultsOGZ84 = new ArrayList<>();
    public static List<DMStoDD> rezultsDMStoDD = new ArrayList<>();
    public static List<DDtoDMS> rezultsDDtoDMS = new ArrayList<>();
    public static List<DMStoCK42> rezultsDMStoCK42 = new ArrayList<>();
    public static List<DDtoCK42> rezultsDDtoCK42 = new ArrayList<>();
    public static List<CK42toDD> rezultsCK42toDD = new ArrayList<>();

    @FXML
    public TextArea outputText;
    public TextField statusBar, labelLineCount;
    public Label statusLabel;
    public ProgressIndicator progressIndicator;
    public ComboBox<String> choiceGeoProblem, choiceCoordinateConverter;

    public void openFile() throws Exception {
        if (outputText.getText().equals("")) {
            statusBar.setText("");
            progressIndicatorRun();

            fileChooserRun.openFileChooser();
            //openFile = selectedOpenFile.getName();
            openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
            openDirectory = selectedOpenFile.getParent();
            progressIndicator.setVisible(false);
            return;
        } else {
            inform.hd = "Файл уже відкритий";
            inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
            inform.inform();
            return;
        }
    }

    public void getSoursOGZ84() throws Exception {

        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            //line = line.replaceAll(";", ",");
            line = line.replaceAll(",", ".");


            //String[] split = line.split(",");
            String[] split = line.split(";");

            if (split.length <= 6 || lineNumber < 4) {
                lineNumber++;
                continue;
            }

            lineNumber++;

            SourceOGZ84 source = new SourceOGZ84(
                    split[0],
                    split[1],
                    split[2],
                    split[3],
                    split[4],
                    split[5],
                    split[6],
                    split[7]);
            sourceOGZ84.add(source);
        }
        rezultsOGZ84 = rezultOGZ84Bulk(sourceOGZ84);

        fileReader.close();

        List<String> soursStrings = sourceOGZ84.stream().map(SourceOGZ84::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);
        outputText.setText(String.valueOf(new StringBuilder()
                .append(headSourceOGZ84)
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber - 4);
        labelLineCount.setText("Cтрок:  " + lineCount);

        getSettings.getGMT();

        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void getSourceDMS() throws IOException {
        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            //
            line = line
                    .replaceAll(",", ".")
                    .replaceAll("°", ";")
                    .replaceAll("'", ";")
                    .replaceAll("\"", ";");

            line = line.replaceAll(";", ",");
            //String[] split = line.split(",");
            String[] split = line.split(",");

            if (split.length <= 3 || lineNumber < 3) {
                lineNumber++;
                continue;
            }

            lineNumber++;

            SourceDMS source = new SourceDMS(
                    Integer.valueOf(split[1]),
                    Integer.valueOf(split[2]),
                    Double.parseDouble(split[3]),
                    Integer.valueOf(split[8]),
                    Integer.valueOf(split[9]),
                    Double.parseDouble(split[10]),
                    Double.parseDouble(split[14]));
            sourceDMS.add(source);
        }
        fileReader.close();

        List<String> soursStrings = sourceDMS.stream().map(SourceDMS::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);

        outputText.setText(String.valueOf(new StringBuilder()
                .append("Широта,       Довгота,      Висота \n")
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber - 3);
        labelLineCount.setText("Cтрок:  " + lineCount);
        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void getSourceDD() throws IOException {
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

        //rezultsDDtoDMS = rezultDDtoCK42Bulk( sourceDD);

        fileReader.close();

        List<String> soursStrings = sourceDD.stream().map(SourceDD::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);

        outputText.setText(String.valueOf(new StringBuilder()
                .append("Широта,       Довгота,      Висота \n")
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber - 3);
        labelLineCount.setText("Cтрок:  " + lineCount);
        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void openDataDMStoDD() throws Exception {
        getSourceDMS();
        rezultsDMStoDD = rezultDMStoDDBulk(sourceDMS);
    }

    public void openDataDMStoCK42() throws Exception {
        getSourceDMS();
        rezultsDMStoCK42 = rezultDMStoCK42Bulk(sourceDMS);
    }

    public void openDataDDtoDMS() throws Exception {
        getSourceDD();
        rezultsDDtoDMS = rezultDDtoDMSBulk(sourceDD);
    }

    public void openDataDDtoCK42() throws Exception {
        getSourceDD();
        rezultsDDtoCK42 = rezultDDtoCK42Bulk(sourceDD);
    }

    public void openDataCK42toDD() throws Exception {
        getSourceDD();
        rezultsCK42toDD = rezultCK42toDDBulk(sourceDD);
    }

    public void openDataOGZ84() throws Exception {
        openFile();
        getSoursOGZ84();
        status = "OGZ84";
    }

    public void onClickCalculate(ActionEvent actionEvent) {

        switch (status) {
            case ("UNKNOWN"):
                statusBar.setText("Помилка! Відсутні дані для рохрахунку");
                inform.hd = "Помилка! Відсутні дані для рохрахунку";
                inform.ct = " 1. Відкрити файл  даних \n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
                inform.alert();
                statusBar.setText("");
                break;

            case ("Compare"):
                try {
                    List<String> rezultStrings = rezultsOGZ84.stream().map(Ogz84::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headOgz84)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                isCalc = "Compare";
                break;

            case ("OGZ84"):
                try {
                    List<String> rezultStrings = rezultsOGZ84.stream().map(Ogz84::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headOgz84)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                isCalc = "OGZ84";
                break;

            case ("DMStoDD"):
                try {
                    List<String> rezultStrings = rezultsDMStoDD.stream().map(DMStoDD::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headDMStoDD)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер градуси, мінути, секунди в градуси");
                isCalc = "DMStoDD";
                break;

            case ("DDtoDMS"):
                try {
                    List<String> rezultStrings = rezultsDDtoDMS.stream().map(DDtoDMS::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headDMStoDD)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер градуси в градуси, мінути, секунди");
                isCalc = "DDtoDMS";
                break;

            case ("DDtoCK42"):
                try {
                    List<String> rezultStrings = rezultsDDtoCK42.stream().map(DDtoCK42::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headDDtoCK42)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер дані WGS-84 в СК-42");
                isCalc = "DDtoCK42";
                break;

            case ("DMStoCK42"):
                try {
                    List<String> rezultStrings = rezultsDMStoCK42.stream().map(DMStoCK42::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headDDtoCK42)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер дані WGS-84 в СК-42");
                isCalc = "DMStoCK42";
                break;

            case ("CK42toDD"):
                try {
                    List<String> rezultStrings = rezultsCK42toDD.stream().map(CK42toDD::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headCK42toDD)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер СК-42 в WGS-84");
                isCalc = "CK42toDD";
                break;
        }
    }

    public void onClickChart(ActionEvent actionEvent) throws IOException {
        progressIndicator.setVisible(true);
        if (status.equals("UNKNOWN") || isCalc.equals("UNKNOWN")) {
            statusBar.setText("Помилка! Відсутні дані для відображення");
            inform.hd = "Помилка! Відсутні дані для збереження";
            inform.ct = " 1. Відкрити файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Відобразити розраховані дані \n";
            inform.alert();
            progressIndicator.setVisible(false);
            return;
        }
        switch (isCalc) {
            case ("Compare"):
                os.viewURL = "/view/chartComparis.fxml";
                os.title = "Графік GPS   " + openFile;
                os.maximized = false;
                os.openStage();
                break;

            case ("OGZ84"):
            case ("DMStoDD"):
            case ("DDtoDMS"):
            case ("DDtoCK42"):
            case ("CK42toDD"):
                os.viewURL = "/view/chartConverter.fxml";
                os.title = "Графік GPS   " + openFile;
                os.maximized = false;
                os.openStage();
                break;
        }
        progressIndicator.setVisible(false);
    }

    public void onClickOpenFileInDesktop(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        fileChooserRun.openFileChooser();
        desktop.open(selectedOpenFile);
    }

    @SneakyThrows
    public void onClickSave(ActionEvent actionEvent) throws IOException {
        progressIndicatorRun();
        if (status.equals("UNKNOWN") || isCalc.equals("UNKNOWN")) {
//            log.warn("Ogz84 is empty");
            statusBar.setText("Помилка! Відсутні дані для збереження");
            inform.hd = "Помилка! Відсутні дані для збереження";
            inform.ct = " 1. Відкрити файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            progressIndicator.setVisible(false);
//            statusBar.setText("");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.csv", "*.csv"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        //fileChooser.setInitialFileName("Comparis_" + openFile);
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);
        File file = fileChooser.showSaveDialog((new Stage()));

        switch (isCalc) {
            case ("Compare"):
            case ("OGZ84"):
                fileChooser.setInitialFileName(openFile + "_ogz.csv");
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw.write("Результати розрахунку\n");
                osw.write("Джерело 1,,,                           Джерело 2 \n");
                osw.write(headOgz84);
                for (Ogz84 rezult : rezultsOGZ84) {
                    osw.write(rezult.toString());
                }
                osw.close();
                statusBar.setText("Успішно записано в файл  '" + openFile + "_ogz.csv'");
                break;

            case ("DMStoDD"):
                fileChooser.setInitialFileName(openFile + "_grad.csv");
                OutputStreamWriter osw1 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw1.write("Результати розрахунку Градуси, мінути, секунди  в  градуси\n");
                osw1.write("Градуси, мінути, секунди,,      Градуси \n");
                osw1.write(headDMStoDD);
                for (DMStoDD rezult : rezultsDMStoDD) {
                    osw1.write(rezult.toString());
                }
                osw1.close();
                statusBar.setText("Успішно записано в файл  '" + openFile + "_grad.csv");
                break;

            case ("DDtoDMS"):
                fileChooser.setInitialFileName(openFile + "_gms.csv");
                OutputStreamWriter osw2 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw2.write("Результати розрахунку Градуси в градуси, мінути, секунди  \n");
                osw2.write("Градуси,,       Градуси, мінути, секунди \n");
                osw2.write(headDMStoDD);
                for (DDtoDMS rezult : rezultsDDtoDMS) {
                    osw2.write(rezult.toString());
                }
                osw2.close();
                statusBar.setText("Успішно записано в файл  '" + openFile + "_gms.csv");
                break;

            case ("DDtoCK42"):
                fileChooser.setInitialFileName(openFile + "_CK-42.csv");
                OutputStreamWriter osw3 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw3.write("Результати розрахунку WGS-84 - CK-42\n");
                osw3.write("WGS-84,,       CK-42 \n");
                osw3.write(headDDtoCK42);
                for (DDtoCK42 rezult : rezultsDDtoCK42) {
                    osw3.write(rezult.toString());
                }
                osw3.close();
                statusBar.setText("Успішно записано в файл  '" + openFile + "_CK-42.csv");
                break;

            case ("CK42toDD"):
                fileChooser.setInitialFileName(openFile + "_WGS-84.csv");
                OutputStreamWriter osw4 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw4.write("Результати розрахунку  CK-42 - WGS-84\n");
                osw4.write("CK-42,,         WGS-84\n");
                osw4.write(headCK42toDD);
                for (CK42toDD rezult : rezultsCK42toDD) {
                    osw4.write(rezult.toString());
                }
                osw4.close();
                statusBar.setText("Успішно записано в файл  '" + openFile + "_WGS-84.csv");
                break;
        }
        progressIndicator.setVisible(false);
    }

    public void choiceCoordinateConverter() throws Exception {
        if (choiceCoordinateConverter.getValue().equals("ГМС в Градуси")) {
            openFile();
            openDataDMStoDD();
            status = "DMStoDD";
        }
        if (choiceCoordinateConverter.getValue().equals("Градуси в ГМС")) {
            openFile();
            openDataDDtoDMS();
            status = "DDtoDMS";
        }
        if (choiceCoordinateConverter.getValue().equals("WGS-84 в СК-42 (градуси)")) {
            openFile();
            openDataDDtoCK42();
            status = "DDtoCK42";
        }
        if (choiceCoordinateConverter.getValue().equals("WGS-84 в СК-42 (ГМС)")) {
            openFile();
            openDataDMStoCK42();
            status = "DMStoCK42";
        }
        if (choiceCoordinateConverter.getValue().equals("СК-42 в WGS-84")) {
            openFile();
            openDataCK42toDD();
            status = "CK42toDD";
        }
    }

    public void choiceGeoProblem() throws Exception {
        if (choiceGeoProblem.getValue().equals("Порівняння координат")) {
            openFile();
            getSoursOGZ84();
            status = "Compare";
        }
        if (choiceGeoProblem.getValue().equals("Обернена геодезична задача (градуси)")) {
            openFile();
            getSoursOGZ84();
            status = "OGZ84";
        }
    }

    public void onClickDovBtn(ActionEvent actionEvent) throws IOException {
//        String url = "D:/EulerConverter/userManual/UserManual_Euler.pdf";
//        Desktop desktop = Desktop.getDesktop();
//        desktop.open(new File(url));

    }

    public void onClickMenuHAM(ActionEvent actionEvent) throws IOException {
        if (Desktop.isDesktopSupported()) {
//            String url = "D:/EulerConverter/userManual/UserManual_HAM.pdf";
//            Desktop desktop = Desktop.getDesktop();
//            desktop.open(new File(url));
        }
    }

    public void onClick_menuAbout(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/about.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onClickNew(ActionEvent e) {
        outputText.setText("");
        statusBar.setText("");
        statusLabel.setText("Відкрийте файл");
        labelLineCount.setText(" ");
        sourceOGZ84.clear();
        rezultsOGZ84.clear();
        sourceDMS.clear();
        rezultsDMStoDD.clear();
        sourceDD.clear();
        rezultsDDtoDMS.clear();
        rezultsDDtoCK42.clear();
        rezultsCK42toDD.clear();
        progressIndicator.setVisible(false);
        choiceGeoProblem.setValue("Геодезичні задачі");
        choiceCoordinateConverter.setValue("Конвертор координат");
        status = "UNKNOWN";
        isCalc = "UNKNOWN";
    }

    public void onClickLocalZone(ActionEvent event) throws IOException {
        os.viewURL = "/view/Settings.fxml";
        os.title = "Часовий пояс   " + openFile;
        os.isModality = true;
        os.isResizable = false;
        os.openStage();
    }

    public void progressIndicatorRun() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
            statusBar.setText("Зачекайте...");
        });
    }

    public void onClickGoogleEarth(ActionEvent actionEvent) {
    }

    public void onClickGetBlank(ActionEvent actionEvent) throws IOException {
        os.viewURL = "/view/getBlank.fxml";
        os.title = "Отримання бланку для розрахунків";
        os.isModality = true;
        os.isResizable = false;
        os.openStage();
    }

    public void onClickCancelBtn(ActionEvent e) {
        System.exit(0);
    }
}


