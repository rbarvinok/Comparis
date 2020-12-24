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
import ua.comparis.javaclass.geo.AccuracyCalculator;
import ua.comparis.javaclass.servisClass.AlertAndInform;
import ua.comparis.javaclass.servisClass.FileChooserRun;
import ua.comparis.javaclass.servisClass.GetSettings;
import ua.comparis.javaclass.servisClass.OpenStage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.comparis.controller.AccuracyController.*;
import static ua.comparis.javaclass.geo.ConverterCoordinateSystem.*;
import static ua.comparis.javaclass.geo.DDtoDMSConverter.resultDDtoDMSBulk;
import static ua.comparis.javaclass.geo.DMStoDDConverter.rezultDMStoDDBulk;
import static ua.comparis.javaclass.geo.NMEAParser.resultNMEABulk;
import static ua.comparis.javaclass.geo.OgzWGS84Calculator.rezultOGZ84Bulk;
import static ua.comparis.javaclass.geo.PgzWGS84Calculator.rezultPGZ84Bulk;
import static ua.comparis.javaclass.servisClass.FileChooserRun.selectedOpenFile;

@Slf4j
public class Controller {
    static AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();
    AccuracyController accuracyController = new AccuracyController();
    AccuracyCalculator accuracyCalculator = new AccuracyCalculator();

    public static String urlComparis = "D:/Comparis";
    public static int localZone = 2;
    public static String openFile = " ";
    public static String openDirectory;
    public static String status = "UNKNOWN";
    public static String isCalc = "UNKNOWN";
    public static String lineCount;
    public String headSourceOGZ84 = "Час,    Широта,    Довгота,    Висота,    Час,    Широта,    Довгота,    Висота\n";
    public static String headSourceNMEA = "Час,    Широта,    Довгота,    Висота\n";
    public String headSourcePGZ84 = "Широта,      Довгота,      Висота,            Відстань,      Азимут \n";
    public String headOgz84 = "Час,    Широта,    Довгота,    Висота,    Час,     Широта,    Довгота,    Висота,    " + "Відстань 2D,    Відстань 3D,   Кут відхилення,   Різниця висот \n";
    public String headPgz84 = "Широта,      Довгота,      Висота,        Відстань,     Азимут,       Широта,     Довгота \n";
    public String headDMStoDD = "Широта (ГМС),         Довгота (ГМС),                Широта (град.),          Довгота (град.),          Висота   \n";
    public String headDDtoCK42 = "Широта (град.),         Довгота (град.),                Широта (ГМС),          Довгота (ГМС),          Висота  ,          X,        Y,      H  \n";
    public String headDMStoCK42 = "Широта (ГМС),         Довгота (ГМС),                Широта (град.),          Довгота (град.),          Висота  ,              X,        Y,      H  \n";
    public String headCK42toDD = "X,        Y,      H,         Широта (ГМС),         Довгота (ГМС),                Широта (град.),          Довгота (град.),          Висота  \n";

    public static List<SourceOGZ84> sourceOGZ84 = new ArrayList<>();
    public static List<SourcePGZ84> sourcePGZ84 = new ArrayList<>();
    public static List<SourceDMS> sourceDMS = new ArrayList<>();
    public static List<SourceDD> sourceDD = new ArrayList<>();
    public static List<SourceNMEA> sourceNMEA = new ArrayList<>();
    public static List<Ogz84> resultsOGZ84 = new ArrayList<>();
    public static List<Pgz84> resultsPGZ84 = new ArrayList<>();
    public static List<DMStoDD> resultsDMStoDD = new ArrayList<>();
    public static List<DDtoDMS> resultsDDtoDMS = new ArrayList<>();
    public static List<DMStoCK42> resultsDMStoCK42 = new ArrayList<>();
    public static List<DDtoCK42> resultsDDtoCK42 = new ArrayList<>();
    public static List<CK42toDD> resultsCK42toDD = new ArrayList<>();
    public static List<NMEA> resultsNMEA = new ArrayList<>();
    public static List<Accuracy> resultsAccuracy = new ArrayList<Accuracy>();

    @FXML
    public TextArea outputText;
    public TextField statusBar;
    public TextField labelLineCount;
    public Label statusLabel;
    public ProgressIndicator progressIndicator;
    public ComboBox<String> choiceGeoProblem, choiceCoordinateConverter;

    public void openFile() {
        if (outputText.getText().equals("")) {
            statusBar.setText("");
            progressIndicatorRun();

            fileChooserRun.openFileChooser();
            //openFile = selectedOpenFile.getName();
            openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
            openDirectory = selectedOpenFile.getParent();
            progressIndicator.setVisible(false);
        } else {
            inform.hd = "Файл уже відкритий";
            inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
            inform.inform();
        }
    }

    public void onClickCalculate( ActionEvent actionEvent ) {

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
                    List<String> rezultStrings = resultsOGZ84.stream().map(Ogz84::toString).collect(Collectors.toList());
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
                    List<String> rezultStrings = resultsOGZ84.stream().map(Ogz84::toString).collect(Collectors.toList());
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

            case ("PGZ84"):
                try {
                    List<String> rezultStrings = resultsPGZ84.stream().map(Pgz84::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headPgz84)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                isCalc = "PGZ84";
                break;

            case ("DMStoDD"):
                try {
                    List<String> rezultStrings = resultsDMStoDD.stream().map(DMStoDD::toString).collect(Collectors.toList());
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
                    List<String> rezultStrings = resultsDDtoDMS.stream().map(DDtoDMS::toString).collect(Collectors.toList());
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
                    List<String> rezultStrings = resultsDDtoCK42.stream().map(DDtoCK42::toString).collect(Collectors.toList());
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
                    List<String> rezultStrings = resultsDMStoCK42.stream().map(DMStoCK42::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headDMStoCK42)
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
                    List<String> rezultStrings = resultsCK42toDD.stream().map(CK42toDD::toString).collect(Collectors.toList());
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

            case ("accuracy"):
                accuracyCalculator.accuracyLat();
                statusBar.setText(openFile);
                statusLabel.setText("Розрахунок точності визначення координат");
                isCalc = "accuracy";
                break;
        }
    }

    public void onClickChart( ActionEvent actionEvent ) throws IOException {
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
            case ("PGZ84"):
            case ("DMStoDD"):
            case ("DDtoDMS"):
            case ("DDtoCK42"):
            case ("DMStoCK42"):
            case ("CK42toDD"):
            case ("NMEA"):
                os.viewURL = "/view/chartConverter.fxml";
                os.title = "Графік GPS   " + openFile;
                os.maximized = false;
                os.openStage();
                break;
        }
        progressIndicator.setVisible(false);
    }

    @SneakyThrows
    public void onClickSave( ActionEvent actionEvent ) {
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
        fileChooser.setInitialFileName(openFile + "_result.csv");
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);
        File file = fileChooser.showSaveDialog((new Stage()));

        switch (isCalc) {
            case ("Compare"):
            case ("OGZ84"):
                fileChooser.setInitialFileName(openFile + "_ogz.csv");
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw.write(openFile + "_result\n");
                osw.write("Результати розрахунку\n");
                osw.write("Джерело 1,,,                           Джерело 2 \n");
                osw.write(headOgz84);
                for (Ogz84 rezult : resultsOGZ84) {
                    osw.write(rezult.toString());
                }
                osw.close();
                break;

            case ("PGZ84"):
                fileChooser.setInitialFileName(openFile + "_pgz.csv");
                OutputStreamWriter oswp = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                oswp.write(openFile + "_result\n");
                oswp.write("Результати розрахунку\n");
                oswp.write("Пряма геодезична задача\n");
                oswp.write(headPgz84);
                for (Pgz84 rezult : resultsPGZ84) {
                    oswp.write(rezult.toString());
                }
                oswp.close();
                break;

            case ("DMStoDD"):
                fileChooser.setInitialFileName(openFile + "_grad.csv");
                OutputStreamWriter osw1 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw1.write(openFile + "_result\n");
                osw1.write("Результати розрахунку Градуси, мінути, секунди  в  градуси\n");
                osw1.write("Градуси, мінути, секунди,,      Градуси \n");
                osw1.write(headDMStoDD);
                for (DMStoDD result : resultsDMStoDD) {
                    osw1.write(result.toString());
                }
                osw1.close();
                break;

            case ("DDtoDMS"):
                fileChooser.setInitialFileName(openFile + "_gms.csv");
                OutputStreamWriter osw2 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw2.write(openFile + "_result\n");
                osw2.write("Результати розрахунку Градуси в градуси, мінути, секунди  \n");
                osw2.write("Градуси,,       Градуси, мінути, секунди \n");
                osw2.write(headDMStoDD);
                for (DDtoDMS rezult : resultsDDtoDMS) {
                    osw2.write(rezult.toString());
                }
                osw2.close();
                break;

            case ("DDtoCK42"):
                fileChooser.setInitialFileName(openFile + "_CK-42.csv");
                OutputStreamWriter osw3 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw3.write(openFile + "_result\n");
                osw3.write("Результати розрахунку WGS-84 - CK-42\n");
                osw3.write("WGS-84,,         WGS-84 (ГМС),,,         CK-42 \n");
                osw3.write(headDDtoCK42);
                for (DDtoCK42 result : resultsDDtoCK42) {
                    osw3.write(result.toString());
                }
                osw3.close();
                break;

            case ("DMStoCK42"):
                fileChooser.setInitialFileName(openFile + "_CK-42.csv");
                OutputStreamWriter osw5 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw5.write(openFile + "_result\n");
                osw5.write("Результати розрахунку WGS-84 - CK-42\n");
                osw5.write("WGS-84 (ГМС),,          WGS-84 (градуси),,         CK-42 \n");
                osw5.write(headDMStoCK42);
                for (DMStoCK42 result : resultsDMStoCK42) {
                    osw5.write(result.toString());
                }
                osw5.close();
                break;

            case ("CK42toDD"):
                fileChooser.setInitialFileName(openFile + "_WGS-84.csv");
                OutputStreamWriter osw4 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                osw4.write(openFile + "_result\n");
                osw4.write("Результати розрахунку  CK-42 - WGS-84\n");
                osw4.write("CK-42,,         WGS-84\n");
                osw4.write(headCK42toDD);
                for (CK42toDD result : resultsCK42toDD) {
                    osw4.write(result.toString());
                }
                osw4.close();
                break;

            case ("NMEA"):
                fileChooser.setInitialFileName(openFile + "_nmea.csv");
                OutputStreamWriter oswnmea = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                oswnmea.write(openFile + "_result\n");
                oswnmea.write("Вилучення даних з файлу NMEA-0183\n");
                oswnmea.write("Часовий пояс - GMT+" + localZone + "\n");
                oswnmea.write(headSourceNMEA);
                for (NMEA result : resultsNMEA) {
                    oswnmea.write(result.toString());
                }
                oswnmea.close();
                break;
        }
        progressIndicator.setVisible(false);
        statusBar.setText("Успішно записано в файл  '" + openFile + "_result.csv");
    }

    public void getSourceOGZ84() throws Exception {
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
        resultsOGZ84 = rezultOGZ84Bulk(sourceOGZ84);

        fileReader.close();

        List<String> soursStrings = sourceOGZ84.stream().map(SourceOGZ84::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);
        outputText.setText(String.valueOf(new StringBuilder()
                .append(headSourceOGZ84)
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber - 4);
        labelLineCount.setText("Cтрок:  " + lineCount);

        getSettings.getSettings();

        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void getSourcePGZ84() throws Exception {
        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            //line = line.replaceAll(";", ",");
            line = line.replaceAll(",", ".");


            //String[] split = line.split(",");
            String[] split = line.split(";");

            if (split.length <= 3 || lineNumber < 4) {
                lineNumber++;
                continue;
            }

            lineNumber++;

            SourcePGZ84 source = new SourcePGZ84(
                    Double.parseDouble(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Double.parseDouble(split[3]),
                    Double.parseDouble(split[4]));
            sourcePGZ84.add(source);
        }
        resultsPGZ84 = rezultPGZ84Bulk(sourcePGZ84);

        fileReader.close();

        List<String> soursStrings = sourcePGZ84.stream().map(SourcePGZ84::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);
        outputText.setText(String.valueOf(new StringBuilder()
                .append(headSourcePGZ84)
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber - 4);
        labelLineCount.setText("Cтрок:  " + lineCount);

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
                    Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]),
                    Double.parseDouble(split[3]),
                    Integer.parseInt(split[8]),
                    Integer.parseInt(split[9]),
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

    public void getSoursNMEA() throws IOException {
        openFile();
        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            //line = line.replaceAll(";", ",");

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
        resultsNMEA = resultNMEABulk(sourceNMEA);

        fileReader.close();

        List<String> soursStrings = resultsNMEA.stream().map(NMEA::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);
        outputText.setText(String.valueOf(new StringBuilder()
                .append(headSourceNMEA)
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber);
        labelLineCount.setText("Cтрок:  " + lineCount);

        getSettings.getSettings();

        statusLabel.setText("Вилучені дані з файлу NMEA-0183");
        statusBar.setText(openFile);
        status = "NMEA";
        isCalc = "NMEA";
    }

    public void openDataDMStoDD() throws Exception {
        openFile();
        getSourceDMS();
        resultsDMStoDD = rezultDMStoDDBulk(sourceDMS);
        status = "DMStoDD";
    }

    public void openDataCompare() throws Exception {
        openFile();
        getSourceOGZ84();
        status = "Compare";
    }

    public void openDataOGZWGS84() throws Exception {
        openFile();
        getSourceOGZ84();
        status = "OGZ84";
    }

    public void openDataDMStoCK42() throws Exception {
        openFile();
        getSourceDMS();
        resultsDMStoCK42 = rezultDMStoCK42Bulk(sourceDMS);
        status = "DMStoCK42";
    }

    public void openDataDDtoDMS() throws Exception {
        openFile();
        getSourceDD();
        resultsDDtoDMS = resultDDtoDMSBulk(sourceDD);
        status = "DDtoDMS";
    }

    public void openDataDDtoCK42() throws Exception {
        openFile();
        getSourceDD();
        resultsDDtoCK42 = rezultDDtoCK42Bulk(sourceDD);
        status = "DDtoCK42";
    }

    public void openDataCK42toDD() throws Exception {
        openFile();
        getSourceDD();
        resultsCK42toDD = rezultCK42toDDBulk(sourceDD);
        status = "CK42toDD";
    }

    public void openDataOGZ84() throws Exception {
        openFile();
        getSourceOGZ84();
        status = "OGZ84";
    }

    public void openDataPGZ84() throws Exception {
        openFile();
        getSourcePGZ84();
        status = "PGZ84";
    }

    public void choiceCoordinateConverter() throws Exception {
        if (choiceCoordinateConverter.getValue().equals("ГМС в Градуси")) {
            openDataDMStoDD();
        }
        if (choiceCoordinateConverter.getValue().equals("Градуси в ГМС")) {
            openDataDDtoDMS();
        }
        if (choiceCoordinateConverter.getValue().equals("WGS-84 в СК-42 (градуси)")) {
            openDataDDtoCK42();
        }
        if (choiceCoordinateConverter.getValue().equals("WGS-84 в СК-42 (ГМС)")) {
            openDataDMStoCK42();
        }
        if (choiceCoordinateConverter.getValue().equals("СК-42 в WGS-84")) {
            openDataCK42toDD();
        }
    }

    public void choiceGeoProblem() throws Exception {
        if (choiceGeoProblem.getValue().equals("Порівняння координат")) {
            openDataCompare();

        }
        if (choiceGeoProblem.getValue().equals("Обернена геодезична задача (градуси)")) {
            openDataOGZWGS84();
        }
        if (choiceGeoProblem.getValue().equals("Пряма геодезична задача (градуси)")) {
            openDataPGZ84();
        }
    }

    public void onClickAccuracy( ActionEvent actionEvent ) throws IOException, InterruptedException {
        os.viewURL = "/view/accuracy.fxml";
        os.title = "Точність визначення координат   " + openFile;
        os.isModality = true;
        os.isResizable = false;
        os.openStage();
        //progressIndicatorRun();
//-------------------------------------------
//        Timer timer = new Timer();
//        while (isAccuracySource = false) {
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    accuracyController.getSours();
//                }
//            }, 1000, 1000);
//            Thread.sleep(1000L);
//            //accuracyController.getSours();
//        }
//        if (isAccuracySource = true) {
//            timer.cancel();
//    }
//------------------------------------------
//        while (isAccuracySource = false) {
//            Timer timer = new Timer();
//            timer.wait(1000);
//            //TimeUnit.SECONDS.sleep(1);
//            accuracyController.getSours();
//        }
//------------------------------------------------
        Thread thread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (isAccuracySource = false) {
                    //TimeUnit.SECONDS.sleep(1);
                    Thread.sleep(1000L);
                    accuracyController.getSours();
                }
            }
        });
//-------------------------------------------
        if (accuracyController.sourceType.equals("NMEA")) {
            resultsNMEA = resultNMEABulk(sourceNMEA);
            List<String> soursStrings = resultsNMEA.stream().map(NMEA::toString).collect(Collectors.toList());
            String textForTextArea = String.join("", soursStrings);
            outputText.setText(String.valueOf(new StringBuilder()
                    .append("Координати контрольної точки\n")
                    .append(trueLat + "    " + trueLong + "     " + trueAlt + "\n")
                    .append("Часовий пояс: GMT+" + localZone + "\n")
                    .append("Координати визначених точок\n")
                    .append(headSourceNMEA)
                    .append(textForTextArea)));
        }

        if (accuracyController.sourceType.equals("DD")) {
            List<String> soursStrings = sourceDD.stream().map(SourceDD::toString).collect(Collectors.toList());
            String textForTextArea = String.join("", soursStrings);
            outputText.setText(String.valueOf(new StringBuilder()
                    .append("Координати контрольної точки\n")
                    .append(trueLat + "    " + trueLong + "     " + trueAlt + "\n")
                    .append("Часовий пояс: GMT+" + localZone + "\n")
                    .append("Координати визначених точок\n")
                    .append("Широта,       Довгота,      Висота \n")
                    .append(textForTextArea)));
        }

        status = "accuracy";
        labelLineCount.setText(String.valueOf(count));
        progressIndicator.setVisible(false);
        statusBar.setText(openFile);
    }

    public void onClickDovBtn( ActionEvent actionEvent ) {
//        String url = "D:/EulerConverter/userManual/UserManual_Euler.pdf";
//        Desktop desktop = Desktop.getDesktop();
//        desktop.open(new File(url));

    }

    public void onClick_menuAbout( ActionEvent actionEvent ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/about.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onClickNew( ActionEvent e ) {
        outputText.setText("");
        statusBar.setText("");
        statusLabel.setText("Відкрийте файл");
        labelLineCount.setText(" ");
        sourceOGZ84.clear();
        sourcePGZ84.clear();
        resultsOGZ84.clear();
        resultsPGZ84.clear();
        sourceDMS.clear();
        resultsDMStoDD.clear();
        sourceDD.clear();
        sourceNMEA.clear();
        resultsDDtoDMS.clear();
        resultsNMEA.clear();
        resultsDDtoCK42.clear();
        resultsCK42toDD.clear();
        progressIndicator.setVisible(false);
        choiceGeoProblem.setValue("Геодезичні задачі");
        choiceCoordinateConverter.setValue("Конвертор координат");
        status = "UNKNOWN";
        isCalc = "UNKNOWN";
    }

    public void onClickLocalZone( ActionEvent event ) throws IOException {
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

    public void onClickOpenFileInDesktop( ActionEvent actionEvent ) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        fileChooserRun.openFileChooser();
        desktop.open(selectedOpenFile);
    }

    public void onClickGetBlank( ActionEvent actionEvent ) throws IOException {
        os.viewURL = "/view/getBlank.fxml";
        os.title = "Отримання бланку для розрахунків";
        os.isModality = true;
        os.isResizable = false;
        os.openStage();
    }

    public void onClickCancelBtn( ActionEvent e ) {
        System.exit(0);
    }


}


