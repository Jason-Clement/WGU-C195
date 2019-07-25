package c195;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import models.*;

public class Application {

    private static volatile Application instance;
    private static final Object mutex = new Object();
    private User activeUser = null;

    private Application() {
    }

    public static Application getInstance() {
        Application result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Application();
            }
        }
        return result;
    }
    
    public static boolean alertForConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Are you sure?");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().get() == ButtonType.YES)
            return true;
        return false;
    }
    
    public static void alertForError(String message1, String message2) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("An error has occurred");
        alert.setHeaderText(null);
        if (message2 == null)
            alert.setContentText(message1);
        else
            alert.setContentText(message1
                + System.lineSeparator()
                + " The error message was: "
                + System.lineSeparator() + System.lineSeparator()
                + message2);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.OK);
        alert.showAndWait();
    }
    
    public Connection getDBConnection() {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        final String DB_URL = "jdbc:mysql://52.206.157.109/U03vBO";
        final String DBUSER = "U03vBO";
        final String DBPASS = "53688095526";
        
        Connection connection = null;
        
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DBUSER, DBPASS);
        } catch (ClassNotFoundException | SQLException ex) {
            Application.alertForError(
                    "Something went wrong connecting to the database.", ex.getLocalizedMessage());
        }
        
        return connection;
    }
    
    public User getActiveUser() {
        return activeUser;
    }
    
    private void setActiveUser(User user) {
        this.activeUser = user;
    }
    
    // Clear text passwords are bad. Bad bad bad bad bad.
    public void attemptLogin(String userName, String password) throws InvalidUsernameOrPasswordException {
        try (Connection connection = this.getDBConnection()) {
            String sql = "SELECT * FROM user WHERE userName = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, userName);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new InvalidUsernameOrPasswordException();
            }
            User user = new User();
            user.fromResultSet(rs);
            this.setActiveUser(user);
            this.recordLogin();
        } catch (SQLException ex) {
            Application.alertForError(
                    "Something when wrong connecting to the database.", ex.getLocalizedMessage());
        }
    }
    
    private void recordLogin() {
        String line = Instant.now().toString() + " " + activeUser.getUserName()
                + System.lineSeparator();
        try {
            Path path = Paths.get(System.getProperty("user.home"), "C195Logins.log");
            StandardOpenOption opt = Files.exists(path) ?
                    StandardOpenOption.APPEND : StandardOpenOption.CREATE;
            Files.write(path, line.getBytes(), opt);
        } catch (IOException ex) {
            System.err.println("Could not open the logins.log file. The error was: " + ex.getLocalizedMessage());
        }
    }
    
    public static ZonedDateTime toZonedDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime().atZone(ZoneId.of("Z"))
                .withZoneSameInstant(ZoneId.systemDefault());
    }
    
    public static Timestamp toTimestamp(ZonedDateTime dateTime) {
        return Timestamp.valueOf(dateTime
                .withZoneSameInstant(ZoneId.of("Z")).toLocalDateTime());
    }
}
