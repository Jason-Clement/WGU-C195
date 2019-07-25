package models;

import c195.Application;
import java.time.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;
import javafx.beans.property.*;

public abstract class DBEntity {
    
    private ReadOnlyObjectWrapper<ZonedDateTime> createDate = new ReadOnlyObjectWrapper();
    private ReadOnlyStringWrapper createdBy = new ReadOnlyStringWrapper();
    private ReadOnlyObjectWrapper<ZonedDateTime> lastUpdate = new ReadOnlyObjectWrapper();
    private ReadOnlyStringWrapper lastUpdateBy = new ReadOnlyStringWrapper();;
    
    private boolean isNew = true;
    public boolean getIsNew() { return isNew; }
    
    public DBEntity() { }
    
    public ZonedDateTime getCreateDate() { return createDate.get(); }
    protected void setCreateDate(ZonedDateTime value) { this.createDate.set(value); }
    public ReadOnlyObjectProperty<ZonedDateTime> createDateProperty() { return createDate.getReadOnlyProperty(); }
    
    public String getCreatedBy() { return createdBy.get(); }
    protected void setCreatedBy(String value) { createdBy.set(value); }
    public ReadOnlyStringProperty createdByProperty() { return createdBy.getReadOnlyProperty(); }
    
    public ZonedDateTime getLastUpdate() { return lastUpdate.get(); }
    protected void setLastUpdate(ZonedDateTime value) { this.lastUpdate.set(value); }
    public ReadOnlyObjectProperty<ZonedDateTime> lastUpdateProperty() { return lastUpdate.getReadOnlyProperty(); }
    
    public String getLastUpdateBy() { return lastUpdateBy.get(); }
    protected void setLastUpdateBy(String value) { lastUpdateBy.set(value); }
    public ReadOnlyStringProperty lastUpdateByProperty() { return lastUpdateBy.getReadOnlyProperty(); }
    
    // I guess I'll avoid using reflection.
    abstract int getId();
    abstract void setId(int value);
    abstract String[] getColumnNames();
    abstract String getTableName();
    
    protected String getIdColumnName() {
        return getTableName() + "Id";
    }
    
    protected void fromResultSet(ResultSet rs) throws SQLException {
        setId(rs.getInt(getIdColumnName()));
        setCreateDate(Application.toZonedDateTime(rs.getTimestamp("createDate")));
        setCreatedBy(rs.getString("createdBy"));
        setLastUpdate(Application.toZonedDateTime(rs.getTimestamp("lastUpdate")));
        setLastUpdateBy(rs.getString("lastUpdateBy"));
        isNew = false;
    }
    
    protected void commitToDb(PreparedStatement statement) throws Exception {
        statement.executeUpdate();
        
        if (isNew) {
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    setId(rs.getInt(1));
                    isNew = false;
                } else {
                    throw new Exception("The result set returned no generated id.");
                }
            }
        }
        setLastUpdate(ZonedDateTime.now());
    }
    
    protected PreparedStatement getPreparedStatement(Connection connection) throws SQLException {
        if (this.isNew)
            return getPreparedInsertStatement(connection);
        return getPreparedUpdateStatement(connection);
    }
    
    private PreparedStatement getPreparedInsertStatement(Connection connection) throws SQLException {
        String[] columns = this.getColumnNames();
        StringBuilder sql = new StringBuilder();
        int count = columns.length + 3;
        sql.append("INSERT INTO " + getTableName() + " (" + String.join(", ", columns)
                + ", createDate, createdBy, lastUpdateBy) VALUES(");
        for (int i = 1; i <= count; i++) {
            sql.append("?");
            if (i < count)
                sql.append(",");
        }
        sql.append(")");
        PreparedStatement statement = connection.prepareStatement(sql.toString(), new String[] { getIdColumnName(), "lastUpdate" });
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC));
        String userName = Application.getInstance().getActiveUser().getUserName();
        statement.setTimestamp(count - 2, timestamp);
        statement.setString(count - 1, userName);
        statement.setString(count, userName);
        
        return statement;
    }
    
    private PreparedStatement getPreparedUpdateStatement(Connection connection) throws SQLException {
        String[] columns = this.getColumnNames();
        StringBuilder sql = new StringBuilder();
        int count = columns.length;
        sql.append("UPDATE " + getTableName() + " SET ");
        for (int i = 1; i <= count; i++) {
            sql.append(columns[i - 1] + " = ?,");
        }
        sql.append(" lastUpdateBy = ?");
        sql.append(" WHERE " + this.getIdColumnName() + " = ?");
        PreparedStatement statement = connection.prepareStatement(sql.toString(), new String[] { getIdColumnName(), "lastUpdate" });
        String userName = Application.getInstance().getActiveUser().getUserName();
        statement.setString(count + 1, userName);
        statement.setInt(count + 2, this.getId());
        
        return statement;
    }
    
    public void deleteFromDb() throws Exception {
        if (isNew) return;
        try (Connection connection = Application.getInstance().getDBConnection()) {
            String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, getId());
            statement.execute();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
