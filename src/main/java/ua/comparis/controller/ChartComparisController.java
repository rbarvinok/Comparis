package ua.comparis.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import ua.comparis.javaclass.domain.Ogz84;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ua.comparis.controller.Controller.openFile;


public class ChartComparisController implements Initializable {

    @FXML
    public LineChart lineChart, lineChartAlt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Набір даних 1");
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Набір даних 2");

        //............  1
        List<Ogz84.Latitude1> gpsLatitude1 = Controller.resultsOGZ84.stream().map(gpsTimes -> {
            return new Ogz84.Latitude1(Double.parseDouble(gpsTimes.getLatitude1()), Double.parseDouble(gpsTimes.getLongitude1()));
        }).collect(Collectors.toList());

        ObservableList<XYChart.Data> gps1 = FXCollections.observableArrayList();
        for (Ogz84.Latitude1 latitude : gpsLatitude1) {
            gps1.add(new XYChart.Data(latitude.getLatitude1(), latitude.getLongitude1()));
        }

        series1.setData(gps1);


        //..............  2
        List<Ogz84.Latitude2> gpsLatitude2 = Controller.resultsOGZ84.stream().map(gpsTimes -> {
            return new Ogz84.Latitude2(Double.parseDouble(gpsTimes.getLatitude2()), Double.parseDouble(gpsTimes.getLongitude2()));
        }).collect(Collectors.toList());

        ObservableList<XYChart.Data> gps2 = FXCollections.observableArrayList();
        for (Ogz84.Latitude2 latitude : gpsLatitude2) {
            gps2.add(new XYChart.Data(latitude.getLatitude2(), latitude.getLongitude2()));
        }

        series2.setData(gps2);

        lineChart.getData().addAll(series1, series2);


        ///////Висота

        XYChart.Series seriesAlt1 = new XYChart.Series();
        XYChart.Series seriesAlt2 = new XYChart.Series();

        CategoryAxis x1 = new CategoryAxis();
        NumberAxis y1 = new NumberAxis();
        LineChart<String, Number> lccAlt = new LineChart<String, Number>(x1, y1);

        seriesAlt1.setName("Набір даних 1");
        seriesAlt2.setName("Набір даних 2");


//////////////   1............
        List<Ogz84.Altitude1> gpsAlt1 = Controller.resultsOGZ84.stream().map(rezult -> {
            return new Ogz84.Altitude1(rezult.getLocalTime1(), Double.parseDouble(rezult.getAltitude1()));
        }).collect(Collectors.toList());

        ObservableList<XYChart.Data> Alt1 = FXCollections.observableArrayList();
        for (Ogz84.Altitude1 altitude1 : gpsAlt1) {
            Alt1.add(new XYChart.Data(altitude1.getLocalTime1(), altitude1.getAltitude1()));
        }

        seriesAlt1.setData(Alt1);

//////////////   2............
        List<Ogz84.Altitude2> gpsAlt2 = Controller.resultsOGZ84.stream().map(rezult -> {
            return new Ogz84.Altitude2(rezult.getLocalTime2(), Double.parseDouble(rezult.getAltitude2()));
        }).collect(Collectors.toList());

        ObservableList<XYChart.Data> Alt2 = FXCollections.observableArrayList();
        for (Ogz84.Altitude2 altitude2 : gpsAlt2) {
            Alt2.add(new XYChart.Data(altitude2.getLocalTime2(), altitude2.getAltitude2()));
        }

        seriesAlt2.setData(Alt2);

        lineChartAlt.getData().addAll(seriesAlt1, seriesAlt2);

    }
}