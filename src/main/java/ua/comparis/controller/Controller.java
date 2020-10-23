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
import ua.comparis.javaclass.domain.Rezult;
import ua.comparis.javaclass.domain.Source;
import ua.comparis.javaclass.servisClass.AlertAndInform;
import ua.comparis.javaclass.servisClass.FileChooserRun;
import ua.comparis.javaclass.servisClass.OpenStage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Double.parseDouble;
import static ua.comparis.javaclass.OgzWGS84.rezultBulk;
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
    public String lineCount;
    public String headFile = "Час,    Широта,    Довгота,    Висота,    Час,    Широта,    Довгота,    Висота,     " +
            "Відстань 2D,    Відстань 3D,    Кут відхилення\n";
    public String headSource = "Час,    Широта,    Довгота,    Висота,    Час,    Широта,    Довгота,    Висота \n";
    public String headRezult = "Час,    Широта,    Довгота,    Висота,       Час,    Широта,    Довгота,    Висота,    " +
            "Відстань 2D,    Відстань 3D,      Кут відхилення\n";
    public String headVelocity = "";

    public static List<Source> sources = new ArrayList<>();
    public static List<Rezult> rezults = new ArrayList<>();

    @FXML
    public TextArea outputText;
    @FXML
    public ImageView imgView;
    @FXML
    public TextField statusBar, labelLineCount;
    @FXML
    public Label statusLabel;

    public ProgressIndicator progressIndicator;


    public void openData() throws Exception {

        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            line = line.replaceAll(";", ",");

            line = line.replaceAll(";", ",");

            String[] split = line.split(",");
            if (split.length <= 6 || lineNumber < 3) {
                lineNumber++;
                continue;
            }
            lineNumber++;

            Source source = new Source(
                    split[0],
                    parseDouble(split[1]),
                    parseDouble(split[2]),
                    parseDouble(split[3]),
                    split[4],
                    parseDouble(split[5]),
                    parseDouble(split[6]),
                    parseDouble(split[7]));
            sources.add(source);
        }
        rezults = rezultBulk(sources);

        fileReader.close();

        List<String> soursStrings = sources.stream().map(Source::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", soursStrings);
        outputText.setText(textForTextArea);

        lineCount = String.valueOf(lineNumber);
        labelLineCount.setText("Cтрок:  " + lineCount);

        getSettings.getGMT();

        statusLabel.setText(headSource);
        statusBar.setText(openFile);

    }

    public void onClickOpenFile(ActionEvent actionEvent) throws Exception {
        if (outputText.getText().equals("")) {
            statusBar.setText("");
            progressIndicatorRun();

            fileChooserRun.openFileChooser();
            openFile = selectedOpenFile.getName();
            openDirectory = selectedOpenFile.getParent();

            openData();
            progressIndicator.setVisible(false);
            return;
        } else
            inform.hd = "Файл уже відкритий";
        inform.ct = " Повторне відкриття файлу призведе до втрати не збережених даних \n";
        inform.inform();
        return;
    }

    public void onClickCalculate(ActionEvent actionEvent) {
        if (outputText.getText().equals("")) {
            statusBar.setText("Помилка! Відсутні дані для рохрахунку");
            inform.hd = "Помилка! Відсутні дані для рохрахунку";
            inform.ct = " 1. Відкрити файл  даних \n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            statusBar.setText("");
            return;
        } else
            try {
                List<String> rezultStrings = rezults.stream().map(Rezult::toString).collect(Collectors.toList());
                String textForTextArea = String.join("", rezultStrings);
                outputText.setText(textForTextArea);

            } catch (NumberFormatException e) {
                inform.alert();
            }
        statusBar.setText(openFile);
        statusLabel.setText(headRezult);
    }

    public void onClickOpenFileInDesktop(ActionEvent actionEvent) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        fileChooserRun.openFileChooser();
        desktop.open(selectedOpenFile);
    }

    @SneakyThrows
    public void onClickSave(ActionEvent actionEvent) throws IOException {
        progressIndicatorRun();
        if (CollectionUtils.isEmpty(rezults)) {
            log.warn("Rezult is empty");
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

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
        osw.write(headFile);
        for (Rezult rezult : rezults) {
            osw.write(rezult.toString());
        }
        osw.close();

        statusBar.setText("Успішно записано в файл 'Comparis_" + openFile + "'");
        progressIndicator.setVisible(false);
    }

    public void onClickChart(ActionEvent actionEvent) throws IOException {
//        progressIndicator.setVisible(true);
//        if (statusLabel.getText().equals(headEuler)) {
//            os.viewURL = "/view/chartEuler.fxml";
//            os.title = "Кути Ейлера   " + openFile;
//            os.openStage();
//            progressIndicator.setVisible(false);
//        } else {
//            if (statusLabel.getText().equals(headQuaternion)) {
//                os.viewURL = "/view/chartQuaternion.fxml";
//                os.title = "Кватерніони   " + openFile;
//                os.openStage();
//                progressIndicator.setVisible(false);
//
//            } else {
//                if (statusLabel.getText().equals(headVelocity)) {
//                    os.viewURL = "/view/chartVelocity.fxml";
//                    os.title = "Вертикальна швидкість   " + openFile;
//                    os.openStage();
//                    progressIndicator.setVisible(false);
//
//                } else {
//                    statusBar.setText("Помилка! Відсутні дані для рохрахунку");
//                    inform.hd = "Помилка! Відсутні дані для відображення";
//                    inform.ct = " Необхідно відкрити підготовлений файл вхідних даних\n ";
//                    inform.alert();
//                    statusBar.setText("");
//                    progressIndicator.setVisible(false);
//                    return;
//                }
//            }
//        }
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
        sources.clear();
        rezults.clear();
        progressIndicator.setVisible(false);
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


