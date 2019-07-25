package models;

import c195.*;
import java.time.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.beans.property.*;

public class Customer extends DBEntity implements Comparator {
    private ReadOnlyIntegerWrapper customerId = new ReadOnlyIntegerWrapper();
    private SimpleStringProperty customerName = new SimpleStringProperty();
    private SimpleIntegerProperty addressId = new SimpleIntegerProperty();
    private SimpleBooleanProperty active = new SimpleBooleanProperty();
    
    public int getCustomerId() { return customerId.get(); }
    protected void setCustomerId(int value) { customerId.set(value); }
    public ReadOnlyIntegerProperty customerIdProperty() { return customerId.getReadOnlyProperty(); }
    
    public String getCustomerName() { return customerName.get(); }
    public void setCustomerName(String value) { customerName.set(value); }
    public SimpleStringProperty customerNameProperty() { return customerName; }
    
    public int getAddressId() { return addressId.get(); }
    public void setAddressId(int value) { addressId.set(value); }
    public SimpleIntegerProperty addressIdProperty() { return addressId; }
    
    public boolean getActive() { return active.get(); }
    public void setActive(boolean value) { active.set(value); }
    public SimpleBooleanProperty activeProperty() { return active; }
    
    protected int getId() { return getCustomerId(); }
    protected void setId(int value) { setCustomerId(value); }
    
    private final String[] columnNames = new String[] { "customerName", "addressId", "active" };
    protected String[] getColumnNames() { return columnNames; }
    private final String tableName = "customer";
    protected String getTableName() { return tableName; }
    
    public void commitToDb() throws Exception {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            PreparedStatement statement = getPreparedStatement(connection);
            int i = 1;
            statement.setString(i++, getCustomerName());
            statement.setInt(i++, getAddressId());
            statement.setBoolean(i++, getActive());
            super.commitToDb(statement);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        int i = 0;
        setCustomerName(rs.getString(columnNames[i++]));
        setAddressId(rs.getInt(columnNames[i++]));
        setActive(rs.getBoolean(columnNames[i++]));
        super.fromResultSet(rs);
    }
    
    public static Customer fromId(int id) {
        Customer customer = new Customer();
        try {
            Connection connection = Application.getInstance().getDBConnection();
            String sql = "SELECT * FROM customer WHERE customerId = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.execute();
            ResultSet rs = statement.getResultSet();
            if (rs.next()) {
                customer.fromResultSet(rs);
            }
        } catch (Exception ex) {
            Application.alertForError(
                "The customer could not be retrieved from the database.", ex.getMessage());
        }
        return customer;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return ((Customer)o1).getCustomerName().compareTo(
                ((Customer)o2).getCustomerName());
    }
}