package com.fishbot.core.logic;

import com.fishbot.core.service.PropertiesHandler;
import com.fishbot.core.service.Status;
import com.fishbot.utils.Constants;
import org.opencv.core.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Class allows to manage fishing process
 */
public class Fishing {

    private static final int MAX_CYCLES_NUMBER = Constants.MAX_CYCLES_NUMBER;
    private static final String GAME_NAME = Constants.GAME_NAME;
    private static final int NUMBER_OF_FAILED_ATTEMPTS_IN_A_ROW = Constants.NUMBER_OF_FAILED_ATTEMPTS_IN_A_ROW;
    private static final long LURE_EXISTS_TIME = Constants.LURE_EXISTS_TIME; // 10 minutes
    private static Fishing instance;
    private int overallAttempts;
    private Robot robot;
    private WindowHandler windowHandler;
    private ImageScanner scanner;
    private Fisher fisher;
    private PropertiesHandler propertiesHandler;
    private Status status;
    private char fishingButton;
    private char attachLureButton;
    private boolean isFishingCanceled;
    private long attachLureLastTime;

    private Fishing() {

        try {

            robot = new Robot();

        } catch (AWTException e) {

            // Do nothing
        }

        scanner = new ImageScanner();

        fisher = new Fisher(robot);

        windowHandler = new WindowHandler(GAME_NAME);

        status = Status.NOT_STARTED;

        try {

            propertiesHandler = PropertiesHandler.getInstance();

        } catch (IOException e) {

            status = Status.ERROR_CONFIG;
        }

        isFishingCanceled = true;
    }

    /**
     * Entry point for singleton
     *
     * @return class instance
     */
    public static synchronized Fishing getInstance() {

        if (instance == null) {

            instance = new Fishing();
        }

        return instance;
    }

    /**
     * Start fishing rounds until canceled.
     * Fishing round consist of:
     * - detect game window
     * - cast a line
     * - get window screenshot and detect bobber
     * - run cycles of splash detection
     * - random timeout
     *
     * @throws IOException
     */
    public void startFishing() throws IOException {

        isFishingCanceled = false;

        overallAttempts = 0;

        status = Status.IN_PROGRESS;

        int failedAttempts = 0;

        updateParameters(propertiesHandler);

        windowHandler.setWindow(GAME_NAME);

        attachLureLastTime = 0;

        BufferedImage image;

        // Fishing round cycle
        while (!isFishingCanceled) {

            Rect rectangle = null;

            overallAttempts++;

            if (windowHandler.isWindowEnabled()) {

                if (!windowHandler.isWindowMinimized()) {

                    windowHandler.bringWindowToTop();

                    attachLureIfRequired();

                    fisher.castALine(fishingButton);

                    // Need time for bobber will appear on screen
                    robot.delay(2000);

                    image = windowHandler.takePicture();

                    scanner.setImage(image);

                    // Searching for a bobber
                    rectangle = scanner.searchBobber();

                } else {

                    status = Status.ERROR_WINDOW_MINIMIZED;

                    isFishingCanceled = true;
                }

            } else {

                status = Status.ERROR_WINDOW_NOT_FOUND;

                isFishingCanceled = true;
            }

            if (rectangle != null) {

                // Bobber is found
                // Detecting splash cycle
                for (int i = 0; i < MAX_CYCLES_NUMBER; i++) {

                    robot.delay(25);

                    if (windowHandler.isWindowEnabled()) {

                        if (!windowHandler.isWindowMinimized()) {

                            image = windowHandler.takePicture(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

                            scanner.setImage(image);

                            if (scanner.detectBobberSplash()) {

                                // Splash detected

                                Point pointOnScreen = new Point(windowHandler.getWindowRectangle().x + rectangle.x + (rectangle.width / 2),
                                        windowHandler.getWindowRectangle().y + rectangle.y + (rectangle.height / 2));

                                windowHandler.bringWindowToTop();

                                fisher.clickBobber(pointOnScreen);

                                robot.delay(1000);

                                break;
                            }

                        } else {

                            status = Status.ERROR_WINDOW_MINIMIZED;

                            isFishingCanceled = true;

                        }
                    } else {

                        status = Status.ERROR_WINDOW_NOT_FOUND;

                        isFishingCanceled = true;
                    }
                }

            } else {

                // Bobber is not found

                failedAttempts++;

                if (failedAttempts >= NUMBER_OF_FAILED_ATTEMPTS_IN_A_ROW) {

                    isFishingCanceled = true;

                    status = Status.ERROR_BOBBER_NOT_FOUND_STREAK;
                }
            }

            randomTimeout(2, 5);
        }
    }

    /**
     * Cancel fishing process
     */
    public void stopFishing() {

        isFishingCanceled = true;
    }

    /**
     * @return status of fishing
     */
    public boolean isFishingCanceled() {

        return isFishingCanceled;
    }

    /**
     * @return number of overall fishing rounds
     */
    public int getAttempts() {

        return overallAttempts;
    }

    /**
     * @return status message
     */
    public Status getStatus() {

        return status;
    }

    /**
     * send /camp message to game window
     */
    public void logout() {

        windowHandler.bringWindowToTop();

        fisher.camp();
    }

    private void attachLureIfRequired() {

        if (attachLureButton != 0) {

            if ((System.currentTimeMillis() - attachLureLastTime) > LURE_EXISTS_TIME) {

                fisher.attachLure(attachLureButton);

                attachLureLastTime = System.currentTimeMillis();
            }
        }
    }

    private void randomTimeout(int min, int max) {

        robot.delay(((int) (Math.random() * (max - min + 1) + min)) * 1000);

    }

    private void updateParameters(PropertiesHandler propertiesHandler) {

        fishingButton = propertiesHandler.getFishingKey();

        attachLureButton = propertiesHandler.getLureKey();
    }
}
