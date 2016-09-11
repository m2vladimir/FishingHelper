package com.fishbot.utils;

import java.io.File;

public class Constants {

    public static final int MAX_CYCLES_NUMBER = 100;
    public static final String GAME_NAME = "World of Warcraft";
    public static final int NUMBER_OF_FAILED_ATTEMPTS_IN_A_ROW = 5;
    public static final long LURE_EXISTS_TIME = 10 * 60 * 1000;
    public static final int COLOR_SIMPLE_SEARCH_RANGE = 14;
    public static final int COLOR_EXTENDED_SEARCH_RANGE = COLOR_SIMPLE_SEARCH_RANGE;
    public static final File CONFIG_FILE = new File("config", "app.properties");
    public static final String COMMENTS = "APPLICATION PROPERTIES";
    public static final int MAX_TIMER_FIELD_LENGTH = 4;
    public static final String VERSION = "Ver. 0.2 Beta";
    public static final String HOME_URL = "https://github.com/m2vladimir/fishbot";
    public static final String FIND_BOBBER_CASCADE_PATH = "config\\cascade_find_bobber_lbp_v3.xml";
    public static final String SPLASH_BOBBER_CASCADE_PATH = "config\\cascade_splash_bobber_lbp_v4.xml";
}
