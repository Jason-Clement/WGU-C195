package models;

import c195.*;
import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import javafx.beans.property.*;

public class Country extends DBEntity {
    private ReadOnlyIntegerWrapper countryId = new ReadOnlyIntegerWrapper();
    private SimpleStringProperty country = new SimpleStringProperty();
    
    public int getCountryId() { return countryId.get(); }
    protected void setCountryId(int value) { countryId.set(value); }
    public ReadOnlyIntegerProperty countryIdProperty() { return countryId.getReadOnlyProperty(); }
    
    public String getCountry() { return country.get(); }
    public void setCountry(String value) { country.set(value); }
    public SimpleStringProperty countryProperty() { return country; }
    
    protected int getId() { return getCountryId(); }
    protected void setId(int value) { setCountryId(value); }
    
    private final String[] columnNames = new String[] { "country" };
    protected String[] getColumnNames() { return columnNames; }
    private final String tableName = "country";
    protected String getTableName() { return tableName; }
    
    public void commitToDb() throws Exception {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = getPreparedStatement(connection);
            int i = 1;
            statement.setString(i++, getCountry());
            super.commitToDb(statement);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public static Country getOrNew(String countryName) {
        Country country = new Country();
        countryName = countryName.trim();
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM country WHERE country = ?");
            statement.setString(1, countryName);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                country.fromResultSet(rs);
            } else {
                country.setCountry(countryName);
                country.getPreparedStatement(connection);
                country.commitToDb();
            }
        } catch (Exception ex) {
            Application.alertForError(
                "The country could not be retrieved from the database.", ex.getMessage());
        }
        return country;
    }
    
    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        int i = 0;
        setCountry(rs.getString(columnNames[i++]));
        super.fromResultSet(rs);
    }
}