package c195;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import models.*;
import javafx.util.Callback;

public class FXMLCustomersController {

    private WindowController controller;
    private int id = 0;
    private ObservableList<Customer> customers;
    private FilteredList<Customer> filteredCustomers;
    private SortedList<Customer> sortedCustomers;
    private FXMLCustomersModel selectedCustomerModel = new FXMLCustomersModel();
    private Customer selectedCustomer;
    
    @FXML private CheckBox activeCheckbox;
    @FXML private TextField address2Box;
    @FXML private TextField addressBox;
    @FXML private Button cancelButton;
    @FXML private TextField cityBox;
    @FXML private TextField countryBox;
    @FXML private AnchorPane customerListPane;
    @FXML private ListView<Customer> customerList;
    @FXML private TextField customerNameBox;
    @FXML private TextField phoneBox;
    @FXML private TextField postalCodeBox;
    @FXML private Button saveButton;
    @FXML private CheckBox showInactiveCheckbox;
    @FXML private Label errorBox;
    @FXML private Button addNewButton;
    
    @FXML
    void initialize() {
        assert activeCheckbox != null : "fx:id=\"activeCheckbox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert address2Box != null : "fx:id=\"address2Box\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert addressBox != null : "fx:id=\"addressBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert cityBox != null : "fx:id=\"cityBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert countryBox != null : "fx:id=\"countryBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert customerList != null : "fx:id=\"customerList\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert customerListPane != null : "fx:id=\"customerListPane\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert customerNameBox != null : "fx:id=\"customerNameBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert phoneBox != null : "fx:id=\"phoneBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert postalCodeBox != null : "fx:id=\"postalCodeBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert showInactiveCheckbox != null : "fx:id=\"showInactiveCheckbox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert errorBox != null : "fx:id=\"errorBox\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        assert addNewButton != null : "fx:id=\"addNewButton\" was not injected: check your FXML file 'FXMLCustomers.fxml'.";
        
        customers = FXCollections.observableArrayList(c -> new Observable[] { c.customerNameProperty(), c.activeProperty() });
        filteredCustomers = customers.filtered(c -> c.getActive());
        sortedCustomers = filteredCustomers.sorted(new Comparator<Customer>() {
            @Override
            public int compare(Customer o1, Customer o2) {
                return o1.getCustomerName().compareTo(o2.getCustomerName());
            }
            
        });
        customerList.setItems(sortedCustomers);
        customerList.setCellFactory(new Callback<ListView<Customer>, ListCell<Customer>>() {
            @Override
            public ListCell<Customer> call(ListView<Customer> list) {
                ListCell<Customer> cell = new ListCell<Customer>() {
                    @Override
                    protected void updateItem(Customer c, boolean empty) {
                        super.updateItem(c, empty);
                        if (c == null || empty) {
                            setText("");
                        } else {
                            setText(c.getCustomerName());
                            if (c.getActive())
                                setTextFill(Paint.valueOf("black"));
                            else
                                setTextFill(Paint.valueOf("gray"));
                        }
                    }
                };
                return cell;
            }
        });
        customerList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        customerList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
            @Override
            public void changed(ObservableValue<? extends Customer> observable, Customer oldCustomer, Customer newCustomer) {
                if (newCustomer == null)
                    selectedCustomerModel.reset();
                else
                    setCustomer(newCustomer);
            }
        });
        updateFilter();
        
        customerNameBox.textProperty().bindBidirectional(selectedCustomerModel.customerNameProperty());
        addressBox.textProperty().bindBidirectional(selectedCustomerModel.addressProperty());
        address2Box.textProperty().bindBidirectional(selectedCustomerModel.address2Property());
        cityBox.textProperty().bindBidirectional(selectedCustomerModel.cityProperty());
        postalCodeBox.textProperty().bindBidirectional(selectedCustomerModel.postalCodeProperty());
        countryBox.textProperty().bindBidirectional(selectedCustomerModel.countryProperty());
        phoneBox.textProperty().bindBidirectional(selectedCustomerModel.phoneProperty());
        activeCheckbox.selectedProperty().bindBidirectional(selectedCustomerModel.activeProperty());
        errorBox.textProperty().bind(selectedCustomerModel.errorsProperty());
    }
    
    private boolean isLoaded = false;
    public boolean getIsLoaded() { return isLoaded; }
    private FXMLMainController mainController;
    public void load(FXMLMainController mainController) {
        this.mainController = mainController;
        loadCustomers();
        selectedCustomerModel.reset();
        customerNameBox.requestFocus();
        isLoaded = true;
    }
    
    private void updateFilter() {
        if (showInactiveCheckbox.isSelected())
            filteredCustomers.setPredicate((Customer c) -> true); // shortcut to always true predicate
        else
            filteredCustomers.setPredicate((Customer c) -> c.getActive()); // lambda makes it easy to specify the active property
    }
    
    private void loadCustomers() {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            Statement stat;
            stat = connection.createStatement();
            stat.execute("SELECT * FROM customer");
            ResultSet rs = stat.getResultSet();
            while (rs.next()) {
                Customer c = new Customer();
                c.fromResultSet(rs);
                customers.add(c);
            }
        } catch (Exception ex) {
            Application.alertForError(
                "An error occurred retrieving the customers from the database.", ex.getMessage());
        }
    }
    
    public void selectCustomer(Customer customer) {
        if (!customer.getActive()) {
            showInactiveCheckbox.setSelected(true);
            updateFilter();
        }
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId() == customer.getCustomerId()) {
                customerList.getSelectionModel().select(customers.get(i));
                break;
            }
        }
    }
    
    private void setCustomer(Customer customer) {
        selectedCustomerModel.loadFromId(customer.getCustomerId());
    }
    
    @FXML
    void addNewButtonActivated(ActionEvent event) {
        customerList.getSelectionModel().clearSelection();
        selectedCustomerModel.reset();
        customerNameBox.requestFocus();
    }
    
    @FXML
    private void saveButtonActivated(ActionEvent event) {
        try {
            selectedCustomerModel.save();
            selectedCustomerModel.reset();
            customerList.refresh();
        } catch (InvalidDataException ex) {
            selectedCustomerModel.setErrors(String.join("\r\n", ex.getErrors()));
        }
    }
    
    @FXML
    private void cancelButtonActivated(ActionEvent event) {
        selectedCustomerModel.reset();
    }

    @FXML
    private void showInactiveChanged(ActionEvent event) {
        updateFilter();
    }
    
    private class FXMLCustomersModel {
        private final IntegerProperty customerId = new SimpleIntegerProperty();
        public final int getCustomerId() { return customerId.get(); }
        public final void setCustomerId(int value) { customerId.set(value); }
        public IntegerProperty customerIdProperty() { return customerId; }
        
        private final StringProperty customerName = new SimpleStringProperty();
        public final String getCustomerName() { return customerName.get(); }
        public final void setCustomerName(String value) { customerName.set(value); }
        public StringProperty customerNameProperty() { return customerName; }
        
        private final IntegerProperty addressId = new SimpleIntegerProperty();
        public final int getAddressId() { return addressId.get(); }
        public final void setAddressId(int value) { addressId.set(value); }
        public IntegerProperty addressIdProperty() { return addressId; }
        
        private final StringProperty address = new SimpleStringProperty();
        public final String getAddress() { return address.get(); }
        public final void setAddress(String value) { address.set(value); }
        public StringProperty addressProperty() { return address; }
        
        private final StringProperty address2 = new SimpleStringProperty();
        public final String getAddress2() { return address2.get(); }
        public final void setAddress2(String value) { address2.set(value); }
        public StringProperty address2Property() { return address2; }
        
        private final StringProperty city = new SimpleStringProperty();
        public final String getCity() { return city.get(); }
        public final void setCity(String value) { city.set(value); }
        public StringProperty cityProperty() { return city; }
        
        private final StringProperty postalCode = new SimpleStringProperty();
        public final String getPostalCode() { return postalCode.get(); }
        public final void setPostalCode(String value) { postalCode.set(value); }
        public StringProperty postalCodeProperty() { return postalCode; }
        
        private final StringProperty country = new SimpleStringProperty();
        public final String getCountry() { return country.get(); }
        public final void setCountry(String value) { country.set(value); }
        public StringProperty countryProperty() { return country; }
        
        private final StringProperty phone = new SimpleStringProperty();
        public final String getPhone() { return phone.get(); }
        public final void setPhone(String value) { phone.set(value); }
        public StringProperty phoneProperty() { return phone; }
        
        private final BooleanProperty active = new SimpleBooleanProperty();
        public final boolean getActive() { return active.get(); }
        public final void setActive(boolean value) { active.set(value); }
        public BooleanProperty activeProperty() { return active; }
        
        private final StringProperty errors = new SimpleStringProperty();
        public final String getErrors() { return errors.get(); }
        public final void setErrors(String value) { errors.set(value); }
        public StringProperty errorsProperty() { return errors; }
        
        public void loadFromId(int id) {
            try (Connection connection = Application.getInstance().getDBConnection()) {
                String sql = "SELECT c.customerId, "
                        + "c.customerName, "
                        + "a.addressId, "
                        + "a.address, "
                        + "a.address2, "
                        + "c1.city, "
                        + "a.postalCode, "
                        + "c2.country, "
                        + "a.phone, "
                        + "c.active "
                        + "FROM customer c "
                        + "INNER JOIN address a ON a.addressId = c.addressId "
                        + "INNER JOIN city c1 ON c1.cityId = a.cityId "
                        + "INNER JOIN country c2 ON c2.countryId = c1.countryId "
                        + "WHERE customerId = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, id);
                statement.execute();
                ResultSet rs = statement.getResultSet();
                if (!rs.next()) {
                    Application.alertForError("That customer ID (" + id + ") does not exist.", null);
                    return;
                }
                int i = 1;
                setCustomerId(rs.getInt(i++));
                String s = rs.getString(i++);
                setCustomerName(s == null ? "" : s);
                setAddressId(rs.getInt(i++));
                s = rs.getString(i++);
                setAddress(s == null ? "" : s);
                s = rs.getString(i++);
                setAddress2(s == null ? "" : s);
                s = rs.getString(i++);
                setCity(s == null ? "" : s);
                s = rs.getString(i++);
                setPostalCode(s == null ? "" : s);
                s = rs.getString(i++);
                setCountry(s == null ? "" : s);
                s = rs.getString(i++);
                setPhone(s == null ? "" : s);
                setActive(rs.getBoolean(i++));
            } catch (Exception ex) {
                Application.alertForError(
                    "The customer could not be loaded.", ex.getMessage());
            }
        }
        
        public void reset() {
            setCustomerId(0);
            setCustomerName("");
            setAddressId(0);
            setAddress("");
            setAddress2("");
            setCity("");
            setPostalCode("");
            setCountry("");
            setPhone("");
            setErrors("");
            setActive(true);
            customerList.getSelectionModel().clearSelection();
        }
        
        public void save() throws InvalidDataException {
            ArrayList<String> errors = new ArrayList<String>();
            int customerId = getCustomerId();
            String customerName = getCustomerName().trim();
            int addressId = getAddressId();
            String address = getAddress().trim();
            String address2 = getAddress2().trim();
            String city = getCity().trim();
            String postalCode = getPostalCode().trim();
            String country = getCountry().trim();
            String phone = getPhone().trim();
            boolean active = getActive();
            if (customerName.isEmpty())
                errors.add("Customer name is required");
            if (address.isEmpty())
                errors.add("Address is required");
            if (city.isEmpty())
                errors.add("City is required");
            if (postalCode.isEmpty())
                errors.add("Postal code is required");
            if (country.isEmpty())
                errors.add("Country is required");
            if (errors.size() > 0) {
                throw new InvalidDataException(errors);
            }
            try {
                Country countryObj = Country.getOrNew(country);
                City cityObj = City.getOrNew(city, countryObj.getCountryId());
                Address addressObj = new Address();
                if (addressId > 0) {
                    addressObj = Address.fromId(addressId);
                }
                addressObj.setAddress(address);
                addressObj.setAddress2(address2);
                addressObj.setCityId(cityObj.getCityId());
                addressObj.setPostalCode(postalCode);
                addressObj.setPhone(phone);
                addressObj.commitToDb();
                Customer c = new Customer();
                if (customerId > 0) {
                    c = customerList.getSelectionModel().getSelectedItem();
                } else {
                    customers.add(c);
                }
                c.setCustomerName(customerName);
                c.setAddressId(addressObj.getAddressId());
                c.setActive(active);
                c.commitToDb();
                selectedCustomer = c;
                customerList.refresh();
            } catch (Exception ex) {
                Application.alertForError(
                    "An error occurred saving the customer to the database.", ex.getMessage());
            }
        }
    }
}