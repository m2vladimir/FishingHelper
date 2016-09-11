package com.fishbot.ui.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HowtoController {

    private static boolean isWindowExist = false;

    @FXML
    Button okButton;


    @FXML
    private void initialize() {

        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Stage currentStage = (Stage) okButton.getScene().getWindow();

                currentStage.close();

                isWindowExist = false;
            }
        });
    }

    public boolean isWindowExist() {

        return isWindowExist;
    }

    public void setIsWindowExist(boolean b) {

        isWindowExist = b;
    }
}
