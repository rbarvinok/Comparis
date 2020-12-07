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
import ua.comparis.javaclass.domain.DDtoDMS;
import ua.comparis.javaclass.domain.DMStoDD;
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

        switch (isCalc) {
            case ("DMStoDD"):
                XYChart.Series series1 = new XYChart.Series();
                series1.setName("Координати");
                getChartDataDMStoDD();
                series1.setData(gps);
                lineChart.getData().addAll(series1);

            case ("DDtoDMS"):
                XYChart.Series series2 = new XYChart.Series();
                series2.setName("Координати");
                getChartDataDDtoDMS();
                series2.setData(gps);
                lineChart.getData().addAll(series2);
        }
    }

    public static void getChartDataDMStoDD() {
        List<DMStoDD.DD> gpsLatitude1 = Controller.rezultsDMStoDD.stream().map(dd -> {
            return new DMStoDD.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (DMStoDD.DD latitude : gpsLatitude1) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
        }
    }

    public static void getChartDataDDtoDMS() {
        List<DDtoDMS.DD> gpsLatitude = Controller.rezultsDDtoDMS.stream().map(dd -> {
            return new DDtoDMS.DD(dd.getLatitudeDD(), dd.getLongitudeDD());
        }).collect(Collectors.toList());

        gps = FXCollections.observableArrayList();
        for (DDtoDMS.DD latitude : gpsLatitude) {
            gps.add(new XYChart.Data(latitude.getLatitudeDD(), latitude.getLongitudeDD()));
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

