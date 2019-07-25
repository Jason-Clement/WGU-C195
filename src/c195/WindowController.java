package c195;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class WindowController {
    
    private Stage stage;
    private double width;
    private double height;
    private Scene scene;
    
    public WindowController(Stage stage) {
        this.stage = stage;
    }
    
    public void exit() {
        stage.close();
    }
    
    public void loadMain() {
        FXMLLoader loader = loadFXML("FXMLMain.fxml", "FXML Main");
        FXMLMainController controller = loader.<FXMLMainController>getController();
        controller.load(this);
        showStage();
    }
    
    public void loadLogin() {
        FXMLLoader loader = loadFXML("FXMLLogin.fxml", "FXML Login");
        FXMLLoginController controller = loader.<FXMLLoginController>getController();
        controller.load(this);
        showStage();
    }
    
    private void showStage() {
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();
    }
    
    private FXMLLoader loadFXML(String url, String name) {
        width = stage.getWidth();
        height = stage.getHeight();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "The " + name + " could not be loaded", ButtonType.OK);
            alert.showAndWait();
            return null;
        }
        
        scene = new Scene(pane);
        
        return loader;
    }
}
