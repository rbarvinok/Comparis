package ua.comparis.javaclass.servisClass;

import javafx.stage.Stage;

import java.io.File;

public class FileChooserRun {

    AlertAndInform inform = new AlertAndInform();
    public static File selectedOpenFile;
    public String extensionFilter;

    public void openFileChooser() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Comparis. Відкриття файлу");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("*.xlsx", "*.xlsx"),
                new javafx.stage.FileChooser.ExtensionFilter("*.csv", "*.csv"),
                new javafx.stage.FileChooser.ExtensionFilter(".txt", "*.txt"),
                new javafx.stage.FileChooser.ExtensionFilter("*.*", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        try {
            if (selectedFile != null) {
                selectedOpenFile = selectedFile;
                extensionFilter=fileChooser.getSelectedExtensionFilter().getDescription();
            } else {
                inform.hd = "Помилка! ";
                inform.ct = "Не вдалось відкрити файл\n";
                inform.alert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}