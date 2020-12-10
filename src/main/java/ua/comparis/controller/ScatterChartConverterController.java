package ua.comparis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import ua.comparis.javaclass.servisClass.OpenStage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ua.comparis.controller.Controller.isCalc;
import static ua.comparis.controller.Controller.openFile;
import static ua.comparis.controller.ChartConverterController.*;

public class ScatterChartConverterController implements Initializable {
    OpenStage os = new OpenStage();
    @FXML
    public ScatterChart scatterChart;
    @FXML
    public Button lineChartButton;

    @Override
    public void initialize( URL location, ResourceBundle resources ) {
        NumberAxis x = new NumberAxis();
        x.setAutoRanging(false);
        x.setForceZeroInRange(false);

        NumberAxis y = new NumberAxis();
        y.setAutoRanging(false);
        y.setForceZeroInRange(false);

        ScatterChart<Number, Number> cccGPS = new ScatterChart<Number, Number>(x, y);
        cccGPS.setTitle("Графік GPS " + openFile);
        x.setLabel("Latitude");
        y.setLabel("Longitude");

        if (isCalc == "DMStoDD") {
            XYChart.Series series1 = new XYChart.Series();
            series1.setName("Координати");
            getChartDataDMStoDD();
            series1.setData(gps);
            scatterChart.getData().addAll(series1);
        }

        if (isCalc == "DDtoDMS") {
            XYChart.Series series2 = new XYChart.Series();
            series2.setName("Координати");
            getChartDataDDtoDMS();
            series2.setData(gps);
            scatterChart.getData().addAll(series2);
        }

        if (isCalc == "DDtoCK42") {
            XYChart.Series seriesCK42 = new XYChart.Series();
            seriesCK42.setName("Координати");
            getChartDataDDtoCK42();
            seriesCK42.setData(gps);
            scatterChart.getData().addAll(seriesCK42);
        }

        if (isCalc == "CK42toDD") {
            XYChart.Series seriesWGS = new XYChart.Series();
            seriesWGS.setName("Координати");
            getChartDataCK42toDD();
            seriesWGS.setData(gps);
            scatterChart.getData().addAll(seriesWGS);
        }

        if (isCalc == "OGZ84") {
            XYChart.Series seriesOGZ1 = new XYChart.Series();
            seriesOGZ1.setName("Набір даних 1");
            XYChart.Series seriesOGZ2 = new XYChart.Series();
            seriesOGZ2.setName("Набір даних 2");
            getChartDataOGZ();
            seriesOGZ1.setData(gps1);
            seriesOGZ2.setData(gps2);
            scatterChart.getData().addAll(seriesOGZ1, seriesOGZ2);
        }
    }

    public void onClickLineChart( ActionEvent actionEvent ) throws IOException {
        os.viewURL = "/view/chartConverter.fxml";
        os.title = "Графік GPS   " + openFile;
        os.maximized = false;
        os.openStage();

        Stage stage = (Stage) lineChartButton.getScene().getWindow();
        stage.close();
    }
}

