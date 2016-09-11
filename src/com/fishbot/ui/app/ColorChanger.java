package com.fishbot.ui.app;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ColorChanger {

    private Point point;
    private ImageView imageView;
    private Color color;
    private Logger logger;

    /**
     * Class for changing ImageView control
     *
     * @param imageView target ImageView control
     */
    public ColorChanger(ImageView imageView) {

        this.imageView = imageView;

        logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

    }

    /**
     * Wait for mouse click.
     * Get pixel color on mouse click event and set it to target ImageView control
     *
     * @throws NativeHookException
     * @throws AWTException
     */
    public void change_using_mouse_hook() throws NativeHookException, AWTException {

        point = null;

        GlobalScreen.registerNativeHook();

        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

                point = nativeMouseEvent.getPoint();

            }

            @Override
            public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {

            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {

            }
        });

        while (point == null) {

            try {

                Thread.sleep(200);

            } catch (InterruptedException e) {

                //Do nothing
            }
        }

        GlobalScreen.unregisterNativeHook();

        BufferedImage image;

        image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

        color = new Color(image.getRGB(point.x, point.y));

        image = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        graphics.setBackground(color);

        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        imageView.setImage(SwingFXUtils.toFXImage(image, null));

    }

    /**
     * Set given color to target ImageView rectangle
     *
     * @param defaultColor
     */
    public void reset(Color defaultColor) {

        BufferedImage image;

        image = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        graphics.setBackground(defaultColor);

        graphics.clearRect(0, 0, image.getWidth(), image.getHeight());

        imageView.setImage(SwingFXUtils.toFXImage(image, null));

    }

    /**
     * getter for last used color
     *
     * @return color
     */
    public Color getColor() {

        return color;
    }
}
