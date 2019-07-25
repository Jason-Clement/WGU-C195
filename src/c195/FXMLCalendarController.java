package c195;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import models.*;

public class FXMLCalendarController {

// <editor-fold defaultstate="expanded" desc="FXML Fields">
    
    @FXML private AnchorPane monthPane;
    @FXML private Label monthHeading;
    @FXML private GridPane monthGrid;
    @FXML private AnchorPane weekPane;
    @FXML private Label weekHeader1;
    @FXML private Label weekHeader2;
    @FXML private Label weekHeader3;
    @FXML private Label weekHeader4;
    @FXML private Label weekHeader5;
    @FXML private Label weekHeader6;
    @FXML private Label weekHeader7;
    @FXML private TilePane weekContent1;
    @FXML private TilePane weekContent2;
    @FXML private TilePane weekContent3;
    @FXML private TilePane weekContent4;
    @FXML private TilePane weekContent5;
    @FXML private TilePane weekContent6;
    @FXML private TilePane weekContent7;
    @FXML private AnchorPane appointmentPane;
    @FXML private TextField apptTitleBox;
    @FXML private TextArea apptContactBox;
    @FXML private TextArea apptDescriptionBox;
    @FXML private DatePicker apptDateBox;
    @FXML private TextField apptLengthBox;
    @FXML private ComboBox<Customer> apptCustomerBox;
    @FXML private ComboBox<String> apptTypeBox;
    @FXML private ComboBox<String> apptLengthTypeBox;
    @FXML private ComboBox<String> apptHourBox;
    @FXML private ComboBox<String> apptMinuteBox;
    @FXML private ComboBox<String> apptPeriodBox;
    @FXML private TextField apptUrlBox;
    @FXML private TextArea apptLocationBox;
    @FXML private Label errorBox;
    @FXML private Font x1;
    @FXML private Font x2;
    @FXML private Insets x3;

// </editor-fold>
// <editor-fold defaultstate="expanded" desc="FXML Events">
    
    @FXML
    void viewByWeekActivated(ActionEvent event) {
        loadScreen(ScreenType.WEEKLY);
    }

    @FXML
    void viewByMonthActivated(ActionEvent event) {
        loadScreen(ScreenType.MONTHLY);
    }

    @FXML
    void newAppointmentActivated(ActionEvent event) {
        loadAppointment(null);
    }

    @FXML
    void weekPrevActivated(ActionEvent event) {
        loadDate(currentDate.minusWeeks(1));
    }
    
    @FXML
    void weekNextActivated(ActionEvent event) {
        loadDate(currentDate.plusWeeks(1));
    }

    @FXML
    void monthPrevActivated(ActionEvent event) {
        loadDate(currentDate.minusMonths(1));
    }

    @FXML
    void monthNextActivated(ActionEvent event) {
        loadDate(currentDate.plusMonths(1));
    }

    @FXML
    void editCustomerActivated(ActionEvent event) {
        mainController.editCustomer(apptCustomerBox.getValue());
    }

    @FXML
    void newCustomerActivated(ActionEvent event) {
        mainController.editCustomer(null);
    }

    @FXML
    void apptLengthTypeChanged(ActionEvent event) {
        try {
            double value = Double.parseDouble(apptLengthBox.getText());
            if (apptLengthTypeBox.getValue().equalsIgnoreCase("minutes")) {
                apptLengthBox.setText(format(value * 60.0));
            } else {
                apptLengthBox.setText(format(value /= 60.0));
            }
        } catch (NumberFormatException ex) {
            // Do nothing
        }
            
    }
    
    @FXML
    void deleteButtonActivated(ActionEvent event) {
        if (Application.alertForConfirmation("Are you sure you want to delete this appointment? This cannot be undone.")) {
            deleteAppointment();
        }
    }

    @FXML
    void saveButtonActivated(ActionEvent event) {
        try {
            saveAppointment();
        } catch (InvalidDataException ex) {
            errorBox.setText(String.join(System.lineSeparator(),ex.getErrors()));
        }
    }

    @FXML
    void cancelButtonActivated(ActionEvent event) {
        clearAppointment();
        loadScreen(calendarType);
    }

// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Initialization">

    @FXML
    void initialize() {
        assert weekContent1 != null : "fx:id=\"weekContent1\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekContent2 != null : "fx:id=\"weekContent2\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekContent3 != null : "fx:id=\"weekContent3\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekContent4 != null : "fx:id=\"weekContent4\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekContent5 != null : "fx:id=\"weekContent5\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekContent6 != null : "fx:id=\"weekContent6\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekContent7 != null : "fx:id=\"weekContent7\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptTitleBox != null : "fx:id=\"apptTitleBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekPane != null : "fx:id=\"weekPane\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptContactBox != null : "fx:id=\"apptContactBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptDescriptionBox != null : "fx:id=\"apptDescriptionBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptDateBox != null : "fx:id=\"apptDateBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert monthPane != null : "fx:id=\"monthPane\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert monthHeading != null : "fx:id=\"monthHeading\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptLengthBox != null : "fx:id=\"apptLengthBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptCustomerBox != null : "fx:id=\"apptCustomerBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptTypeBox != null : "fx:id=\"apptTypeBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert monthGrid != null : "fx:id=\"monthGrid\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptLengthTypeBox != null : "fx:id=\"apptLengthTypeBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptUrlBox != null : "fx:id=\"apptUrlBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert appointmentPane != null : "fx:id=\"appointmentPane\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptLocationBox != null : "fx:id=\"apptLocationBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptHourBox != null : "fx:id=\"apptHourBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptMinuteBox != null : "fx:id=\"apptMinuteBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert apptPeriodBox != null : "fx:id=\"apptPeriodBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader1 != null : "fx:id=\"weekHeader1\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader2 != null : "fx:id=\"weekHeader2\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader3 != null : "fx:id=\"weekHeader3\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader4 != null : "fx:id=\"weekHeader4\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader5 != null : "fx:id=\"weekHeader5\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader6 != null : "fx:id=\"weekHeader6\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert weekHeader7 != null : "fx:id=\"weekHeader7\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert errorBox != null : "fx:id=\"errorBox\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert x1 != null : "fx:id=\"x1\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert x2 != null : "fx:id=\"x2\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";
        assert x3 != null : "fx:id=\"x3\" was not injected: check your FXML file 'FXMLCalendar.fxml'.";

        apptLengthTypeBox.setItems(FXCollections.observableArrayList("Minutes", "Hours"));
        apptLengthTypeBox.getSelectionModel().select(0);
        
        apptHourBox.setItems(FXCollections.observableArrayList("12","1","2","3","4","5","6","7","8","9","10","11"));
        for (int i = 0; i < 60; i++)
            apptMinuteBox.getItems().add(i < 10 ? "0" + String.valueOf(i) : String.valueOf(i));
        apptPeriodBox.setItems(FXCollections.observableArrayList("AM", "PM"));
        
        setupMonthly();
        
        monthPane.setVisible(true);
        weekPane.setVisible(false);
        appointmentPane.setVisible(false);
    }
    
    private boolean isLoaded = false;
    public boolean getIsLoaded() { return isLoaded; }
    private FXMLMainController mainController;
    public void load(FXMLMainController mainController) {
        this.mainController = mainController;
        loadDate(ZonedDateTime.now());
        isLoaded = true;
    }
    
    private ArrayList<MonthCell> monthCells = new ArrayList();
    
    private void setupMonthly() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                MonthCell c = new MonthCell();
                c.setPrefSize(monthGrid.getColumnConstraints().get(j).getPrefWidth(),
                    monthGrid.getRowConstraints().get(i).getPrefHeight());
                monthCells.add(c);
                monthGrid.add(c, j, i);
            }
        }
    }
    
    private String format(double value) {
        if (value % 1 == 0)
            return String.format("%.0f", value);
        else
            return String.valueOf(value);
    }
    
// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Screen Management">
    
    private ScreenType calendarType = ScreenType.MONTHLY;
    
    private enum ScreenType {
        MONTHLY,
        WEEKLY,
        APPOINTMENT
    }
    
    private void loadScreen(ScreenType type) {
        weekPane.setVisible(false);
        monthPane.setVisible(false);
        appointmentPane.setVisible(false);
        switch (type) {
            case MONTHLY:
                calendarType = type;
                monthPane.setVisible(true);
                loadDate();
                break;
            case WEEKLY:
                calendarType = type;
                weekPane.setVisible(true);
                loadDate();
                break;
            case APPOINTMENT:
                appointmentPane.setVisible(true);
                break;
        }
    }
    
// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Subclasses">
    
    private class AppointmentButton extends Button {
        private final String cellStyle = "-fx-background-color: " +
            "    linear-gradient(#f2f2f2, #d6d6d6)," +
            "    linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
            "    linear-gradient(#dddddd 0%, #f6f6f6 50%);" +
            //"-fx-background-radius: 8,7,6;" +
            //"-fx-background-insets: 0,1,2;" +
            "-fx-text-fill: black;" +
            ""; //-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";
        private Appointment appointment;
        public AppointmentButton(Appointment appointment) {
            setTextOverrun(OverrunStyle.ELLIPSIS);
            setText(
                    appointment.getStart().format(DateTimeFormatter.ofPattern("h:mma"))
                    + " " + appointment.getTitle());
            setStyle(cellStyle);
            this.appointment = appointment;
            setOnAction(e -> loadAppointment(this.appointment));
        }
    }
    
    private class MonthCell extends AnchorPane {
        private Label dateCell = new Label();
        private VBox appointmentsBox = new VBox();
        public MonthCell() {
            Font font = dateCell.getFont();
            dateCell.setFont(Font.font(font.getFamily(), FontWeight.LIGHT, 16));
            getChildren().add(dateCell);
            getChildren().add(appointmentsBox);
            setTopAnchor(dateCell, 2.0);
            setLeftAnchor(dateCell, 5.0);
            setTopAnchor(appointmentsBox, 28.0);
            setLeftAnchor(appointmentsBox, 5.0);
            setBottomAnchor(appointmentsBox, 5.0);
            setRightAnchor(appointmentsBox, 5.0);
        }
        public void setDate(int value) {
            dateCell.setText(Integer.toString(value));
        }
        public void clearDate() {
            dateCell.setText("");
        }
        public void addAppointment(Appointment appointment) {
            appointmentsBox.getChildren().add(new AppointmentButton(appointment));
        }
        public void clearAppointments() {
            appointmentsBox.getChildren().clear();
        }
        public void clear() {
            clearDate();
            clearAppointments();
        }
    }
    
// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Date Loading">
    
    private ZonedDateTime currentDate;
    private void loadDate(ZonedDateTime date) {
        currentDate = date;
        loadDate();
    }
    private void loadDate() {
        if (calendarType == ScreenType.MONTHLY) {
            loadMonthly(currentDate);
        } else {
            loadWeekly(currentDate);
        }
    }
    
    private void loadMonthly(ZonedDateTime date) {
        for (MonthCell c : monthCells)
            c.clear();
        ZonedDateTime first = ZonedDateTime.of(date.getYear(), date.getMonthValue(),
                1, 0, 0, 0, 0, ZoneId.systemDefault());
        int firstDayOfWeek = first.getDayOfWeek().getValue();
        ZonedDateTime last = first.with(TemporalAdjusters.lastDayOfMonth());
        monthHeading.setText(first.getMonth().toString() + " " + first.getYear());
        for (int i = firstDayOfWeek, j = 1; j <= last.getDayOfMonth(); i++, j++) {
            monthCells.get(i).setDate(j);
        }
        try (Connection connection = Application.getInstance().getDBConnection()) {
            String sql = "SELECT * FROM appointment WHERE userId = ? AND start >= ? AND start < ?"
                    + " ORDER BY start";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Application.getInstance().getActiveUser().getUserId());
            statement.setTimestamp(2, Application.toTimestamp(first));
            statement.setTimestamp(3, Application.toTimestamp(first.plusMonths(1)));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.fromResultSet(rs);
                monthCells.get(firstDayOfWeek - 1 + appointment.getStart().getDayOfMonth())
                        .addAppointment(appointment);
            }
        } catch (Exception ex) {
            Application.alertForError(
                "The appointments could not be loaded from the database", ex.getMessage());
        }
    }
    
    private void loadWeekly(ZonedDateTime date) {
        Label[] labels = {
            weekHeader1, weekHeader2, weekHeader3, weekHeader4,
            weekHeader5, weekHeader6, weekHeader7
        };
        TilePane[] contents = {
            weekContent1, weekContent2, weekContent3, weekContent4,
            weekContent5, weekContent6, weekContent7
        };
        for (TilePane p : contents)
            p.getChildren().clear();
        ZonedDateTime first = ZonedDateTime.of(date.getYear(), date.getMonthValue(),
                date.getDayOfMonth(), 0, 0, 0, 0, ZoneId.systemDefault());
        first = first.minusDays(first.getDayOfWeek().getValue());
        for (int i = 0; i < 7; i++)
            labels[i].setText(first.plusDays(i).format(DateTimeFormatter.ofPattern("EEEE, MMMM d, Y")));
        try (Connection connection = Application.getInstance().getDBConnection()) {
            String sql = "SELECT * FROM appointment WHERE userId = ? AND start >= ? AND start < ?"
                    + " ORDER BY start";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, Application.getInstance().getActiveUser().getUserId());
            statement.setTimestamp(2, Application.toTimestamp(first));
            statement.setTimestamp(3, Application.toTimestamp(first.plusWeeks(1)));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.fromResultSet(rs);
                contents[appointment.getStart().getDayOfWeek().getValue()].getChildren()
                    .add(new AppointmentButton(appointment));
            }
        } catch (Exception ex) {
            Application.alertForError(
                "The appointments could not be loaded from the database", ex.getMessage());
        }
    }
    
// </editor-fold>
// <editor-fold defaultstate="expanded" desc="Appointment Management">

    private Appointment currentAppointment;
    
    public void loadAppointment(Appointment appointment) {
        loadAppointment(appointment, 0);
    }
    
    public void loadAppointment(Appointment appointment, int customerId) {
        loadTypes();
        loadCustomers();
        clearAppointment();
        if (appointment != null) {
            currentAppointment = appointment;
            populateAppointment(appointment);
        } else {
            currentAppointment = new Appointment();
        }
        if (customerId > 0) {
            for (int i = 0; i < apptCustomerBox.getItems().size(); i++) {
                if (apptCustomerBox.getItems().get(i).getCustomerId() == customerId) {
                    apptCustomerBox.getSelectionModel().select(i);
                    break;
                }
            }
        }
        loadScreen(ScreenType.APPOINTMENT);
    }
    
    private void populateAppointment(Appointment appointment) {
        if (appointment.getTitle() != null)
            apptTitleBox.setText(appointment.getTitle());
        apptDateBox.setValue(appointment.getStart().toLocalDate());
        ZonedDateTime start = appointment.getStart();
        apptHourBox.getSelectionModel().select(start.getHour() % 12);
        apptMinuteBox.getSelectionModel().select(start.getMinute());
        apptPeriodBox.getSelectionModel().select(start.getHour() < 12 ? 0 : 1);
        long minutes = ChronoUnit.MINUTES.between(start, appointment.getEnd());
        if (minutes > 120 && minutes % 15 == 0) {
            apptLengthTypeBox.getSelectionModel().select("Hours");
            double hours = minutes / 60.0;
            apptLengthBox.setText(format(hours));
        } else {
            apptLengthTypeBox.getSelectionModel().select("Minutes");
            apptLengthBox.setText(String.valueOf(minutes));
        }
        if (appointment.getDescription() != null)
            apptDescriptionBox.setText(appointment.getDescription());
        if (appointment.getLocation() != null)
            apptLocationBox.setText(appointment.getLocation());
        if (appointment.getContact() != null)
            apptContactBox.setText(appointment.getContact());
        if (appointment.getUrl() != null)
            apptUrlBox.setText(appointment.getUrl());
        if (appointment.getType() != null) {
            ObservableList<String> typeItems = apptTypeBox.getItems();
            if (!typeItems.contains(appointment.getType()))
                typeItems.add(0, appointment.getType());
           apptTypeBox.getSelectionModel().select(appointment.getType());
        }
        for (int i = 0; i < apptCustomerBox.getItems().size(); i++) {
            Customer customer = apptCustomerBox.getItems().get(i);
            if (customer.getCustomerId() == appointment.getCustomerId()) {
                apptCustomerBox.getSelectionModel().select(i);
                break;
            }
        }
    }
    
    private void loadTypes() {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            ObservableList<String> types = FXCollections.observableArrayList();
            String sql = "SELECT DISTINCT type FROM appointment ORDER BY type";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                types.add(rs.getString(1));
            }
            apptTypeBox.setItems(types);
        } catch (Exception ex) {
            Application.alertForError(
                "Something when wrong connecting to the database.", ex.getMessage());
        }
    }
    
    private void loadCustomers() {
        try (Connection connection = Application.getInstance().getDBConnection()) {
            ObservableList<Customer> customers = FXCollections.observableArrayList();
            String sql = "SELECT * FROM customer WHERE active = 1 ORDER BY customerName";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                Customer c = new Customer();
                c.fromResultSet(rs);
                customers.add(c);
            }
            
            apptCustomerBox.setItems(customers);
            // Lambdas save me a lot of typing here
            apptCustomerBox.setCellFactory((ListView<Customer> list) -> new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer c, boolean empty) {
                    super.updateItem(c, empty);
                    setText(c == null || empty ? "" : c.getCustomerName());
                }
            });
            apptCustomerBox.setButtonCell(new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer c, boolean empty) {
                    super.updateItem(c, empty);
                    setText(c == null || empty ? "" : c.getCustomerName());
                }
            });
        } catch (Exception ex) {
            Application.alertForError(
                "Something when wrong connecting to the database.", ex.getMessage());
        }
    }
    
    private void clearAppointment() {
        apptTitleBox.clear();
        apptTypeBox.getSelectionModel().clearAndSelect(0);
        apptCustomerBox.getSelectionModel().clearAndSelect(0);
        apptDateBox.setValue(LocalDate.now());
        apptHourBox.getSelectionModel().select(LocalDateTime.now().getHour() % 12);
        apptMinuteBox.getSelectionModel().select(0);
        apptPeriodBox.getSelectionModel().select(LocalDateTime.now().getHour() < 12 ? 0 : 1);
        apptLengthBox.setText("30");
        apptDescriptionBox.clear();
        apptLocationBox.clear();
        apptContactBox.clear();
        apptUrlBox.clear();
        errorBox.setText("");
    }
    
    private void saveAppointment() throws InvalidDataException {
        ArrayList<String> errors = new ArrayList<String>();
        String title = apptTitleBox.getText().trim();
        if (title.isEmpty())
            errors.add("Title is required");
        String type = apptTypeBox.getValue().trim();
        if (type.isEmpty())
            errors.add("Type is required");
        int customerId = 0;
        if (apptCustomerBox.getSelectionModel().isEmpty())
            errors.add("Customer is required");
        else
            customerId = apptCustomerBox.getValue().getCustomerId();
        LocalDate date = apptDateBox.getValue();
        int hour = apptHourBox.getSelectionModel().getSelectedIndex();
        int minute = apptMinuteBox.getSelectionModel().getSelectedIndex();
        boolean isMorning = apptPeriodBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("AM");
        if (!isMorning) hour += 12;
        String lengthText = apptLengthBox.getText();
        double length = 0;
        try {
            length = Double.parseDouble(lengthText);
        } catch (NumberFormatException ex) {
            errors.add("Length is required and must be a number");
        }
        if (apptLengthTypeBox.getSelectionModel().getSelectedIndex() > 0)
            length *= 60;
        if (length <= 0)
            errors.add("Appointment length must be greater than 0");
        if (hour < 8 || hour > 16 || hour + minute / 60.0 + length / 60.0 > 17.0)
            errors.add("Appointment must be scheduled during business hours (8:00AM - 5:00PM)");
        String description = apptDescriptionBox.getText();
        String location = apptLocationBox.getText();
        String contact = apptContactBox.getText();
        String url = apptUrlBox.getText();
        ZonedDateTime start = ZonedDateTime.of(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                hour,
                minute,
                0,
                0,
                ZoneId.systemDefault());
        ZonedDateTime end = start.plusMinutes(Double.valueOf(length).longValue());
        
        if (errors.size() > 0)
            throw new InvalidDataException(errors);
        
        int activeUserId = Application.getInstance().getActiveUser().getUserId();
        List<Appointment> userOverlap = currentAppointment.checkForOverlap("userId", activeUserId, start, end);
        if (userOverlap.size() > 0) {
            errors.add("You already have an appointment scheduled during that time:");
            for (Appointment appointment : userOverlap) {
                errors.add("  "
                    + appointment.getStart().format(DateTimeFormatter.ofPattern("h:mma"))
                    + " " + appointment.getTitle());
            }
        }
        
        List<Appointment> customerOverlap = currentAppointment.checkForOverlap("customerId", customerId, start, end);
        if (customerOverlap.size() > 0) {
            errors.add("The customer already has an appointment scheduled during that time:");
            for (Appointment appointment : customerOverlap) {
                errors.add("  "
                    + appointment.getStart().format(DateTimeFormatter.ofPattern("h:mma"))
                    + " " + appointment.getTitle());
            }
        }
        
        if (errors.size() > 0)
            throw new InvalidDataException(errors);
        
        currentAppointment.setTitle(title);
        currentAppointment.setType(type);
        currentAppointment.setUserId(Application.getInstance().getActiveUser().getUserId());
        currentAppointment.setCustomerId(customerId);
        currentAppointment.setStart(start);
        currentAppointment.setEnd(end);
        currentAppointment.setDescription(description);
        currentAppointment.setLocation(location);
        currentAppointment.setContact(contact);
        currentAppointment.setUrl(url);
        
        try {
            currentAppointment.commitToDb();
            loadDate(start);
            loadScreen(calendarType);
        } catch (Exception ex) {
            Application.alertForError(
                "An error occurred saving the appointment to the database", ex.getMessage());
        }
    }
    
    private void deleteAppointment() {
        try {
            ZonedDateTime dt = currentAppointment.getStart();
            currentAppointment.deleteFromDb();
            loadDate(dt);
            loadScreen(calendarType);
        } catch (Exception ex) {
            Application.alertForError(
                "An error occurred deleting the appointment from the database.", ex.getMessage());
        }
    }
    
// </editor-fold>
}
