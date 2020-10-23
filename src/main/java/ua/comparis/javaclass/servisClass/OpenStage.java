package ua.comparis.javaclass.servisClass;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenStage {
    public String viewURL;
    public String imageURL = "/images/measuring.png";
    public String title;

    public void openStage() throws IOException {

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewURL));
        Parent root = (Parent) fxmlLoader.load();
        stage.setTitle(title);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(imageURL)));
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void closeStage(){
//        Stage stage = (Stage) button.getScene().getWindow();
//        stage.close();
    }
}
