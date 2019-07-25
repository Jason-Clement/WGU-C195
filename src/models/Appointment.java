package models;

import c195.*;
import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import javafx.beans.property.*;

public class Appointment extends DBEntity {
    private ReadOnlyIntegerWrapper appointmentId = new ReadOnlyIntegerWrapper();
    private SimpleIntegerProperty customerId = new SimpleIntegerProperty();
    private SimpleIntegerProperty userId = new SimpleIntegerProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleStringProperty description = new SimpleStringProperty();
    private SimpleStringProperty location = new SimpleStringProperty();
    private SimpleStringProperty contact = new SimpleStringProperty();
    private SimpleStringProperty type = new SimpleStringProperty();
    private SimpleStringProperty url = new SimpleStringProperty();
    private SimpleObjectProperty<ZonedDateTime> start = new SimpleObjectProperty();
    private SimpleObjectProperty<ZonedDateTime> end = new SimpleObjectProperty();
    
    public int getAppointmentId() { return appointmentId.get(); }
    protected void setAppointmentId(int value) { appointmentId.set(value); }
    public ReadOnlyIntegerProperty appointmentIdProperty() { return appointmentId.getReadOnlyProperty(); }
    
    public int getCustomerId() { return customerId.get(); }
    public void setCustomerId(int value) { customerId.set(value); }
    public SimpleIntegerProperty customerIdProperty() { return customerId; }
    
    public int getUserId() { return userId.get(); }
    public void setUserId(int value) { userId.set(value); }
    public SimpleIntegerProperty userIdProperty() { return userId; }
    
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public SimpleStringProperty titleProperty() { return title; }
    
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }
    public SimpleStringProperty descriptionProperty() { return description; }
    
    public String getLocation() { return location.get(); }
    public void setLocation(String value) { location.set(value); }
    public SimpleStringProperty locationProperty() { return location; }
    
    public String getContact() { return contact.get(); }
    public void setContact(String value) { contact.set(value); }
    public SimpleStringProperty contactProperty() { return contact; }
    
    public String getType() { return type.get(); }
    public void setType(String value) { type.set(value); }
    public SimpleStringProperty typeProperty() { return type; }
    
    public String getUrl() { return url.get(); }
    public void setUrl(String value) { url.set(value); }
    public SimpleStringProperty urlProperty() { return url; }
    
    public ZonedDateTime getStart() { return start.get(); }
    public void setStart(ZonedDateTime value) { start.set(value); }
    public SimpleObjectProperty<ZonedDateTime> startProperty() { return start; }
    
    public ZonedDateTime getEnd() { return end.get(); }
    public void setEnd(ZonedDateTime value) { end.set(value); }
    public SimpleObjectProperty<ZonedDateTime> endProperty() { return end; }
    
    protected int getId() { return getAppointmentId(); }
    protected void setId(int value) { setAppointmentId(value); }
    
    private final String[] columnNames = new String[] { "customerId", "userId",
        "title", "description", "location", "contact", "type", "url", "start", "end" };
    protected String[] getColumnNames() { return columnNames; } 
    private final String tableName = "appointment";
    protected String getTableName() { return tableName; }
    
    public List<Appointment> checkForOverlap(String idColumnName, int id, ZonedDateTime newStart, ZonedDateTime newEnd) {
        List<Appointment> list = new ArrayList<Appointment>();
        try (Connection connection = Application.getInstance().getDBConnection()) {
            String sql = "SELECT * FROM appointment WHERE " + idColumnName + " = ?"
                    + " AND ((start >= ? AND start <= ?) OR (end >= ? AND end <= ?))"
                    + " AND appointmentId <> ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            int i = 1;
            statement.setInt(i++, id);
            Timestamp startUtc = Application.toTimestamp(newStart);
            Timestamp endUtc = Application.toTimestamp(newEnd);
            statement.setTimestamp(i++, startUtc);
            statement.setTimestamp(i++, endUtc);
            statement.setTimestamp(i++, startUtc);
            statement.setTimestamp(i++, endUtc);
            statement.setInt(i++, getAppointmentId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.fromResultSet(rs);
                list.add(appointment);
            }
        } catch (Exception ex) {
            Application.alertForError(
                "An error occurred retrieving data from the database", ex.getMessage());
        }
        return list;
    }
    
    public void commitToDb() throws Exception {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = getPreparedStatement(connection);
            int i = 1;
            statement.setInt(i++, getCustomerId());
            statement.setInt(i++, getUserId());
            statement.setString(i++, getTitle());
            statement.setString(i++, getDescription());
            statement.setString(i++, getLocation());
            statement.setString(i++, getContact());
            statement.setString(i++, getType());
            statement.setString(i++, getUrl());
            statement.setTimestamp(i++, Application.toTimestamp(getStart()));
            statement.setTimestamp(i++, Application.toTimestamp(getEnd()));
            super.commitToDb(statement);
        } catch (Exception ex) {
            throw ex;
        }
    }
        
    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        int i = 0;
        setCustomerId(rs.getInt(columnNames[i++]));
        setUserId(rs.getInt(columnNames[i++]));
        setTitle(rs.getString(columnNames[i++]));
        setDescription(rs.getString(columnNames[i++]));
        setLocation(rs.getString(columnNames[i++]));
        setContact(rs.getString(columnNames[i++]));
        setType(rs.getString(columnNames[i++]));
        setUrl(rs.getString(columnNames[i++]));
        setStart(Application.toZonedDateTime(rs.getTimestamp(columnNames[i++])));
        setEnd(Application.toZonedDateTime(rs.getTimestamp(columnNames[i++])));
        super.fromResultSet(rs);
    }
}