package models;

import c195.*;
import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import javafx.beans.property.*;

public class Address extends DBEntity {
    private ReadOnlyIntegerWrapper addressId = new ReadOnlyIntegerWrapper();
    private SimpleStringProperty address = new SimpleStringProperty();
    private SimpleStringProperty address2 = new SimpleStringProperty();
    private SimpleIntegerProperty cityId = new SimpleIntegerProperty();
    private SimpleStringProperty postalCode = new SimpleStringProperty();
    private SimpleStringProperty phone = new SimpleStringProperty();
    
    public int getAddressId() { return addressId.get(); }
    protected void setAddressId(int value) { addressId.set(value); }
    public ReadOnlyIntegerProperty addressIdProperty() { return addressId.getReadOnlyProperty(); }
    
    public String getAddress() { return address.get(); }
    public void setAddress(String value) { address.set(value); }
    public SimpleStringProperty addressProperty() { return address; }
    
    public String getAddress2() { return address2.get(); }
    public void setAddress2(String value) { address2.set(value); }
    public SimpleStringProperty address2Property() { return address2; }
    
    public int getCityId() { return cityId.get(); }
    public void setCityId(int value) { cityId.set(value); }
    public SimpleIntegerProperty cityIdProperty() { return cityId; }
    
    public String getPostalCode() { return postalCode.get(); }
    public void setPostalCode(String value) { postalCode.set(value); }
    public SimpleStringProperty postalCodeProperty() { return postalCode; }
    
    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }
    public SimpleStringProperty phoneProperty() { return phone; }
    
    protected int getId() { return getAddressId(); }
    protected void setId(int value) { setAddressId(value); }
    
    private final String[] columnNames = new String[] { "address", "address2", "cityId", "postalCode", "phone" };
    protected String[] getColumnNames() { return columnNames; }
    private final String tableName = "address";
    protected String getTableName() { return tableName; }
    
    public void commitToDb() throws Exception {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = getPreparedStatement(connection);
            int i = 1;
            statement.setString(i++, getAddress());
            statement.setString(i++, getAddress2());
            statement.setInt(i++, getCityId());
            statement.setString(i++, getPostalCode());
            statement.setString(i++, getPhone());
            super.commitToDb(statement);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        int i = 0;
        setAddress(rs.getString(columnNames[i++]));
        setAddress2(rs.getString(columnNames[i++]));
        setCityId(rs.getInt(columnNames[i++]));
        setPostalCode(rs.getString(columnNames[i++]));
        setPhone(rs.getString(columnNames[i++]));
        super.fromResultSet(rs);
    }
    
    public static Address fromId(int id) throws InvalidDataException {
        Address address = new Address();
        try (Connection connection = Application.getInstance().getDBConnection()) {
            String sql = "SELECT * FROM address WHERE addressId = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                address.fromResultSet(rs);
            } else {
                ArrayList<String> errors = new ArrayList<String>();
                errors.add("An address with that ID was not found.");
                throw new InvalidDataException(errors);
            }
        } catch (Exception ex) {
            Application.alertForError(
                "The address could not be retrieved from the database.", ex.getMessage());
        }
        return address;
    }
}