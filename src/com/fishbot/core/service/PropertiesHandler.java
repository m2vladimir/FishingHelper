package com.fishbot.core.service;

import com.fishbot.utils.Constants;

import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * This singleton class is a layer between configuration file and objects
 */
public class PropertiesHandler {

    private static final File CONFIG_FILE = Constants.CONFIG_FILE;

    private static final String COMMENTS = Constants.COMMENTS;

    private static Properties properties;

    private static PropertiesHandler instance;

    private PropertiesHandler() throws IOException {

        properties = new Properties();

        if (!CONFIG_FILE.exists()) {

            createDefaultProperties();
        }

        load();
    }

    /**
     * Entry point for singleton
     *
     * @return class instance
     */
    public static synchronized PropertiesHandler getInstance() throws IOException {

        if (instance == null) {

            instance = new PropertiesHandler();
        }

        return instance;
    }

    /**
     * Load properties from config file
     */
    public void load() throws IOException {

        InputStream is = new FileInputStream(CONFIG_FILE);

        properties.load(is);

        is.close();
    }

    /**
     * Write properties to file
     *
     * @param prop     current properties
     * @param comments comments for this properties
     */
    private void store(Properties prop, String comments) throws IOException {

        OutputStream os = new FileOutputStream(CONFIG_FILE);

        prop.store(os, comments);

    }

    /**
     * Write properties to file
     */
    public void store() throws IOException {

        store(properties, COMMENTS);
    }

    /**
     * Create new configuration file and write default properties to it
     */
    public void createDefaultProperties() throws IOException {

        CONFIG_FILE.getParentFile().mkdirs();

        CONFIG_FILE.createNewFile();

        Properties tmpProperties = new Properties();

        tmpProperties.put("BOBBER.PIXEL.COLOR.RED", "132");

        tmpProperties.put("BOBBER.PIXEL.COLOR.GREEN", "61");

        tmpProperties.put("BOBBER.PIXEL.COLOR.BLUE", "68");

        tmpProperties.put("KEY.FISHING", "F");

        tmpProperties.put("KEY.LURE", "L");

        tmpProperties.put("LOGOUT.ON.STOP", "false");

        store(tmpProperties, COMMENTS);

    }

    /**
     * Get color for searching
     *
     * @return value of logoutOnStop checkbox
     */
    public boolean getLogout() {

        return Boolean.valueOf(properties.getProperty("LOGOUT.ON.STOP"));
    }

    /**
     * Write properties to file
     *
     * @param value value of logoutOnStop checkbox
     */
    public void setLogout(boolean value) {

        properties.put("LOGOUT.ON.STOP", String.valueOf(value));
    }

    /**
     * Get color for searching
     *
     * @return restored from properties rectangle object
     */
    public Color getColor() {

        int red = Integer.valueOf(properties.getProperty("BOBBER.PIXEL.COLOR.RED"));

        int green = Integer.valueOf(properties.getProperty("BOBBER.PIXEL.COLOR.GREEN"));

        int blue = Integer.valueOf(properties.getProperty("BOBBER.PIXEL.COLOR.BLUE"));

        return new Color(red, green, blue);
    }

    /**
     * Write properties to file
     *
     * @param color color object for writing
     */
    public void setColor(Color color) {

        properties.put("BOBBER.PIXEL.COLOR.RED", Integer.toString(color.getRed()));

        properties.put("BOBBER.PIXEL.COLOR.GREEN", Integer.toString(color.getGreen()));

        properties.put("BOBBER.PIXEL.COLOR.BLUE", Integer.toString(color.getBlue()));
    }

    /**
     * Get key char that allows to call fishing in game
     *
     * @return char property
     */
    public char getFishingKey() {

        return properties.getProperty("KEY.FISHING").toLowerCase().charAt(0);
    }

    /**
     * Write properties to file
     *
     * @param fishingKey character for writing
     */
    public void setFishingKey(char fishingKey) {

        properties.put("KEY.FISHING", String.valueOf(fishingKey));
    }

    /**
     * Get key char that allows to attach lure in game
     *
     * @return char property
     */
    public char getLureKey() {

        char c = 0;

        if (!properties.getProperty("KEY.LURE").isEmpty()) {

            c = properties.getProperty("KEY.LURE").toLowerCase().charAt(0);
        }

        return c;
    }

    /**
     * Write properties to file
     *
     * @param lureKey character for writing
     */
    public void setLureKey(char lureKey) {

        properties.put("KEY.LURE", String.valueOf(lureKey));
    }

    /**
     * Write properties to file
     */
    public void setEmptyLureKey() {

        properties.put("KEY.LURE", "");
    }
}
