package com.fishbot.ui.app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;

/**
 * JavaFx timer.
 * Send stop to main app when reached timeout.
 */
public class FishingTimer {

    private Timeline timeline;
    private Label label;
    private Button stopButton;
    private long limit;

    public FishingTimer(Label label, Button stopButton) {

        this.label = label;
        this.stopButton = stopButton;
    }

    /**
     * Start timer in new thread.
     * Will continue until canceled or until timeout will be reached.
     */
    public void start() {

        final long startTime = System.currentTimeMillis();

        label.setText("0:00:00");

        timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                long diff = System.currentTimeMillis() - startTime;
                long fishingTime = diff;

                if (limit != 0 && fishingTime > limit) {

                    stopButton.fire();
                }

                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                diff -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                diff -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

                label.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));

            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();
    }

    /**
     * Send cancel to timer thread.
     */
    public void stop() {

        timeline.stop();
    }

    /**
     * Set timer timeout.
     *
     * @param value value of milliseconds.
     */
    public void setLimit(String value) {

        if (!value.isEmpty()) {

            limit = Long.valueOf(value);

            limit = limit * 1000 * 60;
        }
    }
}
