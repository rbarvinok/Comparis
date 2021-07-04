package ua.comparis.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.comparis.javaclass.domain.*;
import ua.comparis.javaclass.servisClass.*;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    public static int localZone = 2;
    public int colsInpDate = 0;
    public static String openFile = " ";
    public static String openDirectory;
    public static String status = "UNKNOWN";
    public static String isCalc = "UNKNOWN";
    public static String lineCount;
    public static String headSourceNMEA = "Час, Широта, Довгота, Висота\n";
    public String headOgz84 = "Час, Широта, Довгота, Висота, Час, Широта, Довгота, Висота, " + "Відстань 2D, Відстань 3D, Кут відхилення, Різниця висот \n";
    public String headPgz84 = "Широта, Довгота, Висота, Відстань, Азимут, Широта, Довгота \n";
    public String headDMStoDD = "Широта (ГМС), Довгота (ГМС), Широта (град.), Довгота (град.), Висота, Назва точки  \n";
    public String headDDtoCK42 = "Широта (град.), Довгота (град.), Широта (ГМС), Довгота (ГМС), Висота, X, Y, H, Назва точки \n";
    public String headDMStoCK42 = "Широта (ГМС), Довгота (ГМС), Широта (град.), Довгота (град.), Висота, X, Y, H, Назва точки \n";
    public String headCK42toDD = "X, Y, H, Широта (ГМС), Довгота (ГМС), Широта (град.), Довгота (град.), Висота, Назва точки \n";

    public static List<SourceOGZ84> sourceOGZ84 = new ArrayList<>();
    public static List<SourcePGZ84> sourcePGZ84 = new ArrayList<>();
    public static List<SourceDMS> sourceDMS = new ArrayList<>();
    @Getter
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
    public ObservableList<InputDate> inputDatesList = FXCollections.observableArrayList();

    @FXML
    public TableView outputTable;
    public TextField statusBar;
    public TextField labelLineCount;
    public Label statusLabel;
    public ProgressIndicator progressIndicator;
    public ComboBox<String> choiceGeoProblem, choiceCoordinateConverter;

    public void openFile() {
        //if (outputText.getText().equals("")) {
        statusBar.setText("");
        progressIndicatorRun();

        fileChooserRun.openFileChooser();
        openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 4);
        openDirectory = selectedOpenFile.getParent();
        progressIndicator.setVisible(false);
//        } else {
//            inform.hd = "Файл уже відкритий";
//            inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
//            inform.inform();
//        }
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
                    inputDates(resultsOGZ84);
                    TableColumn<InputDate, String> tTime = new TableColumn<>("Час");
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tTime2 = new TableColumn<>("Час");
                    TableColumn<InputDate, String> tLat2 = new TableColumn<>("Широта");
                    TableColumn<InputDate, String> tLong2 = new TableColumn<>("Довгота");
                    TableColumn<InputDate, String> tAlt2 = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tDist2D = new TableColumn<>("Відстань 2D");
                    TableColumn<InputDate, String> tDist3D = new TableColumn<>("Відстань 3D");
                    TableColumn<InputDate, String> tAngle = new TableColumn<>("Кут відхилення");
                    TableColumn<InputDate, String> tDeltaH = new TableColumn<>("Різниця висот");

                    for (int i = 0; i <= colsInpDate - 12; i++) {
                        final int indexColumn = i;
                        tTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tTime2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tLat2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                        tLong2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
                        tAlt2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
                        tDist2D.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
                        tDist3D.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(9 + indexColumn)));
                        tAngle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(10 + indexColumn)));
                        tDeltaH.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(11 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tTime, tLat, tLong, tAlt, tTime2, tLat2, tLong2, tAlt2, tDist2D, tDist3D, tAngle, tDeltaH);
                    outputTable.setItems(inputDatesList);

                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                isCalc = "Compare";
                break;

            case ("OGZ84"):
                try {
                    inputDates(resultsOGZ84);
                    TableColumn<InputDate, String> tTime = new TableColumn<>("Час");
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tTime2 = new TableColumn<>("Час");
                    TableColumn<InputDate, String> tLat2 = new TableColumn<>("Широта");
                    TableColumn<InputDate, String> tLong2 = new TableColumn<>("Довгота");
                    TableColumn<InputDate, String> tAlt2 = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tDist2D = new TableColumn<>("Відстань 2D");
                    TableColumn<InputDate, String> tDist3D = new TableColumn<>("Відстань 3D");
                    TableColumn<InputDate, String> tAngle = new TableColumn<>("Кут відхилення");
                    TableColumn<InputDate, String> tDeltaH = new TableColumn<>("Різниця висот");

                    for (int i = 0; i <= colsInpDate - 12; i++) {
                        final int indexColumn = i;
                        tTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tTime2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tLat2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                        tLong2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
                        tAlt2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
                        tDist2D.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
                        tDist3D.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(9 + indexColumn)));
                        tAngle.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(10 + indexColumn)));
                        tDeltaH.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(11 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tTime, tLat, tLong, tAlt, tTime2, tLat2, tLong2, tAlt2, tDist2D, tDist3D, tAngle, tDeltaH);
                    outputTable.setItems(inputDatesList);


                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                isCalc = "OGZ84";
                break;

            case ("PGZ84"):
                try {
                    inputDates(resultsPGZ84);
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tDist = new TableColumn<>("Відстань");
                    TableColumn<InputDate, String> tAz = new TableColumn<>("Азимут");
                    TableColumn<InputDate, String> tLat2 = new TableColumn<>("Широта");
                    TableColumn<InputDate, String> tLong2 = new TableColumn<>("Довгота");

                    for (int i = 0; i <= colsInpDate - 7; i++) {
                        final int indexColumn = i;
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tDist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tAz.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tLat2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                        tLong2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tLat, tLong, tAlt, tDist, tAz, tLat2, tLong2);
                    outputTable.setItems(inputDatesList);
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                isCalc = "PGZ84";
                break;

            case ("DMStoDD"):
                try {
                    inputDates(resultsDMStoDD);
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта (ГМС)");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота (ГМС)");
                    TableColumn<InputDate, String> tLatD = new TableColumn<>("Широта (град.)");
                    TableColumn<InputDate, String> tLongD = new TableColumn<>("Довгота (град.)");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

                    for (int i = 0; i <= colsInpDate - 6; i++) {
                        final int indexColumn = i;
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tLatD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tLongD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tLat, tLong, tLatD, tLongD, tAlt, tMemo);
                    outputTable.setItems(inputDatesList);
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер градуси, мінути, секунди в градуси");
                isCalc = "DMStoDD";
                break;

            case ("DDtoDMS"):
                try {
                    inputDates(resultsDDtoDMS);
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта (град.)");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота (град.)");
                    TableColumn<InputDate, String> tLatD = new TableColumn<>("Широта (ГМС)");
                    TableColumn<InputDate, String> tLongD = new TableColumn<>("Довгота (ГМС)");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

                    for (int i = 0; i <= colsInpDate - 6; i++) {
                        final int indexColumn = i;
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tLatD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tLongD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tLat, tLong, tLatD, tLongD, tAlt, tMemo);
                    outputTable.setItems(inputDatesList);
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер градуси в градуси, мінути, секунди");
                isCalc = "DDtoDMS";
                break;

            case ("DDtoCK42"):
                try {
                    inputDates(resultsDDtoCK42);
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта (град.)");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота (град.)");
                    TableColumn<InputDate, String> tLatD = new TableColumn<>("Широта (ГМС)");
                    TableColumn<InputDate, String> tLongD = new TableColumn<>("Довгота (ГМС)");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tX = new TableColumn<>("X");
                    TableColumn<InputDate, String> tY = new TableColumn<>("Y");
                    TableColumn<InputDate, String> tH = new TableColumn<>("H");
                    TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

                    for (int i = 0; i <= colsInpDate - 9; i++) {
                        final int indexColumn = i;
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tLatD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tLongD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tX.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                        tY.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
                        tH.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
                        tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tLat, tLong, tLatD, tLongD, tAlt, tX, tY, tH, tMemo);
                    outputTable.setItems(inputDatesList);
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер дані WGS-84 в СК-42");
                isCalc = "DDtoCK42";
                break;

            case ("DMStoCK42"):
                try {
                    inputDates(resultsDMStoCK42);
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта (ГМС)");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота (ГМС)");
                    TableColumn<InputDate, String> tLatD = new TableColumn<>("Широта (град.)");
                    TableColumn<InputDate, String> tLongD = new TableColumn<>("Довгота (град.)");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tX = new TableColumn<>("X");
                    TableColumn<InputDate, String> tY = new TableColumn<>("Y");
                    TableColumn<InputDate, String> tH = new TableColumn<>("H");
                    TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

                    for (int i = 0; i <= colsInpDate - 9; i++) {
                        final int indexColumn = i;
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tLatD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tLongD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tX.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                        tY.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
                        tH.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
                        tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tLat, tLong, tLatD, tLongD, tAlt, tX, tY, tH, tMemo);
                    outputTable.setItems(inputDatesList);


                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Конвертер дані WGS-84 в СК-42");
                isCalc = "DMStoCK42";
                break;

            case ("CK42toDD"):
                try {
                    inputDates(resultsCK42toDD);
                    TableColumn<InputDate, String> tX = new TableColumn<>("X");
                    TableColumn<InputDate, String> tY = new TableColumn<>("Y");
                    TableColumn<InputDate, String> tH = new TableColumn<>("H");
                    TableColumn<InputDate, String> tLat = new TableColumn<>("Широта (град.)");
                    TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота (град.)");
                    TableColumn<InputDate, String> tLatD = new TableColumn<>("Широта (ГМС)");
                    TableColumn<InputDate, String> tLongD = new TableColumn<>("Довгота (ГМС)");
                    TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
                    TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

                    for (int i = 0; i <= colsInpDate - 9; i++) {
                        final int indexColumn = i;
                        tX.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
                        tY.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
                        tH.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
                        tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
                        tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
                        tLatD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
                        tLongD.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
                        tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
                        tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(8 + indexColumn)));
                    }
                    outputTable.getColumns().addAll(tX, tY, tH, tLat, tLong, tLatD, tLongD, tAlt, tMemo);
                    outputTable.setItems(inputDatesList);
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
    public void onClickSave(ActionEvent actionEvent) {
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
                new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"),
                new FileChooser.ExtensionFilter("*.csv", "*.csv"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        fileChooser.setInitialFileName(openFile + "_result");
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);
        File file = fileChooser.showSaveDialog((new Stage()));

        switch (isCalc) {
            case ("Compare"):
            case ("OGZ84"):
                fileChooser.setInitialFileName(openFile + "_ogz");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_ogz");

                    DataFormat format = book.createDataFormat();
                    CellStyle dateStyle = book.createCellStyle();
                    dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_ogz");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Джерело 1");
                    cell = row.createCell(4, CellType.STRING);
                    cell.setCellValue("Джерело 2");
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headOgz84, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headOgz84.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;

                    for (Ogz84 rezult : resultsOGZ84) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headOgz84, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(rezult.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
                    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                    osw.write(openFile + "_result\n");
                    osw.write("Результати розрахунку\n");
                    osw.write("Джерело 1,,,                           Джерело 2 \n");
                    osw.write(headOgz84);
                    for (Ogz84 rezult : resultsOGZ84) {
                        osw.write(rezult.toString());
                    }
                    osw.close();
                }
                break;

            case ("PGZ84"):
                fileChooser.setInitialFileName(openFile + "_pgz");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_pgz");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_pgz");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Пряма геодезична задача");
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headPgz84, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headPgz84.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;

                    for (Pgz84 rezult : resultsPGZ84) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headPgz84, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(rezult.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
                    OutputStreamWriter oswp = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                    oswp.write(openFile + "_result\n");
                    oswp.write("Результати розрахунку\n");
                    oswp.write("Пряма геодезична задача\n");
                    oswp.write(headPgz84);
                    for (Pgz84 rezult : resultsPGZ84) {
                        oswp.write(rezult.toString());
                    }
                    oswp.close();
                }
                break;

            case ("DMStoDD"):
                fileChooser.setInitialFileName(openFile + "_grad");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_grad");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_grad");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Градуси, мінути, секунди  в  градуси");
                    rownum++;
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headDMStoDD, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headDMStoDD.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;

                    for (DMStoDD result : resultsDMStoDD) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headDMStoDD, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(result.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
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
                }
                break;

            case ("DDtoDMS"):
                fileChooser.setInitialFileName(openFile + "_gms");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_gms");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_gms");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Градуси в градуси, мінути, секунди ");
                    rownum++;
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headDMStoDD, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headDMStoDD.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;
                    for (DDtoDMS result : resultsDDtoDMS) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headDMStoDD, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(result.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
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
                }
                break;

            case ("DDtoCK42"):
                fileChooser.setInitialFileName(openFile + "_CK-42");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_CK-42");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_CK-42");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("WGS-84 - CK-42");
                    rownum++;
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headDDtoCK42, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headDDtoCK42.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;
                    for (DDtoCK42 result : resultsDDtoCK42) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headDDtoCK42, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(result.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
                    OutputStreamWriter osw3 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                    osw3.write(openFile + "_result\n");
                    osw3.write("Результати розрахунку WGS-84 - CK-42\n");
                    osw3.write("WGS-84,,         WGS-84 (ГМС),,,         CK-42 \n");
                    osw3.write(headDDtoCK42);
                    for (DDtoCK42 result : resultsDDtoCK42) {
                        osw3.write(result.toString());
                    }
                    osw3.close();
                }
                break;

            case ("DMStoCK42"):
                fileChooser.setInitialFileName(openFile + "_CK-42");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_CK-42");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_CK-42");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("WGS-84 - CK-42");
                    rownum++;
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headDMStoCK42, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headDMStoCK42.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;
                    for (DMStoCK42 result : resultsDMStoCK42) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headDMStoCK42, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(result.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
                    OutputStreamWriter osw5 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                    osw5.write(openFile + "_result\n");
                    osw5.write("Результати розрахунку WGS-84 - CK-42\n");
                    osw5.write("WGS-84 (ГМС),,          WGS-84 (градуси),,         CK-42 \n");
                    osw5.write(headDMStoCK42);
                    for (DMStoCK42 result : resultsDMStoCK42) {
                        osw5.write(result.toString());
                    }
                    osw5.close();
                }
                break;

            case ("CK42toDD"):
                fileChooser.setInitialFileName(openFile + "_WGS-84");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_WGS-84");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_WGS-84");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Результати розрахунку");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("CK-42 - WGS-84");
                    rownum++;
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headCK42toDD, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headCK42toDD.split(",")[colnum]);
                        sheet.autoSizeColumn(colnum);
                    }
                    rownum++;
                    for (CK42toDD result : resultsCK42toDD) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headCK42toDD, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(result.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
                    OutputStreamWriter osw4 = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                    osw4.write(openFile + "_result\n");
                    osw4.write("Результати розрахунку  CK-42 - WGS-84\n");
                    osw4.write("CK-42,,         WGS-84\n");
                    osw4.write(headCK42toDD);
                    for (CK42toDD result : resultsCK42toDD) {
                        osw4.write(result.toString());
                    }
                    osw4.close();
                }
                break;

            case ("NMEA"):
                fileChooser.setInitialFileName(openFile + "_nmea.csv");
                //-------------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.xlsx")) {
                    Workbook book = new XSSFWorkbook();
                    Sheet sheet = book.createSheet(openFile + "_WGS-84");

                    int rownum = 0;
                    Cell cell = null;
                    Row row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(openFile + "_result");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Вилучення даних з файлу NMEA-0183");
                    rownum++;
                    row = sheet.createRow(rownum);
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue("Часовий пояс - GMT+" + localZone);
                    rownum++;
                    row = sheet.createRow(rownum);

                    for (int colnum = 0; colnum <= StringUtils.countMatches(headSourceNMEA, ","); colnum++) {
                        cell = row.createCell(colnum, CellType.STRING);
                        cell.setCellValue(headSourceNMEA.split(",")[colnum]);
                        //sheet.autoSizeColumn(colnum);
                    }
                    rownum++;
                    for (NMEA result : resultsNMEA) {
                        row = sheet.createRow(rownum);
                        for (int colnum = 0; colnum <= StringUtils.countMatches(headSourceNMEA, ","); colnum++) {
                            cell = row.createCell(colnum, CellType.STRING);
                            cell.setCellValue(result.toString().split(",")[colnum]);
                            sheet.autoSizeColumn(colnum);
                        }
                        rownum++;
                    }

                    FileOutputStream outFile = new FileOutputStream(file);
                    book.write(outFile);
                    outFile.close();
                }
//------------------------
                if (fileChooser.getSelectedExtensionFilter().getDescription().equals("*.csv")) {
                    OutputStreamWriter oswnmea = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
                    oswnmea.write(openFile + "_result\n");
                    oswnmea.write("Вилучення даних з файлу NMEA-0183\n");
                    oswnmea.write("Часовий пояс - GMT+" + localZone + "\n");
                    oswnmea.write(headSourceNMEA);
                    for (NMEA result : resultsNMEA) {
                        oswnmea.write(result.toString());
                    }
                    oswnmea.close();
                }
                break;
        }
        progressIndicator.setVisible(false);
        statusBar.setText("Успішно записано в файл  '" + openFile + "_result.csv");
    }

    public void getSourceOGZ84() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        if (fileChooserRun.extensionFilter.equals("*.xlsx")) {
            FileInputStream inputStream = new FileInputStream(String.valueOf(selectedOpenFile));
            openFile = selectedOpenFile.getName().substring(0, selectedOpenFile.getName().length() - 5);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Row row = null;
            //Cell cell;
            Iterator<Row> rowIterator = sheet.iterator();
            int rownum = 0;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                rownum++;
                if (rownum >= 4) {

                    SourceOGZ84 source = new SourceOGZ84(
                            sdf.format(row.getCell(0).getDateCellValue()),
                            String.valueOf(row.getCell(1).getNumericCellValue()),
                            String.valueOf(row.getCell(2).getNumericCellValue()),
                            String.valueOf(row.getCell(3).getNumericCellValue()),
                            sdf.format(row.getCell(4).getDateCellValue()),
                            String.valueOf(row.getCell(5).getNumericCellValue()),
                            String.valueOf(row.getCell(6).getNumericCellValue()),
                            String.valueOf(row.getCell(7).getNumericCellValue()));
                    sourceOGZ84.add(source);
                }
            }
            resultsOGZ84 = rezultOGZ84Bulk(sourceOGZ84);
            inputStream.close();
            lineCount = String.valueOf(rownum - 4);
        }
        //---------------------------------------------------------
        if (fileChooserRun.extensionFilter.equals("*.csv")) {
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
            lineCount = String.valueOf(lineNumber - 4);
        }
        //output to Table----------------------------------------
        inputDates(sourceOGZ84);
        TableColumn<InputDate, String> tTime = new TableColumn<>("Час");
        TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
        TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
        TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
        TableColumn<InputDate, String> tTime2 = new TableColumn<>("Час");
        TableColumn<InputDate, String> tLat2 = new TableColumn<>("Широта");
        TableColumn<InputDate, String> tLong2 = new TableColumn<>("Довгота");
        TableColumn<InputDate, String> tAlt2 = new TableColumn<>("Висота");

        for (int i = 0; i <= colsInpDate - 8; i++) {
            final int indexColumn = i;
            tTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
            tTime2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
            tLong2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(5 + indexColumn)));
            tLat2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(6 + indexColumn)));
            tAlt2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(7 + indexColumn)));
        }
        outputTable.getColumns().addAll(tTime, tLat, tLong, tAlt, tTime2, tLat2, tLong2, tAlt2);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------
        labelLineCount.setText("Cтрок:  " + lineCount);
        getSettings.getSettings();
        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void getSourcePGZ84() throws Exception {
        if (fileChooserRun.extensionFilter.equals("*.xlsx")) {
            FileInputStream inputStream = new FileInputStream(String.valueOf(selectedOpenFile));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Row row = null;
            //Cell cell;
            Iterator<Row> rowIterator = sheet.iterator();
            int rownum = 0;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                rownum++;
                if (rownum >= 4) {

                    SourcePGZ84 source = new SourcePGZ84(
                            row.getCell(0).getNumericCellValue(),
                            row.getCell(1).getNumericCellValue(),
                            row.getCell(2).getNumericCellValue(),
                            row.getCell(3).getNumericCellValue(),
                            row.getCell(4).getNumericCellValue());
                    sourcePGZ84.add(source);
                }
            }
            resultsPGZ84 = rezultPGZ84Bulk(sourcePGZ84);
            inputStream.close();
            lineCount = String.valueOf(rownum - 3);
        }
        //---------------------------------------------------------
        if (fileChooserRun.extensionFilter.equals("*.csv")) {
            FileReader fileReader = new FileReader(selectedOpenFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int lineNumber = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replaceAll(",", ".");
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
            lineCount = String.valueOf(lineNumber - 3);
        }
        //output to Table----------------------------------------
        inputDates(sourcePGZ84);
        TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
        TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
        TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
        TableColumn<InputDate, String> tDist = new TableColumn<>("Відстань");
        TableColumn<InputDate, String> tAz = new TableColumn<>("Азимут");

        for (int i = 0; i <= colsInpDate - 5; i++) {
            final int indexColumn = i;
            tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            tDist.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
            tAz.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(4 + indexColumn)));
        }
        outputTable.getColumns().addAll(tLat, tLong, tAlt, tDist, tAz);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------
        labelLineCount.setText("Cтрок:  " + lineCount);
        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void getSourceDMS() throws IOException {
        if (fileChooserRun.extensionFilter.equals("*.xlsx")) {
            FileInputStream inputStream = new FileInputStream(String.valueOf(selectedOpenFile));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Row row = null;
            //Cell cell;
            Iterator<Row> rowIterator = sheet.iterator();
            int rownum = 0;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                rownum++;
                if (rownum >= 4) {
                    //System.out.println(row.getCell(0).getStringCellValue().replace("'","°").split("°")[1]);
                    SourceDMS source = new SourceDMS(
                            Integer.parseInt(String.valueOf(row.getCell(0).getStringCellValue().split("°")[0])),
                            Integer.parseInt(String.valueOf(row.getCell(0).getStringCellValue().replace("'", "°").split("°")[1])),
                            Double.parseDouble(String.valueOf(row.getCell(0).getStringCellValue()
                                    .replaceAll("\"", "")
                                    .replaceAll(",", ".")
                                    .split("'")[1])),
                            Integer.parseInt(String.valueOf(row.getCell(1).getStringCellValue().split("°")[0])),
                            Integer.parseInt(String.valueOf(row.getCell(1).getStringCellValue().replace("'", "°").split("°")[1])),
                            Double.parseDouble(String.valueOf(row.getCell(1).getStringCellValue()
                                    .replaceAll("\"", "")
                                    .replaceAll(",", ".")
                                    .split("'")[1])),
                            row.getCell(2).getNumericCellValue(),
                            row.getCell(3).getStringCellValue()
                    );
                    sourceDMS.add(source);
                }
            }
            inputStream.close();
            lineCount = String.valueOf(rownum - 3);
        }

        //---------------------------------------------------
        if (fileChooserRun.extensionFilter.equals("*.csv")) {
            FileReader fileReader = new FileReader(selectedOpenFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int lineNumber = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line
                        .replaceAll(",", ".")
                        .replaceAll("°", ";")
                        .replaceAll("'", ";")
                        .replaceAll("\"", ";");

                line = line.replaceAll(";", ",");
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
                        Double.parseDouble(split[14]),
                        split[15]
                );
                sourceDMS.add(source);
            }
            fileReader.close();
            lineCount = String.valueOf(lineNumber - 3);
        }

        //output to Table----------------------------------------
        inputDates(sourceDMS);
        TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
        TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
        TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
        TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

        for (int i = 0; i <= colsInpDate - 4; i++) {
            final int indexColumn = i;
            tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
        }
        outputTable.getColumns().addAll(tLat, tLong, tAlt, tMemo);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------
        labelLineCount.setText("Cтрок:  " + lineCount);
        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
    }

    public void getSourceDD() throws IOException {
        if (fileChooserRun.extensionFilter.equals("*.xlsx")) {
            FileInputStream inputStream = new FileInputStream(String.valueOf(selectedOpenFile));
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Row row = null;
            //Cell cell;

            Iterator<Row> rowIterator = sheet.iterator();
            int rownum = 0;

            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                rownum++;
//                Iterator<Cell> cellIterator = row.cellIterator();
                if (rownum <= 5) {
                    rownum++;
                    continue;
                }
                SourceDD source = new SourceDD(
                        row.getCell(0).getNumericCellValue(),
                        row.getCell(1).getNumericCellValue(),
                        row.getCell(2).getNumericCellValue(),
                        row.getCell(3).getStringCellValue()
                );
                sourceDD.add(source);

                lineCount = String.valueOf(rownum - 6);
            }
            inputStream.close();
        }
        //---------------------------------------------------------
        if (fileChooserRun.extensionFilter.equals("*.csv")) {
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
                        Double.parseDouble(split[2]),
                        split[3]
                );
                sourceDD.add(source);
            }
            lineCount = String.valueOf(lineNumber - 3);
            fileReader.close();
        }
        //output to Table----------------------------------------
        inputDates(sourceDD);
        TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
        TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
        TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");
        TableColumn<InputDate, String> tMemo = new TableColumn<>("Назва точки");

        for (int i = 0; i <= colsInpDate - 4; i++) {
            final int indexColumn = i;
            tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            tMemo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
        }
        outputTable.getColumns().addAll(tLat, tLong, tAlt, tMemo);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------
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
        //output to Table----------------------------------------
        inputDates(resultsNMEA);
        TableColumn<InputDate, String> tTime = new TableColumn<>("Час");
        TableColumn<InputDate, String> tLong = new TableColumn<>("Довгота");
        TableColumn<InputDate, String> tLat = new TableColumn<>("Широта");
        TableColumn<InputDate, String> tAlt = new TableColumn<>("Висота");

        for (int i = 0; i <= colsInpDate - 4; i++) {
            final int indexColumn = i;
            tTime.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(0 + indexColumn)));
            tLat.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(1 + indexColumn)));
            tLong.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(2 + indexColumn)));
            tAlt.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItems().get(3 + indexColumn)));
        }
        outputTable.getColumns().addAll(tTime, tLat, tLong, tAlt);
        outputTable.setItems(inputDatesList);
        //--------------------------------------------------------
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

    public void inputDates(List source) {
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
        outputTable.setEditable(false);
        int rowsInpDate = 0;
        String line;
        String csvSplitBy = ",";
        for (int j = 0; j < source.size(); j++) {
            line = source.get(j).toString();
            line = line.replace("[", "").replace("]", "");
            rowsInpDate += 1;
            String[] fields = line.split(csvSplitBy, -1);
            colsInpDate = fields.length;
            InputDate inpd = new InputDate(fields);
            inputDatesList.add(inpd);
        }
    }

    public void onClickDovBtn(ActionEvent actionEvent) {
//        String url = "D:/EulerConverter/userManual/UserManual_Euler.pdf";
//        Desktop desktop = Desktop.getDesktop();
//        desktop.open(new File(url));
    }

    public void onClick_menuAbout(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/about.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onClickNew(ActionEvent e) {
        outputTable.getColumns().clear();
        outputTable.getItems().clear();
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

    public void onClickLocalZone(ActionEvent event) throws IOException {
        os.viewURL = "/view/settings.fxml";
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

    public void onClickOpenFileInDesktop(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        fileChooserRun.openFileChooser();
        desktop.open(selectedOpenFile);
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