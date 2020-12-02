package ua.comparis.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import ua.comparis.javaclass.GetSettings;
import ua.comparis.javaclass.domain.Ogz84;
import ua.comparis.javaclass.domain.DMStoDD;
import ua.comparis.javaclass.domain.SourceDMStoDD;
import ua.comparis.javaclass.domain.SourceOGZ84;
import ua.comparis.javaclass.servisClass.AlertAndInform;
import ua.comparis.javaclass.servisClass.FileChooserRun;
import ua.comparis.javaclass.servisClass.OpenStage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.comparis.javaclass.geo.DMStoDDConverter.rezultDMStoDDBulk;
import static ua.comparis.javaclass.geo.OgzWGS84.rezultOGZ84Bulk;
import static ua.comparis.javaclass.servisClass.FileChooserRun.selectedOpenFile;

@Slf4j
public class Controller {
    AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();

    public static String localZone = "GMT+2";
    public static String openFile = " ";
    public static String openDirectory;
    public static String status = "UNKNOWN";
    public String lineCount;
    //    public String headFile = "Час,    Широта,    Довгота,    Висота,       Час,    Широта,    Довгота,    Висота,    " +
//            "Відстань 2D,    Відстань 3D,      Кут відхилення\n";
    public String headSourceOGZ84 = "Час,    Широта,    Довгота,    Висота,    Час,    Широта,    Довгота,    Висота \n";
    public String headOgz84 = "Час,    Широта,    Довгота,    Висота,       Час,    Широта,    Довгота,    Висота,    " + "Відстань 2D,    Відстань 3D,      Кут відхилення\n";
    public String headDMStoDD = "Широта,    Довгота,    Висота,       Широта,    Довгота,    Висота   \n";

    public static List<SourceOGZ84> sourceOGZ84s = new ArrayList<>();
    public static List<SourceDMStoDD> sourceDMStoDDs = new ArrayList<>();
    public static List<Ogz84> rezultsOGZ84 = new ArrayList<>();
    public static List<DMStoDD> rezultsDMStoDD = new ArrayList<>();

    @FXML
    public TextArea outputText;
    @FXML
    public ImageView imgView;
    @FXML
    public TextField statusBar, labelLineCount;
    @FXML
    public Label statusLabel;

    public ProgressIndicator progressIndicator;


    public void openDataOGZ84() throws Exception {

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
            sourceOGZ84s.add(source);
        }
        rezultsOGZ84 = rezultOGZ84Bulk(sourceOGZ84s);

        fileReader.close();

        List<String> soursStrings = sourceOGZ84s.stream().map(SourceOGZ84::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);
        outputText.setText(String.valueOf(new StringBuilder()
                .append(headSourceOGZ84)
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber);
        labelLineCount.setText("Cтрок:  " + lineCount);

        getSettings.getGMT();

        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
        status = "OGZ84";
    }

    public void openDataDMStoDD() throws Exception {

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

            SourceDMStoDD source = new SourceDMStoDD(
                    split[1],
                    split[2],
                    split[3],
                    split[8],
                    split[9],
                    split[10],
                    split[14]);
            sourceDMStoDDs.add(source);
        }
        rezultsDMStoDD = rezultDMStoDDBulk(sourceDMStoDDs);

        fileReader.close();

        List<String> soursStrings = sourceDMStoDDs.stream().map(SourceDMStoDD::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);

        outputText.setText(String.valueOf(new StringBuilder()
                .append("Широта,       Довгота,      Висота \n")
                .append(textForTextArea)));

        lineCount = String.valueOf(lineNumber);
        labelLineCount.setText("Cтрок:  " + lineCount);

        statusLabel.setText("Вхідні дані");
        statusBar.setText(openFile);
        status = "DMStoDD";
    }

    public void openFile() throws Exception {
        if (outputText.getText().equals("")) {
            statusBar.setText("");
            progressIndicatorRun();

            fileChooserRun.openFileChooser();
            openFile = selectedOpenFile.getName();
            openDirectory = selectedOpenFile.getParent();

            progressIndicator.setVisible(false);
            return;
        } else
            inform.hd = "Файл уже відкритий";
        inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
        inform.inform();
        return;
    }

    public void onClickOpenOGZ84(ActionEvent actionEvent) throws Exception {
        openFile();
        openDataOGZ84();
    }

    public void onClickOpenDMStoDD(ActionEvent actionEvent) throws Exception {
        openFile();
        openDataDMStoDD();
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
                break;

            case ("DMStoDD"):
                try {
                    List<String> rezultStrings =  rezultsDMStoDD.stream().map(DMStoDD::toString).collect(Collectors.toList());
                    String textForTextArea = String.join("", rezultStrings);
                    outputText.setText(String.valueOf(new StringBuilder()
                            .append(headDMStoDD)
                            .append(textForTextArea)));
                } catch (NumberFormatException e) {
                    inform.alert();
                }
                statusBar.setText(openFile);
                statusLabel.setText("Розраховані дані");
                break;
        }
    }

    public void onClickOpenFileInDesktop(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        fileChooserRun.openFileChooser();
        desktop.open(selectedOpenFile);
    }

    @SneakyThrows
    public void onClickSave(ActionEvent actionEvent) throws IOException {
        progressIndicatorRun();
        if (CollectionUtils.isEmpty(rezultsOGZ84)) {
            log.warn("Ogz84 is empty");
            statusBar.setText("Помилка! Відсутні дані для збереження");
            inform.hd = "Помилка! Відсутні дані для збереження";
            inform.ct = " 1. Відкрити підготовлений файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            progressIndicator.setVisible(false);
            statusBar.setText("");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.csv", "*.csv"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        fileChooser.setInitialFileName("Comparis_" + openFile);
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showSaveDialog((new Stage()));

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, false), "CP1251");
        osw.write(headOgz84);
        for (Ogz84 rezult : rezultsOGZ84) {
            osw.write(rezult.toString());
        }
        osw.close();

        statusBar.setText("Успішно записано в файл 'Comparis_" + openFile + "'");
        progressIndicator.setVisible(false);
    }

    public void onClickChart(ActionEvent actionEvent) throws IOException {
        progressIndicator.setVisible(true);
        if (status.equals("OGZ84")) {
            os.viewURL = "/view/chart.fxml";
            os.title = "Графік GPS   " + openFile;
            os.maximized = true;
            os.openStage();
            progressIndicator.setVisible(false);
        } else {
            statusBar.setText("Помилка! Відсутні дані для розрахунку");
            inform.hd = "Помилка! Відсутні дані для відображення";
            inform.ct = " Необхідно відкрити підготовлений файл вхідних даних\n ";
            inform.alert();
            statusBar.setText("");
            progressIndicator.setVisible(false);
            return;
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
        sourceOGZ84s.clear();
        rezultsOGZ84.clear();
        sourceDMStoDDs.clear();
        progressIndicator.setVisible(false);
        status = "UNKNOWN";
    }

    public void onClickPressureSettings(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Settings.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        stage.setTitle("Наземний тиск   " + openFile);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/measuring.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void progressIndicatorRun() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
            statusBar.setText("Зачекайте...");
        });
    }

    public void onClickCancelBtn(ActionEvent e) {
        System.exit(0);
    }

    public void onClickVelocity(ActionEvent actionEvent) {
    }


}


