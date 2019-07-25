package models;

import c195.*;
import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.*;

public class City extends DBEntity {
    private ReadOnlyIntegerWrapper cityId = new ReadOnlyIntegerWrapper();
    private SimpleStringProperty city = new SimpleStringProperty();
    private SimpleIntegerProperty countryId = new SimpleIntegerProperty();
    
    public int getCityId() { return cityId.get(); }
    protected void setCityId(int value) { cityId.set(value); }
    public ReadOnlyIntegerProperty customerIdProperty() { return cityId.getReadOnlyProperty(); }
    
    public String getCity() { return city.get(); }
    public void setCity(String value) { city.set(value); }
    public SimpleStringProperty cityProperty() { return city; }
    
    public int getCountryId() { return countryId.get(); }
    public void setCountryId(int value) { countryId.set(value); }
    public SimpleIntegerProperty countryIdProperty() { return countryId; }
    
    protected int getId() { return getCityId(); }
    protected void setId(int value) { setCityId(value); }
    
    private final String[] columnNames = new String[] { "city", "countryId" };
    protected String[] getColumnNames() { return columnNames; }
    private final String tableName = "city";
    protected String getTableName() { return tableName; }
    
    public void commitToDb() throws Exception {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = getPreparedStatement(connection);
            int i = 1;
            statement.setString(i++, getCity());
            statement.setInt(i++, getCountryId());
            super.commitToDb(statement);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public static City getOrNew(String cityName, int countryId) {
        City city = new City();
        cityName = cityName.trim();
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM city WHERE city = ? AND countryId = ?");
            statement.setString(1, cityName);
            statement.setInt(2, countryId);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                city.fromResultSet(rs);
            } else {
                city.setCity(cityName);
                city.setCountryId(countryId);
                city.getPreparedStatement(connection);
                city.commitToDb();
            }
        } catch (Exception ex) {
            Application.alertForError(
                "The city could not be retrieved from the database.", ex.getMessage());
        }
        return city;
    }
    
    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        int i = 0;
        setCity(rs.getString(columnNames[i++]));
        setCountryId(rs.getInt(columnNames[i++]));
        super.fromResultSet(rs);
    }
}