package com.fishbot.ui.controller;

import com.fishbot.core.logic.Fishing;
import com.fishbot.core.service.PropertiesHandler;
import com.fishbot.core.service.Status;
import com.fishbot.ui.app.FisherApp;
import com.fishbot.ui.app.FishingTimer;
import com.fishbot.ui.app.StatusUpdater;
import com.fishbot.utils.Constants;
import javafx.application.HostServices;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

public class FishbotController {

    private static final int MAX_TIMER_FIELD_LENGTH = Constants.MAX_TIMER_FIELD_LENGTH;
    private static final String VERSION = Constants.VERSION;
    private static final String HOME_URL = Constants.HOME_URL;
    @FXML
    private Button stopButton;
    @FXML
    private Button startButton;
    @FXML
    private CheckBox stopTimerCheckbox;
    @FXML
    private TextField stopTimerField;
    @FXML
    private CheckBox logoutCheckbox;
    @FXML
    private TextField fishingButtonField;
    @FXML
    private TextField lureButtonField;
    @FXML
    private Hyperlink homeLink;
    @FXML
    private Tooltip hyperlinkTooltip;
    @FXML
    private Label versionLabel;
    @FXML
    private Button faqButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label timerLabel;

    private HostServices hostServices;
    private FishingTimer timer;
    private StatusUpdater statusUpdater;
    private PropertiesHandler properties;
    private Fishing fishing;

    public FishbotController() {

        fishing = Fishing.getInstance();
    }

    public void setHostServices(HostServices hostServices) {

        this.hostServices = hostServices;
    }

    @FXML
    private void initialize() {

        timer = new FishingTimer(timerLabel, stopButton);

        statusUpdater = new StatusUpdater(errorLabel, statusLabel, stopButton);

        statusLabel.setText(Status.NOT_STARTED.toString());

        try {

            properties = PropertiesHandler.getInstance();

        } catch (IOException e) {

            errorLabel.setText(Status.ERROR_CONFIG.toString());
        }

        settingsButton.setDisable(true);

        versionLabel.setText(VERSION);

        handleStartButton();

        handleStopButton();

        handleStopTimerField();

        handleFaqButton();

        handleHomeLink();

        addTextFieldLimiter(fishingButtonField);

        addTextFieldLimiter(lureButtonField);

        loadProperties();

    }

    private void handleStartButton() {

        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (!fishingButtonField.getText().isEmpty()) {

                    saveProperties();

                    startButton.setDisable(true);
                    logoutCheckbox.setDisable(true);
                    fishingButtonField.setDisable(true);
                    lureButtonField.setDisable(true);
                    //settingsButton.setDisable(true);
                    stopTimerCheckbox.setDisable(true);
                    stopTimerField.setDisable(true);

                    stopButton.setDisable(false);
                    timerLabel.setDisable(false);

                    errorLabel.setText("");

                    handleStopTimerField();

                    Thread thread = new Thread() {

                        @Override
                        public void run() {

                            try {

                                fishing.startFishing();

                            } catch (IOException e) {

                                errorLabel.setText(Status.ERROR_COMMON.toString());
                            }
                        }
                    };

                    thread.setDaemon(true);

                    thread.start();

                    timer.setLimit(stopTimerField.getText());
                    timer.start();

                    statusUpdater.start();

                } else {

                    errorLabel.setText(Status.FISHING_KEY_NOT_DEFINED.toString());
                }
            }
        });
    }

    private void handleStopButton() {

        stopButton.setDisable(true);

        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                startButton.setDisable(false);
                logoutCheckbox.setDisable(false);
                fishingButtonField.setDisable(false);
                lureButtonField.setDisable(false);
                //settingsButton.setDisable(false);
                stopTimerCheckbox.setDisable(false);

                stopButton.setDisable(true);
                timerLabel.setDisable(true);

                if (stopTimerCheckbox.isSelected()) {

                    stopTimerField.setDisable(false);

                } else {

                    stopTimerField.setDisable(true);
                }

                timer.stop();

                statusUpdater.stop();

                fishing.stopFishing();

                if (logoutCheckbox.isSelected()) fishing.logout();
            }
        });
    }

    private void handleStopTimerField() {

        final String regex = "[0-9]*";

        stopTimerField.setDisable(true);

        stopTimerCheckbox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (stopTimerCheckbox.isSelected()) {

                    stopTimerField.setDisable(false);

                } else {

                    stopTimerField.clear();

                    stopTimerField.setDisable(true);
                }
            }
        });

        stopTimerField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if (newValue.length() > MAX_TIMER_FIELD_LENGTH || !newValue.matches(regex)) {

                    ((StringProperty) observable).setValue(oldValue);
                }

            }
        });
    }

    private void addTextFieldLimiter(TextField tf) {

        final String regex = "[a-zA-Z0-9]?";

        tf.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {


                if (!newValue.matches(regex)) {

                    ((StringProperty) observable).setValue(oldValue);

                    if (newValue.length() > 0) {

                        if (newValue.substring(newValue.length() - 1).matches(regex)) {

                            ((StringProperty) observable).setValue(newValue.substring(newValue.length() - 1).toUpperCase());
                        }
                    }

                } else {

                    ((StringProperty) observable).setValue(newValue.toUpperCase());

                }
            }
        });
    }


    private void loadProperties() {

        fishingButtonField.setText(String.valueOf(properties.getFishingKey()));

        lureButtonField.setText(String.valueOf(properties.getLureKey()));

        if (properties.getLogout()) logoutCheckbox.setSelected(true);

    }

    private void saveProperties() {

        properties.setFishingKey(fishingButtonField.getText().charAt(0));

        String lureKey = lureButtonField.getText();

        if (lureKey != null && !lureKey.isEmpty()) {

            properties.setLureKey(lureKey.charAt(0));

        } else {

            properties.setEmptyLureKey();
        }

        if (logoutCheckbox.isSelected()) {

            properties.setLogout(true);

        } else {

            properties.setLogout(false);
        }

        try {

            properties.store();

        } catch (IOException e) {

            errorLabel.setText(Status.ERROR_CONFIG.toString());
        }
    }

    private void handleHomeLink() {

        hyperlinkTooltip.setText(HOME_URL);

        homeLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                hostServices.showDocument(HOME_URL);
            }
        });
    }

    private void handleFaqButton() {

        faqButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {

                    FisherApp.openHowToWindow();

                } catch (IOException e) {

                    errorLabel.setText(Status.ERROR_COMMON.toString());
                }
            }
        });
    }
}