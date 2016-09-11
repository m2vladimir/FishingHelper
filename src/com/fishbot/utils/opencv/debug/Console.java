package com.fishbot.utils.opencv.debug;

import com.fishbot.core.logic.Fishing;
import com.fishbot.core.service.PropertiesHandler;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console extends Thread {

    private static Fishing fishbot;
    private PropertiesHandler propertiesHandler;

    public Console() throws AWTException {

        fishbot = Fishing.getInstance();

        try {

            propertiesHandler = PropertiesHandler.getInstance();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        help();

        BufferedReader br = null;

        String line;

        try {

            br = new BufferedReader(new InputStreamReader(System.in));

            while ((line = br.readLine()) != null) {

                switch (line) {
                    //TODO All operations as enum class

                    case "exit":
                        exit();
                        return;

                    case "start":
                        startFishing();
                        break;

                    case "stop":
                        stopFishing();
                        break;

                    case "set fishing button":
                        //TODO processing
                        break;

                    case "set lure button":
                        //TODO processing
                        break;

                    case "color":
                        //TODO processing
                        break;

                    case "screen area":
                        //TODO processing
                        break;

                    case "status":
                        status();
                        break;

                    case "help":
                        help();
                        break;

                    default:
                        System.out.println("Not supported operation");
                        break;
                }
            }

        } catch (IOException e) {

            e.printStackTrace();

            //TODO add logging

        } finally {

            try {

                br.close();

            } catch (IOException e) {

                e.printStackTrace();

                //TODO add logging
            }
        }
    }

    private void exit() {

        fishbot.stopFishing();
    }

    private void startFishing() {

        if (fishbot.isFishingCanceled()) {

            System.out.println("Fishing is started");

            Thread thread = new Thread() {

                @Override
                public void run() {

                    try {

                        fishbot.startFishing();

                    } catch (IOException e) {

                        e.printStackTrace();

                        //TODO add logging
                    }
                }
            };

            thread.setDaemon(true);

            thread.start();

        } else {

            System.out.println("Fishing already in progress");
        }
    }

    private void stopFishing() {

        if (!fishbot.isFishingCanceled()) {

            fishbot.stopFishing();

            System.out.println("Fishing is stopped");

        } else {

            System.out.println("Fishing already stopped");
        }
    }

    private void setFishingKey(char c) {

        if (!fishbot.isFishingCanceled()) {

            propertiesHandler.setFishingKey(c);

            System.out.println("Fishing button was changed to " + c);

        } else {

            System.out.println("You must stop fishing before change settings");
        }

    }

    private void setLureKey(char c) {

        if (!fishbot.isFishingCanceled()) {

            propertiesHandler.setLureKey(c);

            System.out.println("Attach lure button was changed to " + c);

        } else {

            System.out.println("You must stop fishing before change settings");
        }

    }

    private void setColor(Color color) {

        if (!fishbot.isFishingCanceled()) {

            propertiesHandler.setColor(color);

            System.out.println("Default color for searching was changed");

        } else {

            System.out.println("You must stop fishing before change settings");
        }

    }

    private void status() {

        if (!fishbot.isFishingCanceled()) {

            System.out.println("Fishing is currently in progress");
            System.out.print("Overall attempts: " + fishbot.getAttempts());

        } else {

            System.out.println("Fishing is stopped");
            System.out.print("Overall attempts: " + fishbot.getAttempts());
        }
    }

    private void help() {

        //TODO add all operations
        System.out.println("List of allowing operations:");
        System.out.println("exit");
        System.out.println("start");
        System.out.println("stop");
        System.out.println("status");
        System.out.println("help");
        System.out.println("screenshot");

    }

}
