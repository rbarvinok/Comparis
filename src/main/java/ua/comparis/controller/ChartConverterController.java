package ua.comparis.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ua.comparis.javaclass.domain.*;
import ua.comparis.javaclass.servisClass.OpenStage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.comparis.controller.Controller.isCalc;
import static ua.comparis.controller.Controller.openFile;


public class ChartConverterController implements Initializable {
    OpenStage os = new OpenStage();
    @FXML
    public LineChart lineChart;

    @FXML
    public Button scatterChartButton;
    public static ObservableList<XYChart.Data> gps;
    public static ObservableList<XYChart.Data> gps1;
    public static ObservableList<XYChart.Data> gps2;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {

        NumberAxis x = new NumberAxis();
        x.setAutoRanging(false);
        x.setForceZeroInRange(false);

        NumberAxis y = new NumberAxis();
        y.setAutoRanging(false);
        y.setForceZeroInRange(false);

        LineChart<Number, Number> lccGPS = new LineChart<Number, Number>(x, y);
        lccGPS.setTitle("Графік GPS " + openFile);
        x.setLabel("Latitude");
        y.setLabel("Longitude");

        if (isCalc == "DMStoDD") {
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Координати");
            getChartDataDMStoDD();
            series1.setData(gps);
            lineChart.getData().addAll(series1);
        }

        if (isCalc == "DDtoDMS") {
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Координати");
            getChartDataDDtoDMS();
            series2.setData(gps);
            lineChart.getData().addAll(series2);
        }

        if (isCalc == "DDtoCK42") {
            XYChart.Series seriesCK42 = new XYChart.Series();
            seriesCK42.setName("Координати");
            getChartDataDDtoCK42();
            seriesCK42.setData(gps);
            lineChart.getData().addAll(seriesCK42);
        }

        if (isCalc == "DMStoCK42") {
            XYChart.Series seriesDMStoCK42 = new XYChart.Series();
            seriesDMStoCK42.setName("Координати");
            getChartDataDMStoCK42();
            seriesDMStoCK42.setData(gps);
            lineChart.getData().addAll(seriesDMStoCK42);
        }

        if (isCalc == "NMEA") {
            XYChart.Series seriesNMEA = new XYChart.Series();
            seriesNMEA.setName("Координати");
            getChartDataNMEA();
            seriesNMEA.setData(gps);
            lineChart.getData().addAll(seriesNMEA);
        }

        if (isCalc == "CK42toDD") {
            XYChart.Series seriesWGS = new XYChart.Series();
            seriesWGS.setName("Координати");
            getChartDataCK42toDD();
            seriesWGS.setData(gps);
            lineChart.getData().addAll(seriesWGS);
        }

        if (isCalc == "OGZ84") {
            XYChart.Series seriesOGZ1 = new XYChart.Series();
            seriesOGZ1.setName("Набір даних 1");
            XYChart.Series seriesOGZ2 = new XYChart.Series();
            seriesOGZ2.setName("Набір даних 2");
            getChartDataOGZ();
            seriesOGZ1.setData(gps1);
            seriesOGZ2.setData(gps2);
            lineChart.getData().addAll(seriesOGZ1, seriesOGZ2);
        }

        if (isCalc == "PGZ84") {
            XYChart.Series seriesPGZ1 = new XYChart.Series();
            seriesPGZ1.setName("Набір даних 1");
            XYChart.Series seriesPGZ2 = new XYChart.Series();
            seriesPGZ2.setName("Набір даних 2");
            getChartDataPGZ();
            seriesPGZ1.setData(gps1);
            seriesPGZ2.setData(gps2);
            lineChart.getData().addAll(seriesPGZ1, seriesPGZ2);
        }
    }

    public static void getChartDataDMStoDD() {
        List<DMStoDD.DD> gpsLatitude1 = Controller.resultsDMStoDD.stream().map(dd -> {
            return new DMStoDD.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (DMStoDD.DD latitude : gpsLatitude1) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataNMEA() {
        List<NMEA.DD> gpsLatitude1 = Controller.resultsNMEA.stream().map(dd -> {
            return new NMEA.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (NMEA.DD latitude : gpsLatitude1) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataDDtoDMS() {
        List<DDtoDMS.DD> gpsLatitude = Controller.resultsDDtoDMS.stream().map(dd -> {
            return new DDtoDMS.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (DDtoDMS.DD latitude : gpsLatitude) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataDDtoCK42() {
        List<DDtoCK42.DD> gpsLatitude = Controller.resultsDDtoCK42.stream().map(dd -> {
            return new DDtoCK42.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (DDtoCK42.DD latitude : gpsLatitude) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataDMStoCK42() {
        List<DMStoCK42.DD> gpsLatitude = Controller.resultsDMStoCK42.stream().map(dd -> {
            return new DMStoCK42.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (DMStoCK42.DD latitude : gpsLatitude) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataCK42toDD() {
        List<CK42toDD.DD> gpsLatitude = Controller.resultsCK42toDD.stream().map(dd -> {
            return new CK42toDD.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (CK42toDD.DD latitude : gpsLatitude) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataOGZ() {
        //............  1
        List<Ogz84.Latitude1> gpsLatitude1 = Controller.resultsOGZ84.stream().map(gpsTimes -> {
            return new Ogz84.Latitude1(Double.parseDouble(gpsTimes.getLatitude1()), Double.parseDouble(gpsTimes.getLongitude1()));
        }).collect(Collectors.toList());

        gps1 = FXCollections.observableArrayList();
        for (Ogz84.Latitude1 latitude : gpsLatitude1) {
            gps1.add(new XYChart.Data(latitude.getLatitude1(), latitude.getLongitude1()));
        }

        //..............  2
        List<Ogz84.Latitude2> gpsLatitude2 = Controller.resultsOGZ84.stream().map(gpsTimes -> {
            return new Ogz84.Latitude2(Double.parseDouble(gpsTimes.getLatitude2()), Double.parseDouble(gpsTimes.getLongitude2()));
        }).collect(Collectors.toList());

        gps2 = FXCollections.observableArrayList();
        for (Ogz84.Latitude2 latitude : gpsLatitude2) {
            gps2.add(new XYChart.Data(latitude.getLatitude2(), latitude.getLongitude2()));
        }
    }

    public static void getChartDataPGZ() {
        //............  1
        List<Pgz84.Latitude1> gpsLatitude1 = Controller.resultsPGZ84.stream().map(gpsTimes -> {
            return new Pgz84.Latitude1(gpsTimes.getLatitude1(), gpsTimes.getLongitude1());
        }).collect(Collectors.toList());

        gps1 = FXCollections.observableArrayList();
        for (Pgz84.Latitude1 latitude : gpsLatitude1) {
            gps1.add(new XYChart.Data(latitude.getLatitude1(), latitude.getLongitude1()));
        }

        //..............  2
        List<Pgz84.Latitude2> gpsLatitude2 = Controller.resultsPGZ84.stream().map(gpsTimes -> {
            return new Pgz84.Latitude2(gpsTimes.getLatitude2(), gpsTimes.getLongitude2());
        }).collect(Collectors.toList());

        gps2 = FXCollections.observableArrayList();
        for (Pgz84.Latitude2 latitude : gpsLatitude2) {
            gps2.add(new XYChart.Data(latitude.getLatitude2(), latitude.getLongitude2()));
        }
    }

    public void onClickScatterChart() throws IOException {
        os.viewURL = "/view/scatterChartConverter.fxml";
        os.title = "Графік GPS   " + openFile;
        os.maximized = false;
        os.openStage();
        Stage stage = (Stage) scatterChartButton.getScene().getWindow();
        stage.close();
    }
}

