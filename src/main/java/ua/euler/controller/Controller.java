package ua.euler.controller;

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
import ua.euler.javaclass.GetSettings;
import ua.euler.javaclass.domain.EulerAngles;
import ua.euler.javaclass.domain.Quaternion;
import ua.euler.javaclass.servisClass.AlertAndInform;
import ua.euler.javaclass.servisClass.FileChooserRun;
import ua.euler.javaclass.servisClass.OpenStage;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.euler.javaclass.QuaternionToEulerAnglesConvectorNonNormalised.calculateAltVelocity;
import static ua.euler.javaclass.QuaternionToEulerAnglesConvectorNonNormalised.quaternionToEulerAnglesBulk;
import static ua.euler.javaclass.servisClass.FileChooserRun.selectedOpenFile;

@Slf4j
public class Controller {
    AlertAndInform inform = new AlertAndInform();
    OpenStage os = new OpenStage();
    FileChooserRun fileChooserRun = new FileChooserRun();
    GetSettings getSettings = new GetSettings();

    public static double pressureNull = 101325.0;
    public static String openFile = " ";
    public static String openDirectory;
    public static String fileData;
    public String fileTime;
    public String hamModel;
    public String hamNumber;
    public String lineCount;
    public String timeStart, timeStop;
    public String headFile = "Час,    Курс,    Крен,    Тангаж,    Висота,    Вертикальна швидкість \n";
    public String headQuaternion = "Кватерніони (Час, Qw, Qx, Qy, Qz)";
    public String headEuler = "Кути Ейлера (Час,  Курс,  Крен,  Тангаж,   Висота)";
    public String headVelocity = "Час,  Атмосферний тиск,  Висота,  Вертикальна швидкість";
    public Double allTime;

    public static List<EulerAngles> eulerAngles = new ArrayList<>();
    public static List<Quaternion> quaternions = new ArrayList<>();

    @FXML
    public TextArea outputText;
    @FXML
    public ImageView imgView;
    @FXML
    public TextField statusBar, labelLineCount;
    @FXML
    public Label statusLabel, labelFileName, labelFileData, labelFileTime;
    @FXML
    public Label labelHamModel, labelHamNumber, labelAllTime, labelPress, LabelCapPress;
    @FXML
    public ProgressIndicator progressIndicator;


    public void openData() throws Exception {

        FileReader fileReader = new FileReader(selectedOpenFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        int lineNumber = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {

            if (lineNumber == 0) {
                hamModel = line.split(",")[2] + line.split(",")[3];
            }
            if (lineNumber == 1) {
                hamNumber = line.split(",")[4];
            }
            if (lineNumber == 2) {
                line = line.replaceAll(";", ",");
                fileData = line.split(",")[2];
                fileTime = line.split(",")[3];
            }
            if (lineNumber == 9) {
                timeStart = line.split(",")[0];
            }

            line = line.replaceAll(";", ",");

            String[] split = line.split(",");
            if (split.length <= 14 || lineNumber < 9) {
                lineNumber++;
                continue;
            }
            lineNumber++;

            if (lineNumber == lineNumber) {
                timeStop = String.valueOf(line.split(",")[0]);
            }

            Quaternion quaternion = new Quaternion(
                    split[0],
                    Double.parseDouble(split[7]),
                    Double.parseDouble(split[8]),
                    Double.parseDouble(split[9]),
                    Double.parseDouble(split[10]),
                    Double.parseDouble(split[14]));
            quaternions.add(quaternion);
        }

        eulerAngles = quaternionToEulerAnglesBulk(quaternions);
        eulerAngles = calculateAltVelocity(eulerAngles);
        fileReader.close();

        List<String> quaternionStrings = quaternions.stream().map(Quaternion::toString).collect(Collectors.toList());
        String textForTextArea = String.join("", quaternionStrings);
        outputText.setText(textForTextArea);

        lineCount = String.valueOf(lineNumber);
        labelLineCount.setText("Cтрок:  " + lineCount);

        allTime = Double.parseDouble(timeStop) - Double.parseDouble(timeStart);

        getSettings.getPressureNull();

        statusLabel.setText(headQuaternion);
        statusBar.setText(headQuaternion);
        labelHamModel.setText(hamModel);
        labelHamNumber.setText(hamNumber);
        labelFileName.setText(" Файл \n" + openFile);
        labelFileData.setText(" Дата \n" + fileData);
        labelFileTime.setText(" Час  \n" + fileTime);
        labelAllTime.setText(" Час \n вимірювання \n" + allTime + " сек");
        LabelCapPress.setText("Тиск на рівні землі");
        labelPress.setText(pressureNull + "  Pa");
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
            inform.ct = " 1. Відкрити файл  даних 'НАМ'\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            statusBar.setText("");
            return;
        } else
            try {
                List<String> eulerAnglesStrings = eulerAngles.stream().map(EulerAngles::toStringEuler).collect(Collectors.toList());
                String textForTextArea = String.join("", eulerAnglesStrings);
                outputText.setText(textForTextArea);

            } catch (NumberFormatException e) {
                inform.alert();
            }
        statusBar.setText(headEuler);
        statusLabel.setText(headEuler);
    }

    public void onClickVelocity(ActionEvent actionEvent) {
        if (outputText.getText().equals("")) {
            statusBar.setText("Помилка! Відсутні дані для рохрахунку");
            inform.hd = "Помилка! Відсутні дані для рохрахунку";
            inform.ct = " 1. Відкрити файл  даних 'НАМ'\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            inform.alert();
            statusBar.setText("");
            return;
        } else {
            List<String> rateOfDeclinesStrings = eulerAngles.stream().map(EulerAngles::toStringVelocity).collect(Collectors.toList());
            String textForTextArea = String.join("", rateOfDeclinesStrings);
            outputText.setText(textForTextArea);

            statusBar.setText(headVelocity);
            statusLabel.setText(headVelocity);
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
        if (CollectionUtils.isEmpty(eulerAngles)) {
            log.warn("eulerAnges is empty");
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
                new FileChooser.ExtensionFilter(".txt", "*.txt"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        fileChooser.setInitialFileName("EulerAngles_" + openFile);
        File userDirectory = new File(openDirectory);
        fileChooser.setInitialDirectory(userDirectory);

        File file = fileChooser.showSaveDialog((new Stage()));

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), "Cp1251");
        osw.write("Модель реєстратора  -  " + hamModel + "\n");
        osw.write("Серійний номер  -  " + hamNumber + "\n");
        osw.write("Дата  -  " + fileData + "\n");
        osw.write("Час  -  " + fileTime + "\n");
        osw.write("Час вимірювання  -  " + allTime + "\n");
        osw.write("Атмосферний тиск на рівні землі  -  " + pressureNull + "  Па \n\n");
        osw.write(headFile);
        for (EulerAngles eulerAngle : eulerAngles) {
            //log.info(eulerAngle.toString());
            osw.write(eulerAngle.toString());
        }
        osw.close();

        statusBar.setText("Успішно записано в файл 'EulerAngles_" + openFile + "'");
        progressIndicator.setVisible(false);
    }

    public void onClickChart(ActionEvent actionEvent) throws IOException {
        progressIndicator.setVisible(true);
        if (statusLabel.getText().equals(headEuler)) {
            os.viewURL = "/view/chartEuler.fxml";
            os.title = "Кути Ейлера   " + openFile;
            os.openStage();
            progressIndicator.setVisible(false);
        } else {
            if (statusLabel.getText().equals(headQuaternion)) {
                os.viewURL = "/view/chartQuaternion.fxml";
                os.title = "Кватерніони   " + openFile;
                os.openStage();
                progressIndicator.setVisible(false);

            } else {
                if (statusLabel.getText().equals(headVelocity)) {
                    os.viewURL = "/view/chartVelocity.fxml";
                    os.title = "Вертикальна швидкість   " + openFile;
                    os.openStage();
                    progressIndicator.setVisible(false);

                } else {
                    statusBar.setText("Помилка! Відсутні дані для рохрахунку");
                    inform.hd = "Помилка! Відсутні дані для відображення";
                    inform.ct = " Необхідно відкрити підготовлений файл вхідних даних\n ";
                    inform.alert();
                    statusBar.setText("");
                    progressIndicator.setVisible(false);
                    return;
                }
            }
        }
    }

       public void onClickDovBtn(ActionEvent actionEvent) throws IOException {
        String url = "D:/EulerConverter/userManual/UserManual_Euler.pdf";
        Desktop desktop = Desktop.getDesktop();
        desktop.open(new File(url));

    }

    public void onClickMenuHAM(ActionEvent actionEvent) throws IOException {
        if (Desktop.isDesktopSupported()) {
            String url = "D:/EulerConverter/userManual/UserManual_HAM.pdf";
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(url));
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
        statusLabel.setText("Конвертування кватерніонів в кути Ейлера");
        labelHamModel.setText(" ");
        labelHamNumber.setText(" ");
        labelFileName.setText(" ");
        labelFileData.setText(" ");
        labelFileTime.setText(" ");
        labelLineCount.setText(" ");
        labelAllTime.setText(" ");
        labelPress.setText(" ");
        LabelCapPress.setText(" ");
        quaternions.clear();
        eulerAngles.clear();
        progressIndicator.setVisible(false);
    }

    public void onClickPressureSettings(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/pressureSettings.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        stage.setTitle("Наземний тиск   " + openFile);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/HAM.png")));
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

}


