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
    public void initialize(URL location, ResourceBundle resources) {
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
        switch (isCalc) {
            case ("DMStoDD"):
                XYChart.Series series1 = new XYChart.Series();
                series1.setName("Координати");
                getChartDataDMStoDD();
                series1.setData(gps);
                scatterChart.getData().addAll(series1);

            case ("DDtoDMS"):
                XYChart.Series series2 = new XYChart.Series();
                series2.setName("Координати");
                getChartDataDDtoDMS();
                series2.setData(gps);
                scatterChart.getData().addAll(series2);
        }
    }

    public void onClickLineChart(ActionEvent actionEvent) throws IOException {
        os.viewURL = "/view/chartConverter.fxml";
        os.title = "Графік GPS   " + openFile;
        os.maximized = false;
        os.openStage();

        Stage stage = (Stage) lineChartButton.getScene().getWindow();
        stage.close();
    }
}

