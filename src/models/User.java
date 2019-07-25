package models;

import c195.*;
import java.time.*;
import java.sql.*;
import javafx.beans.property.*;

public class User extends DBEntity {
    private ReadOnlyIntegerWrapper userId = new ReadOnlyIntegerWrapper();
    private SimpleStringProperty userName = new SimpleStringProperty();
    private SimpleStringProperty password = new SimpleStringProperty();
    private SimpleBooleanProperty active = new SimpleBooleanProperty();
    
    public int getUserId() { return userId.get(); }
    protected void setUserId(int value) { userId.set(value); }
    public ReadOnlyIntegerProperty userIdProperty() { return userId.getReadOnlyProperty(); }
    
    public String getUserName() { return userName.get(); }
    public void setUserName(String value) { userName.set(value); }
    public SimpleStringProperty userNameProperty() { return userName; }
    
    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public SimpleStringProperty passwordProperty() { return password; }
    
    public boolean getActive() { return active.get(); }
    public void setActive(boolean value) { active.set(value); }
    public SimpleBooleanProperty activeProperty() { return active; }
    
    protected int getId() { return getUserId(); }
    protected void setId(int value) { setUserId(value); }
    
    private final String[] columnNames = new String[] { "userName", "password", "active" };
    protected String[] getColumnNames() { return columnNames; }
    private final String tableName = "user";
    protected String getTableName() { return tableName; }
    
    public void commitToDb() {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = getPreparedStatement(connection);
            int i = 1;
            statement.setString(i++, getUserName());
            statement.setString(i++, getPassword());
            statement.setBoolean(i++, getActive());
            super.commitToDb(statement);
        } catch (Exception ex) {
            Application.alertForError(
                "Something went wrong saving the user to the database.", ex.getMessage());
        }
    }
    
    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        int i = 0;
        setUserName(rs.getString(columnNames[i++]));
        setPassword(rs.getString(columnNames[i++]));
        setActive(rs.getBoolean(columnNames[i++]));
        super.fromResultSet(rs);
    }
}