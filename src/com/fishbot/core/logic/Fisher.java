package com.fishbot.core.logic;

import com.fishbot.core.service.KeyboardHelper;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Class allows to manipulate character in WoW game
 */
public class Fisher {

    private Robot robot;

    private KeyboardHelper keyboard;

    public Fisher(Robot robot) {

        this.robot = robot;

        keyboard = new KeyboardHelper(robot);
    }

    /**
     * Send any keys to top application
     *
     * @param sentence sentence to send
     */
    public void say(String sentence) {

        keyboard.type(KeyEvent.VK_ENTER);

        for (int i = 0; i < sentence.length(); i++) {

            keyboard.type(sentence.charAt(i));
        }

        keyboard.type(KeyEvent.VK_ENTER);
    }

    /**
     * Mouse click to specified coordinates on a screen
     *
     * @param point point to click
     */
    public void clickBobber(Point point) {

        robot.mouseMove(point.x, point.y);

        // SHIFT + Click for auto loot

        robot.mousePress(MouseEvent.BUTTON3_MASK);

        robot.keyPress(KeyEvent.VK_SHIFT);

        robot.mouseRelease(MouseEvent.BUTTON3_MASK);

        robot.keyRelease(KeyEvent.VK_SHIFT);

    }

    /**
     * Start fishing round
     */
    public void castALine(char c) {

        keyboard.type(c);

    }

    /**
     * Stop fishing and relax
     */
    public void camp() {

        say("/camp");

    }

    /**
     * Attach lure to fishing pole
     *
     * @param c key to start attach in game
     */
    public void attachLure(char c) {

        if (String.valueOf(c).isEmpty()) return;

        keyboard.type(c);

        robot.delay(10000); // Waiting until cast completed

    }
}
