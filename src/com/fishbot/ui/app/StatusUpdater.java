package com.fishbot.ui.app;

import com.fishbot.core.logic.Fishing;
import com.fishbot.core.service.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Async label updater for JavaFX app.
 */
public class StatusUpdater {

    private Timeline timeline;
    private Status status;
    private Label errorLabel;
    private Label statusLabel;
    private Button stopButton;
    private int attempts;
    private Fishing fishing;

    public StatusUpdater(Label errorLabel, Label statusLabel, Button stopButton) {

        this.errorLabel = errorLabel;
        this.statusLabel = statusLabel;
        this.stopButton = stopButton;
        status = Status.NOT_STARTED;
        fishing = Fishing.getInstance();
    }

    /**
     * Get status from core every 1 second and set it to main scene labels.
     */
    public void start() {

        status = Status.IN_PROGRESS;

        attempts = 1;

        statusLabel.setText(status.toString() + attempts);

        timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                status = fishing.getStatus();

                if (status.name().contains("ERROR")) {

                    errorLabel.setText(status.toString());

                    stopButton.fire();

                } else {

                    attempts = fishing.getAttempts();

                    statusLabel.setText(status.toString() + attempts);

                }
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();

    }

    /**
     * Stop updater thread.
     */
    public void stop() {

        timeline.stop();

        if (status.name().contains("ERROR")) {

            status = Status.STOPPED_BY_ERROR;

        } else {

            status = Status.STOPPED_BY_USER;
        }

        statusLabel.setText(status.toString());

    }


}
