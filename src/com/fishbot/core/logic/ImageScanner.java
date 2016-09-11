package com.fishbot.core.logic;

import com.fishbot.utils.Constants;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

/**
 * Class allows to detect fishing bobber and bobber splash
 * on image using different algorithms
 *
 * @author VMyakushin
 */
public class ImageScanner {

    // Search for +- range in RGB
    private static final int COLOR_SIMPLE_SEARCH_RANGE = Constants.COLOR_SIMPLE_SEARCH_RANGE;
    private static final int COLOR_EXTENDED_SEARCH_RANGE = Constants.COLOR_EXTENDED_SEARCH_RANGE;
    private static final String FIND_BOBBER_CASCADE_PATH = Constants.FIND_BOBBER_CASCADE_PATH;
    private static final String SPLASH_BOBBER_CASCADE_PATH = Constants.SPLASH_BOBBER_CASCADE_PATH;
    private BufferedImage image;
    private Color color;
    private CascadeClassifier find_bobber_cascade;
    private CascadeClassifier splash_bobber_cascade;

    /**
     * Image must be set before any search will be executed.
     *
     * @param image an image for processing
     */
    public void setImage(BufferedImage image) {

        this.image = image;

        find_bobber_cascade = new CascadeClassifier(FIND_BOBBER_CASCADE_PATH);

        splash_bobber_cascade = new CascadeClassifier(SPLASH_BOBBER_CASCADE_PATH);

    }

    /**
     * Checks if given color exists on image.
     *
     * @param color specified color
     * @return point with coordinates of pixel with given color on image
     */
    public Point searchColor(Color color) {

        if (image == null || color == null) {

            return null;

        }

        for (int j = 0; j < image.getHeight(); j++) { //from top to bottom

            for (int i = image.getWidth() - 1; i >= 0; i--) { //from right to left

                Color currentColor = new Color(image.getRGB(i, j));

                if (currentColor.getRed() < color.getRed() + COLOR_SIMPLE_SEARCH_RANGE && currentColor.getRed() > color.getRed() - COLOR_SIMPLE_SEARCH_RANGE) {

                    if (currentColor.getBlue() < color.getBlue() + COLOR_SIMPLE_SEARCH_RANGE && currentColor.getBlue() > color.getBlue() - COLOR_SIMPLE_SEARCH_RANGE) {

                        if (currentColor.getGreen() < color.getGreen() + COLOR_SIMPLE_SEARCH_RANGE && currentColor.getGreen() > color.getGreen() - COLOR_SIMPLE_SEARCH_RANGE) {

                            this.color = currentColor; // Set the color if it was found at specified RGB area

                            return new Point(i, j);
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Detect if pixel exists in some area on the image.
     *
     * @param x      X coordinate of area
     * @param y      Y coordinate of area
     * @param width  area width
     * @param height area height
     * @param color  color for searching
     * @return true if color NOT found (this mean that fishing bobber fall down, time to catch a fish!)
     */
    public boolean detectBobberSplash(int x, int y, int width, int height, Color color) throws IOException {

        if (image == null || color == null) {

            return false;

        }

        BufferedImage tmpImage = image.getSubimage(x, y, width, height);

        for (int i = 0; i < tmpImage.getWidth(); i++) {

            for (int j = 0; j < tmpImage.getHeight(); j++) {

                Color currentColor = new Color(tmpImage.getRGB(i, j));

                if (currentColor.getRed() < color.getRed() + COLOR_EXTENDED_SEARCH_RANGE && currentColor.getRed() > color.getRed() - COLOR_EXTENDED_SEARCH_RANGE) {

                    if (currentColor.getBlue() < color.getBlue() + COLOR_EXTENDED_SEARCH_RANGE && currentColor.getBlue() > color.getBlue() - COLOR_EXTENDED_SEARCH_RANGE) {

                        if (currentColor.getGreen() < color.getGreen() + COLOR_EXTENDED_SEARCH_RANGE && currentColor.getGreen() > color.getGreen() - COLOR_EXTENDED_SEARCH_RANGE) {

                            return false;

                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Detect if splash signature exists on image
     *
     * @return true if exists
     */
    public boolean detectBobberSplash() {

        Mat mat = bufferedImageToMat(image);

        Mat grayMat = new Mat();

        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        MatOfRect detections = new MatOfRect();

        splash_bobber_cascade.detectMultiScale(grayMat, detections, 1.2, 2, 0, new Size(), new Size(40, 40));

        boolean detected = false;

        Rect[] rects = detections.toArray();

        if (rects.length > 0) detected = true;

        return detected;
    }

    /**
     * Search for bobber signature on given image using openCV
     *
     * @return rectangle of bobber image
     */
    public Rect searchBobber() {

        Mat mat = bufferedImageToMat(image);

        Mat grayMat = new Mat();

        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        MatOfRect detections = new MatOfRect();

        find_bobber_cascade.detectMultiScale(grayMat, detections, 1.2, 2, 0, new Size(), new Size(100, 100));

        boolean detected = false;

        Rect[] rects = detections.toArray();

        if (rects.length > 0) detected = true;

        if (detected) {

            return rects[0];

        } else {

            return null;
        }
    }

    private Mat bufferedImageToMat(BufferedImage bi) {

        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);

        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();

        mat.put(0, 0, data);

        return mat;
    }

    /**
     * Getter for color
     *
     * @return color that has more accurate RGB parameters than default
     */
    public Color getColor() {

        return color;
    }
}
