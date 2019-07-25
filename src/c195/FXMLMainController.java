package c195;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import models.*;

public class FXMLMainController {
    
    private WindowController controller;
    
    @FXML private TabPane mainTabPane;
    @FXML private AnchorPane customersView;
    @FXML private FXMLCustomersController customersViewController;
    @FXML private AnchorPane calendarView;
    @FXML private FXMLCalendarController calendarViewController;
    @FXML private AnchorPane reportsView;
    @FXML private FXMLReportsController reportsViewController;
    
    @FXML private Tab calendarTab;
    @FXML private Tab customersTab;
    @FXML private Tab reportsTab;
    
    public void initialize() {
        assert mainTabPane != null : "fx:id=\"mainTabPane\" was not injected: check your FXML file 'FXMLMain.fxml'.";
        assert calendarTab != null : "fx:id=\"calendarTab\" was not injected: check your FXML file 'FXMLMain.fxml'.";
        assert customersTab != null : "fx:id=\"customersTab\" was not injected: check your FXML file 'FXMLMain.fxml'.";
        assert reportsTab != null : "fx:id=\"reportsTab\" was not injected: check your FXML file 'FXMLMain.fxml'.";
        
        mainTabPane.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<Tab>() {
                @Override
                public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                    tabChanged();
                }
            }
        );
    }
    
    public void load(WindowController controller) {
        this.controller = controller;
        mainTabPane.getSelectionModel().select(calendarTab);
    }
    
    public void tabChanged() {
        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == calendarTab) {
            if (!calendarViewController.getIsLoaded())
                calendarViewController.load(this);
        } else if (selectedTab == customersTab) {
            if (!customersViewController.getIsLoaded())
                customersViewController.load(this);
        } else if (selectedTab == reportsTab) {
            if (!reportsViewController.getIsLoaded())
                reportsViewController.load();
        }
    }
    
    public void editCustomer(Customer customer) {
        mainTabPane.getSelectionModel().select(customersTab);
        customersViewController.selectCustomer(customer);
    }
}
