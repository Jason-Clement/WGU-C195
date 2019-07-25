package c195;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.Map.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import models.*;


public class FXMLLoginController {

    @FXML private ComboBox<String> languageBox;
    @FXML private Label languageLabel;
    @FXML private Button loginButton;
    @FXML private PasswordField passwordBox;
    @FXML private Label passwordLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField usernameBox;
    @FXML private Label errorBox;
    
    private Hashtable<String, String> languages;
    private FXMLLoginModel loginModel = new FXMLLoginModel();
    private WindowController controller;
    private ResourceBundle resourceBundle;

    @FXML
    void initialize() {
        assert errorBox != null : "fx:id=\"errorBox\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert languageBox != null : "fx:id=\"languageBox\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert languageLabel != null : "fx:id=\"languageLabel\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert passwordBox != null : "fx:id=\"passwordBox\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert passwordLabel != null : "fx:id=\"passwordLabel\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert usernameLabel != null : "fx:id=\"usernameLabel\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        assert usernameBox != null : "fx:id=\"usernameBox\" was not injected: check your FXML file 'FXMLLogin.fxml'.";
        
        languageLabel.textProperty().bind(loginModel.languageProperty());
        usernameLabel.textProperty().bind(loginModel.usernameProperty());
        passwordLabel.textProperty().bind(loginModel.passwordProperty());
        loginButton.textProperty().bind(loginModel.loginProperty());
        
        languages = new Hashtable<>();
        languages.put("English", "en");
        Entry<String, String> lang = languages.entrySet().iterator().next();
        languages.put("российский", "ru");
        languages.put("Français", "fr");
        
        ObservableList<String> languageBoxItems = FXCollections
                .observableArrayList(languages.keySet().toArray(new String[0])).sorted();
        
        languageBox.setItems(languageBoxItems);
        
        Locale defLocale = Locale.getDefault();
        for (Entry<String, String> e : languages.entrySet()) {
            if (defLocale.getLanguage().equals(new Locale(e.getValue()).getLanguage())) {
                lang = e;
                break;
            }
        }
        
        languageBox.setValue(lang.getKey());
        setLanguage(lang.getValue());
    }
    
    public void load(WindowController controller) {
        this.controller = controller;
        
        usernameBox.requestFocus();
    }
    
    @FXML 
    void usernameBoxKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            passwordBox.requestFocus();
    }
    
    @FXML
    void passwordBoxKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            attemptLogin();
    }
    
    @FXML
    void loginButtonAction(ActionEvent event) {
        attemptLogin();
    }
    
    @FXML
    void changeLanguage(ActionEvent event) {
        String ln = languageBox.getValue();
        if (!languages.containsKey(ln)) {
            errorBox.setText(String.format(resourceBundle.getString("languageerror"), languageBox.getValue()));
            return;
        }
        setLanguage(languages.get(languageBox.getValue()));
        errorBox.setText("");
    }

    void attemptLogin() {
        String user = usernameBox.getText().trim();
        String pass = passwordBox.getText();
        try {
            Application.getInstance().attemptLogin(user, pass);
        } catch (InvalidUsernameOrPasswordException ex) {
            errorBox.setText(resourceBundle.getString("passworderror"));
            return;
        } catch (Exception ex) {
            errorBox.setText(String.format(resourceBundle.getString("dberror"), ex.getMessage()));
            return;
        }
        checkForAppointment();
        controller.loadMain();
    }
    
    void checkForAppointment() {
        User user = Application.getInstance().getActiveUser();
        try (Connection connection = Application.getInstance().getDBConnection()) {
            String sql = "SELECT a.*, c.customerName FROM appointment a "
                    + "INNER JOIN customer c ON c.customerId = a.customerId "
                    + "WHERE userId = ? AND start >= ? AND start <= ? ORDER BY start";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getUserId());
            ZonedDateTime current = ZonedDateTime.now();
            statement.setTimestamp(2, Application.toTimestamp(current));
            statement.setTimestamp(3, Application.toTimestamp(current.plus(15, ChronoUnit.MINUTES)));
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.fromResultSet(rs);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Soon");
                alert.setHeaderText("You have an appointment with "
                    + rs.getString("customerName") + " at "
                    + appointment.getStart().format(DateTimeFormatter.ofPattern("h:mma")));
                alert.getButtonTypes().clear();
                alert.getButtonTypes().addAll(ButtonType.OK);
                alert.show();
            }
        } catch (SQLException ex) {
            // Do nothing; if there is a database problem, it will be evident
            // in other places
        }
    }
    
    void setLanguage(String languageCode) {
        resourceBundle = ResourceBundle.getBundle("languages/" + languageCode);
        loginModel.setLanguage(resourceBundle.getString("language"));
        loginModel.setUsername(resourceBundle.getString("username"));
        loginModel.setPassword(resourceBundle.getString("password"));
        loginModel.setLogin(resourceBundle.getString("login"));
    }
    
    private class FXMLLoginModel {        
        private final StringProperty login = new SimpleStringProperty();
        public StringProperty loginProperty() { return login; }
        public final String getLogin() { return loginProperty().get(); }
        public final void setLogin(String value) { loginProperty().set(value); }
        
        private final StringProperty username = new SimpleStringProperty();
        public StringProperty usernameProperty() { return username; }
        public final String getUsername() { return usernameProperty().get(); }
        public final void setUsername(String value) { usernameProperty().set(value); }
        
        private final StringProperty password = new SimpleStringProperty();
        public StringProperty passwordProperty() { return password; }
        public final String getPassword() { return passwordProperty().get(); }
        public final void setPassword(String value) { passwordProperty().set(value); }
        
        private final StringProperty language = new SimpleStringProperty();
        public StringProperty languageProperty() { return language; }
        public final String getLanguage() { return languageProperty().get(); }
        public final void setLanguage(String value) { languageProperty().set(value); }
    }
}
