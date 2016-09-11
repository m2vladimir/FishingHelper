package com.fishbot.core.service;

public enum Status {

    NOT_STARTED("Strongly recommended to zoom-in character camera to closest distance."),
    FISHING_KEY_NOT_DEFINED("Error: fishing button is not defined!"),
    IN_PROGRESS("Trying to catch a fish... attempt #"),
    STOPPED_BY_USER("Stopped by user. Click start to catch a fish."),
    STOPPED_BY_ERROR("Fishing is stopped due to the error."),
    CLICK_PIXEL("Take screenshot and find the fishing bobber on the picture. Click on pixel with a color that you need."),
    CLICK_PIXEL_ERROR("Something going wrong. Try to set color again."),
    ERROR_COMMON("Something is going wrong. Restart and try again."),
    ERROR_WINDOW_NOT_FOUND("Error: game window is not found!"),
    ERROR_WINDOW_MINIMIZED("Error: game window is minimized!"),
    ERROR_CONFIG("Error: something wrong with configuration file. Restart and try again."),
    ERROR_BOBBER_NOT_FOUND_STREAK("Error: bobber was not found 5 times in a row!");

    private String message;

    Status(String message) {

        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
