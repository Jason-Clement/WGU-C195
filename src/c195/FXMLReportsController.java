package c195;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import models.*;

public class FXMLReportsController {

    @FXML private VBox customersBoxWrapper;
    @FXML private ComboBox<Customer> customersBox;
    @FXML private CheckBox customerUpcomingOnlyCheckbox;
    @FXML private VBox usersBoxWrapper;
    @FXML private ComboBox<User> usersBox;
    @FXML private CheckBox userUpcomingOnlyCheckbox;
    @FXML private Label headingBox;
    @FXML private TextArea reportTextArea;
    @FXML private TableView<AppointmentsByMonthModel> appointmentsByMonthGrid;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthYearColumn;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth1Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth2Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth3Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth4Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth5Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth6Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth7Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth8Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth9Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth10Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth11Column;
    @FXML private TableColumn<AppointmentsByMonthModel, Integer> appointmentsByMonthMonth12Column;
        
    @FXML
    void appointmentsByMonthActivated(ActionEvent event) {
        showAppointmentsByMonth();
    }

    @FXML
    void appointmentsPerUserActivated(ActionEvent event) {
        showAppointmentsPerUser();
    }

    @FXML
    void userChanged(ActionEvent event) {
        if (usersBox.getValue() != null)
            populateAppointmentsPerUserReport(usersBox.getValue());
    }

    @FXML
    void appointmentsPerCustomerActivated(ActionEvent event) {
        showAppointmentsPerCustomer();
    }

    @FXML
    void customerChanged(ActionEvent event) {
        if (customersBox.getValue() != null)
            populateAppointmentsPerCustomerReport(customersBox.getValue());
    }
    
    @FXML
    void initialize() {
        assert headingBox != null : "fx:id=\"headingBox\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert customersBoxWrapper != null : "fx:id=\"consultantsBoxWrapper\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert customersBox != null : "fx:id=\"consultantsBox\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert customerUpcomingOnlyCheckbox != null : "fx:id=\"customerUpcomingOnlyCheckbox\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert usersBoxWrapper != null : "fx:id=\"usersBoxWrapper\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert usersBox != null : "fx:id=\"usersBox\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert reportTextArea != null : "fx:id=\"reportTextArea\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert userUpcomingOnlyCheckbox != null : "fx:id=\"userUpcomingOnlyCheckbox\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthGrid != null : "fx:id=\"appointmentsByMonthGrid\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthYearColumn != null : "fx:id=\"appointmentsByMonthYearColumn\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth1Column != null : "fx:id=\"appointmentsByMonthMonth1Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth2Column != null : "fx:id=\"appointmentsByMonthMonth2Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth3Column != null : "fx:id=\"appointmentsByMonthMonth3Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth4Column != null : "fx:id=\"appointmentsByMonthMonth4Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth5Column != null : "fx:id=\"appointmentsByMonthMonth5Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth6Column != null : "fx:id=\"appointmentsByMonthMonth6Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth7Column != null : "fx:id=\"appointmentsByMonthMonth7Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth8Column != null : "fx:id=\"appointmentsByMonthMonth8Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth9Column != null : "fx:id=\"appointmentsByMonthMonth9Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth10Column != null : "fx:id=\"appointmentsByMonthMonth10Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth11Column != null : "fx:id=\"appointmentsByMonthMonth11Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";
        assert appointmentsByMonthMonth12Column != null : "fx:id=\"appointmentsByMonthMonth12Column\" was not injected: check your FXML file 'FXMLReports.fxml'.";

        hideVBox(customersBoxWrapper);
        hideVBox(usersBoxWrapper);
        
        appointmentsByMonthYearColumn.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("year"));
        appointmentsByMonthMonth1Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month1"));
        appointmentsByMonthMonth2Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month2"));
        appointmentsByMonthMonth3Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month3"));
        appointmentsByMonthMonth4Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month4"));
        appointmentsByMonthMonth5Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month5"));
        appointmentsByMonthMonth6Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month6"));
        appointmentsByMonthMonth7Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month7"));
        appointmentsByMonthMonth8Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month8"));
        appointmentsByMonthMonth9Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month9"));
        appointmentsByMonthMonth10Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month10"));
        appointmentsByMonthMonth11Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month11"));
        appointmentsByMonthMonth12Column.setCellValueFactory(new PropertyValueFactory<AppointmentsByMonthModel, Integer>("month12"));
        
        usersBox.setCellFactory((ListView<User> list) -> new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(u == null || empty ? "" : u.getUserName());
            }
        });
        usersBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                setText(u == null || empty ? "" : u.getUserName());
            }
        });
        
        customersBox.setCellFactory((ListView<Customer> list) -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer u, boolean empty) {
                super.updateItem(u, empty);
                setText(u == null || empty ? "" : u.getCustomerName());
            }
        });
        customersBox.setButtonCell(new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer u, boolean empty) {
                super.updateItem(u, empty);
                setText(u == null || empty ? "" : u.getCustomerName());
            }
        });
    }
    
    private boolean isLoaded = false;
    public boolean getIsLoaded() { return isLoaded; }
    public void load() {
        showAppointmentsByMonth();
        isLoaded = true;
    }
    
    private void hideVBox(VBox box) {
        box.setManaged(false);
        box.setVisible(false);
    }
    
    private void showVBox(VBox box) {
        box.setManaged(true);
        box.setVisible(true);
    }
    
// <editor-fold defaultstate="expanded" desc="Appointments by Month">
    
    public class AppointmentsByMonthModel {
        
        private SimpleIntegerProperty year = new SimpleIntegerProperty(0);
        public int getYear() { return year.get(); }
        public void setYear(int value) { year.set(value); }
        public SimpleIntegerProperty yearProperty() { return year; }
        
        private SimpleIntegerProperty month1 = new SimpleIntegerProperty(0);
        public int getMonth1() { return month1.get(); }
        public void setMonth1(int value) { month1.set(value); }
        public SimpleIntegerProperty month1Property() { return month1; }
        
        private SimpleIntegerProperty month2 = new SimpleIntegerProperty(0);
        public int getMonth2() { return month2.get(); }
        public void setMonth2(int value) { month2.set(value); }
        public SimpleIntegerProperty month2Property() { return month2; }
        
        private SimpleIntegerProperty month3 = new SimpleIntegerProperty(0);
        public int getMonth3() { return month3.get(); }
        public void setMonth3(int value) { month3.set(value); }
        public SimpleIntegerProperty month3Property() { return month3; }
        
        private SimpleIntegerProperty month4 = new SimpleIntegerProperty(0);
        public int getMonth4() { return month4.get(); }
        public void setMonth4(int value) { month4.set(value); }
        public SimpleIntegerProperty month4Property() { return month4; }
        
        private SimpleIntegerProperty month5 = new SimpleIntegerProperty(0);
        public int getMonth5() { return month5.get(); }
        public void setMonth5(int value) { month5.set(value); }
        public SimpleIntegerProperty month5Property() { return month5; }
        
        private SimpleIntegerProperty month6 = new SimpleIntegerProperty(0);
        public int getMonth6() { return month6.get(); }
        public void setMonth6(int value) { month6.set(value); }
        public SimpleIntegerProperty month6Property() { return month6; }
        
        private SimpleIntegerProperty month7 = new SimpleIntegerProperty(0);
        public int getMonth7() { return month7.get(); }
        public void setMonth7(int value) { month7.set(value); }
        public SimpleIntegerProperty month7Property() { return month7; }
        
        private SimpleIntegerProperty month8 = new SimpleIntegerProperty(0);
        public int getMonth8() { return month8.get(); }
        public void setMonth8(int value) { month8.set(value); }
        public SimpleIntegerProperty month8Property() { return month8; }
        
        private SimpleIntegerProperty month9 = new SimpleIntegerProperty(0);
        public int getMonth9() { return month9.get(); }
        public void setMonth9(int value) { month9.set(value); }
        public SimpleIntegerProperty month9Property() { return month9; }
        
        private SimpleIntegerProperty month10 = new SimpleIntegerProperty(0);
        public int getMonth10() { return month10.get(); }
        public void setMonth10(int value) { month10.set(value); }
        public SimpleIntegerProperty month10Property() { return month10; }
        
        private SimpleIntegerProperty month11 = new SimpleIntegerProperty(0);
        public int getMonth11() { return month11.get(); }
        public void setMonth11(int value) { month11.set(value); }
        public SimpleIntegerProperty month11Property() { return month11; }
        
        private SimpleIntegerProperty month12 = new SimpleIntegerProperty(0);
        public int getMonth12() { return month12.get(); }
        public void setMonth12(int value) { month12.set(value); }
        public SimpleIntegerProperty month12Property() { return month12; }
        
        public AppointmentsByMonthModel(int year) {
            setYear(year);
        }
        
        public void incrementMonth(int month) {
            switch (month) {
                case 1: setMonth1(getMonth1() + 1); break;
                case 2: setMonth2(getMonth2() + 1); break;
                case 3: setMonth3(getMonth3() + 1); break;
                case 4: setMonth4(getMonth4() + 1); break;
                case 5: setMonth5(getMonth5() + 1); break;
                case 6: setMonth6(getMonth6() + 1); break;
                case 7: setMonth7(getMonth7() + 1); break;
                case 8: setMonth8(getMonth8() + 1); break;
                case 9: setMonth9(getMonth9() + 1); break;
                case 10: setMonth10(getMonth10() + 1); break;
                case 11: setMonth11(getMonth11() + 1); break;
                case 12: setMonth12(getMonth12() + 1); break;
            }
        }
    }
    
    private void showAppointmentsByMonth() {
        appointmentsByMonthGrid.setVisible(true);
        reportTextArea.setVisible(false);
        hideVBox(customersBoxWrapper);
        hideVBox(usersBoxWrapper);
        try (Connection connection = Application.getInstance().getDBConnection()) {
            headingBox.setText("Number of Appointments Each Month");
            ObservableList<AppointmentsByMonthModel> list = FXCollections.observableArrayList();
            String sql = "SELECT start FROM appointment";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                ZonedDateTime start = Application.toZonedDateTime(rs.getTimestamp("start"));
                int year = start.getYear();
                int month = start.getMonthValue();
                AppointmentsByMonthModel m = new AppointmentsByMonthModel(year);
                boolean found = false;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getYear() == year) {
                        m = list.get(i);
                        found = true;
                        break;
                    }
                }
                if (!found)
                    list.add(m);
                m.incrementMonth(month);
            }
            appointmentsByMonthGrid.setItems(list);
            appointmentsByMonthGrid.refresh();
        } catch (Exception ex) {
            Application.alertForError(
                "The database could not be accessed.", ex.getMessage());
        }
    }
    
// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Appointments per User">
    
    private void showAppointmentsPerUser() {
        appointmentsByMonthGrid.setVisible(false);
        reportTextArea.setVisible(true);
        reportTextArea.clear();
        hideVBox(customersBoxWrapper);
        showVBox(usersBoxWrapper);
        loadUsers();
        User activeUser = Application.getInstance().getActiveUser();
        for (int i = 0; i < usersBox.getItems().size(); i++) {
            if (usersBox.getItems().get(i).getUserId() == activeUser.getUserId()) {
                usersBox.getSelectionModel().select(i);
                break;
            }
        }
    }
    
    private void populateAppointmentsPerUserReport(User user) {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            boolean showUpcomingOnly = userUpcomingOnlyCheckbox.isSelected();
            headingBox.setText((showUpcomingOnly ? "Upcoming " : "All ")
                    + "Appointments for " + user.getUserName());
            String sql = "SELECT a.*, c.customerName FROM appointment a INNER JOIN customer c "
                    + "ON c.customerId = a.customerId WHERE userId = ? AND c.active = 1 "
                    + "ORDER BY start";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, user.getUserId());
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            String sep = System.lineSeparator();
            LocalDate lastDate = LocalDate.MIN;
            LocalDate yesterday = LocalDate.now().minusDays(1);
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.fromResultSet(rs);
                String customer = rs.getString("customerName");
                ZonedDateTime start = appointment.getStart();
                ZonedDateTime end = appointment.getEnd();
                LocalDate date = start.toLocalDate();
                if (!showUpcomingOnly || date.isAfter(yesterday)) {
                    if (date.isAfter(lastDate)) {
                        sb.append(sep + date.format(DateTimeFormatter.ofPattern("MMM d, y")) + sep);
                        lastDate = date;
                    }
                    sb.append(start.format(DateTimeFormatter.ofPattern("h:mma")) + "-");
                    sb.append(end.format(DateTimeFormatter.ofPattern("h:mma")) + "\t");
                    sb.append(appointment.getTitle());
                    sb.append(" with ");
                    sb.append(customer);
                    sb.append(sep);
                }
            }
            reportTextArea.setText(sb.toString());
        } catch (Exception ex) {
            Application.alertForError(
                "Something went wrong connecting to the database.", ex.getMessage());
        }
    }
    
    private void showAppointmentsPerCustomer() {
        appointmentsByMonthGrid.setVisible(false);
        reportTextArea.setVisible(true);
        reportTextArea.clear();
        showVBox(customersBoxWrapper);
        hideVBox(usersBoxWrapper);
        loadCustomers();
        customersBox.getSelectionModel().selectFirst();
    }
    
    private void populateAppointmentsPerCustomerReport(Customer customer) {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            boolean showUpcomingOnly = customerUpcomingOnlyCheckbox.isSelected();
            headingBox.setText((showUpcomingOnly ? "Upcoming " : "All ")
                    + "Appointments for " + customer.getCustomerName());
            String sql = "SELECT a.*, u.userName FROM appointment a INNER JOIN user u "
                    + "ON u.userId = a.userId WHERE customerId = ? "
                    + "ORDER BY start";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, customer.getCustomerId());
            ResultSet rs = statement.executeQuery();
            StringBuilder sb = new StringBuilder();
            String sep = System.lineSeparator();
            LocalDate lastDate = LocalDate.MIN;
            LocalDate yesterday = LocalDate.now().minusDays(1);
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.fromResultSet(rs);
                String user = rs.getString("userName");
                ZonedDateTime start = appointment.getStart();
                ZonedDateTime end = appointment.getEnd();
                LocalDate date = start.toLocalDate();
                if (!showUpcomingOnly || date.isAfter(yesterday)) {
                    if (date.isAfter(lastDate)) {
                        sb.append(sep + date.format(DateTimeFormatter.ofPattern("MMM d, y")) + sep);
                        lastDate = date;
                    }
                    sb.append(start.format(DateTimeFormatter.ofPattern("h:mma")) + "-");
                    sb.append(end.format(DateTimeFormatter.ofPattern("h:mma")) + "\t");
                    sb.append(appointment.getTitle());
                    sb.append(" with ");
                    sb.append(user);
                    sb.append(sep);
                }
            }
            reportTextArea.setText(sb.toString());
        } catch (Exception ex) {
            Application.alertForError(
                "Something went wrong connecting to the database.", ex.getMessage());
        }
    }
    
    private void loadUsers() {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            ObservableList<User> users = FXCollections.observableArrayList();
            String sql = "SELECT * FROM user ORDER BY userName";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                User u = new User();
                u.fromResultSet(rs);
                users.add(u);
            }
            usersBox.setItems(users);
        } catch (Exception ex) {
            Application.alertForError(
                "Something went wrong connecting to the database.", ex.getMessage());
        }
    }
    
// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Appointments per Customer">
    
    private void loadCustomers() {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            ObservableList<Customer> customers = FXCollections.observableArrayList();
            String sql = "SELECT * FROM customer WHERE active = 1 ORDER BY customerName";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Customer u = new Customer();
                u.fromResultSet(rs);
                customers.add(u);
            }
            customersBox.setItems(customers);
        } catch (Exception ex) {
            Application.alertForError(
                "Something went wrong connecting to the database.", ex.getMessage());
        }
    }
    
// </editor-fold>
    
}
        