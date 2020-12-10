package ua.comparis.javaclass.servisClass;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static ua.comparis.Main.icoImage;

public class OpenStage {
    public String viewURL;
    public String imageURL = icoImage;
    public String title;
    public boolean maximized = false;
    public boolean isModality = false;
    public boolean isResizable = true;

    public void openStage() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewURL));
        Parent root = (Parent) fxmlLoader.load();
        stage.setTitle(title);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(imageURL)));
        stage.setScene(new Scene(root));
        stage.setMaximized(maximized);
        if (isModality = true)
            stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(isResizable);
        stage.show();
    }

    public void closeStage() {
//        Stage stage = (Stage) button.getScene().getWindow();
//        stage.close();
    }
}
