package ua.euler.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import ua.euler.javaclass.Dovidka;
import ua.euler.javaclass.OpenStage;
import ua.euler.javaclass.QuaternionToEulerAnglesConvector;
import ua.euler.javaclass.domain.EulerAngles;
import ua.euler.javaclass.domain.Quaternion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Controller {
    Dovidka pb = new Dovidka();
    OpenStage os = new OpenStage();
    //    ProgressIndicatorRun progressIndicator = new ProgressIndicatorRun();

    public static String openFile;
    public static String openDirectory;
    public static String fileData;
    public static String fileTime;
    public static String headFile = "Час UTC, Курс, Крен, Тангаж \n";

    public static List<EulerAngles> eulerAngles = new ArrayList<>();
    public static List<Quaternion> quaternions = new ArrayList<>();


    @FXML
    public TextArea outputText;
    @FXML
    public ImageView imgView;
    @FXML
    public TextField statusBar;
    @FXML
    public Label statusLabel, labelFileName, labelFileData, labelFileTime;
    @FXML
    public ProgressIndicator pi;

    public void onClick(ActionEvent actionEvent) {
        if (statusBar.getText().equals("")) {
            statusBar.setText("Помилка! Відсутні дані для рохрахунку");
            pb.hd = "Помилка! Відсутні дані для рохрахунку";
            pb.ct = " 1. Відкрити підготовлений файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
            pb.dovButton();
        } else
            try {
                List<String> eulerAnglesStrings = eulerAngles.stream().map(EulerAngles::toString).collect(Collectors.toList());
                String textForTextArea = String.join("", eulerAnglesStrings);
                outputText.setText(textForTextArea);
            } catch (NumberFormatException e) {
                pb.alert();
            }
        statusBar.setText("Кути Ейлера (Час UTC, Курс, Крен, Тангаж)");
        statusLabel.setText("Кути Ейлера (Час UTC, Курс, Крен, Тангаж)");
    }

    public void onClickOpenFile(ActionEvent actionEvent) throws IOException {
        pi.setVisible(true);
        //Desktop desktop = Desktop.getDesktop();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("EulerConverter. Відкриття файлу");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.csv", "*.csv"),
                new FileChooser.ExtensionFilter(".txt", "*.txt"),
                new FileChooser.ExtensionFilter("*.*", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            //desktop.open(selectedFile);
            openFile = selectedFile.getName();
            openDirectory = selectedFile.getParent();

            FileReader fileReader = new FileReader(selectedFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            int lineNumber = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {

                if (lineNumber == 2) {
                    line = line.replaceAll(";", ",");
                    fileData = line.split(",")[2];
                    fileTime = line.split(",")[3];
                }

                // А где пример фалйоа?

                //line = line.replaceAll(",", ".").replaceAll(";", ",");
                line = line.replaceAll(";", ",");

                String[] split = line.split(",");


                if (split.length <= 11 || lineNumber < 9) {
                    lineNumber++;
                    continue;
                }
                lineNumber++;

                Quaternion quaternion = new Quaternion(
                        QuaternionToEulerAnglesConvector.timeFormatter(split[0]),
                        Double.parseDouble(split[7]),
                        Double.parseDouble(split[8]),
                        Double.parseDouble(split[9]),
                        Double.parseDouble(split[10]));
                quaternions.add(quaternion);
            }
            eulerAngles = QuaternionToEulerAnglesConvector.quaternionToEulerAnglesBulk(quaternions);

            List<String> quaternionStrings = quaternions.stream().map(Quaternion::toString).collect(Collectors.toList());
            String textForTextArea = String.join("", quaternionStrings);
            outputText.setText(textForTextArea);
        }

        pi.setVisible(false);
        statusBar.setText("Кватерніони (Час UTC, Qw, Qx, Qy, Qz)");
        statusLabel.setText("Кватерніони (Час UTC, Qw, Qx, Qy, Qz)");
        labelFileName.setText("Файл \n" + openFile);
        labelFileData.setText(" Дата \n" + fileData);
        labelFileTime.setText(" Час  \n" + fileTime);
    }

   @SneakyThrows
    public void onClickSave(ActionEvent actionEvent) throws IOException {
        pi.setVisible(true);
        if (CollectionUtils.isEmpty(eulerAngles)) {
            log.warn("eulerAnges is empty");
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
        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.write("Дата,  " + fileData + "\n");
        fileWriter.write("Час початку вимірювання,  " + fileTime + "\n");
        fileWriter.write(headFile);
        for (EulerAngles eulerAngle : eulerAngles) {
//            log.info(eulerAngle.toString());
            fileWriter.write(eulerAngle.toString());
        }
        fileWriter.close();
        statusBar.setText("Успішно записано в файл 'EulerAngles_" + openFile + "'");
        pi.setVisible(false);
    }

    public void onClickChart(ActionEvent actionEvent) throws IOException {
        pi.setVisible(true);
        if (statusLabel.getText().equals("Кути Ейлера (Час UTC, Курс, Крен, Тангаж)")) {
            os.viewURL = "/view/chartEuler.fxml";
            os.title = "Кути Ейлера   " + openFile;
            os.openStage();
            pi.setVisible(false);
        } else {
            if (statusLabel.getText().equals("Кватерніони (Час UTC, Qw, Qx, Qy, Qz)")) {
                os.viewURL = "/view/chartQuaternion.fxml";
                os.title = "Кватерніони   " + openFile;
                os.openStage();
                pi.setVisible(false);
            } else {
                statusBar.setText("Помилка! Відсутні дані для рохрахунку");
                pb.hd = "Помилка! Відсутні дані для рохрахунку";
                pb.ct = " 1. Відкрити підготовлений файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
                pb.dovButton();
            }
        }
    }

    public void onClickDovBtn(ActionEvent actionEvent) {
        pb.hd = "Конвертор кватерніонів в кути Ейлера";
        pb.ct = " 1. Відкрити файл вихідних даних\n 2. Натиснути кнопку Розрахувати \n 3. Зберегти розраховані дані в вихідний файл\n";
        pb.dovButton();
    }

    public void onClick_menuAbout(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/about.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onClickCancelBtn(ActionEvent e) {
        System.exit(0);
    }

    public void OnClickNew(ActionEvent e) {
        outputText.setText("");
        statusBar.setText("");
        statusLabel.setText("Конвертування кватерніонів в кути Ейлера");
        labelFileName.setText(" ");
        labelFileData.setText(" ");
        labelFileTime.setText(" ");
        pi.setVisible(false);
    }

}


