package com.fishbot.utils.opencv.debug;

import com.fishbot.core.logic.WindowHandler;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Screenshoter {

    private static final int SQUARE_SIDE = 80;
    private static WindowHandler wh;
    private String folderPositive;
    private String folderNegative;
    private int startCountPositivesFrom;
    private int startCountNegativesFrom;
    private File goodFile;
    private File badFile;
    private BufferedWriter goodFileWriter;
    private BufferedWriter badFileWriter;
    private Logger logger;
    private boolean isNativeHookRegistered = false;

    public Screenshoter(String folderPositive, String folderNegative) {

        this.folderPositive = folderPositive;
        this.folderNegative = folderNegative;
        goodFile = new File(new File(folderPositive).getParent(), "Good.dat");
        badFile = new File(new File(folderNegative).getParent(), "Bad.dat");

        logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }

    public Screenshoter(String folderPositive, String folderNegative, int startCountPositivesFrom, int startCountNegativesFrom) {

        this.startCountPositivesFrom = startCountPositivesFrom;
        this.startCountNegativesFrom = startCountNegativesFrom;
        this.folderPositive = folderPositive;
        this.folderNegative = folderNegative;
        goodFile = new File(new File(folderPositive).getParent(), "Good.dat");
        badFile = new File(new File(folderNegative).getParent(), "Bad.dat");

        logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }

/*
    public static void main(String[] args) throws IOException, InterruptedException, NativeHookException {

        String opencvpath = "D:\\PROJECTS\\FishingHelper\\lib\\x64\\";

        System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");

        Screenshoter screenshoter = new Screenshoter("D:\\PROJECTS\\OpenCV_Bobber_samples\\Good", "D:\\PROJECTS\\OpenCV_Bobber_samples\\Bad");


        // #RENAME SAMPLES SECTION
        //screenshoter.renameSamples("splash", "D:\\PROJECTS\\OpenCV_Bobber_samples\\SPLASHES_COL\\Good", 20, 20);



        // #SCREEN FULL WINDOW SECTION
        wh = new WindowHandler("World of Warcraft");

        for (int i = 0; i < 1000; i++) {

            BufferedImage image = wh.takePicture();

            screenshoter.screenBobberNegative(image, new Point(0, 0));
        }

        screenshoter.close();



        // #TAKE SMALL SCREENSHOT USING MOUSE
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //String line;

        //boolean isRunning = false;

        //while ((line = br.readLine()) != null) {

        //    if (line.equals("exit")) break;

         //   if (line.equals("stop")) {

         //       isRunning = false;

        //        screenshoter.unregisterNativeHook();
        //    }

        //    if (line.equals("start") && !isRunning) {

         //       isRunning = true;

        //        screenshoter.runMouseListener(20, 20, "D:\\PROJECTS\\OpenCV_Bobber_samples\\samples");
        //    }

         //   Thread.sleep(300);
        //}

        //screenshoter.unregisterNativeHook();





        // #TEST FIND BOBBER CASCADE SECTION
        //wh = new WindowHandler("World of Warcraft");

        //CascadeClassifier cascade = new CascadeClassifier("D:\\PROJECTS\\FishingHelper\\src\\com\\fishbot\\utils\\opencv\\cascade_find_bobber_lbp_v3.xml");

        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //String line;

        //while ((line = br.readLine()) != null) {

        //    if (line.equals("exit")) break;

        //    screenshoter.detect(wh.takePicture(), cascade);
        //}


        // #TEST SPLASH BOBBER CASCADE SECTION
        wh = new WindowHandler("World of Warcraft");

        CascadeClassifier find_bobber_cascade = new CascadeClassifier("config\\cascade_find_bobber_lbp_v3.xml");

        CascadeClassifier splash_bobber_cascade = new CascadeClassifier("config\\cascade_splash_bobber_lbp_v4.xml");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;

        while ((line = br.readLine()) != null) {

            if (line.equals("exit")) break;

            Rect rect = screenshoter.detect(wh.takePicture(), find_bobber_cascade);

            if (rect != null) {

                for (int i = 0; i < 100; i++) {

                    System.out.println(i);

                    long curms = System.currentTimeMillis();

                    screenshoter.detect_splash(wh.takePicture(rect.x, rect.y, rect.width + 5, rect.height + 5), splash_bobber_cascade, i);

                    //if (screenshoter.detect_splash(wh.takePicture(rect.x, rect.y, rect.width + 5, rect.height + 5), splash_bobber_cascade, i)) {
                     //   break;
                    //}

                    System.out.println("Detection time: " + (System.currentTimeMillis() - curms) + "ms");
                }
            }
        }



    }*/


    public void screenBobberPositive(BufferedImage image, Point point) throws IOException {

        if (!goodFile.exists()) {

            goodFile.getParentFile().mkdirs();

            goodFile.createNewFile();

        }

        BufferedImage smallImage = new BufferedImage(SQUARE_SIDE - 5, SQUARE_SIDE - 5, BufferedImage.TYPE_3BYTE_BGR);

        smallImage.setData(image.getRaster().createWritableChild(point.x - 35, point.y - 30, 75, 75, 0, 0, null));

        File file = new File(folderPositive, startCountPositivesFrom + ".jpg");

        if (!file.getParentFile().exists()) {

            file.getParentFile().mkdirs();
        }

        Mat mat = bufferedImageToMat(smallImage);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(file.getAbsolutePath(), mat);

        //file = new File(folderPositive, startCountPositivesFrom + "_orig.jpg");

        //ImageIO.write(smallImage, "jpg", file);

        if (goodFileWriter == null) {

            goodFileWriter = new BufferedWriter(new FileWriter(goodFile, true));
        }

        goodFileWriter.write(file.getParentFile().getName() + "\\" + file.getName() + " 1 0 0 " + SQUARE_SIDE + " " + SQUARE_SIDE);
        goodFileWriter.newLine();

        startCountPositivesFrom++;
    }

    public void screenBobberNegative(BufferedImage image, Point point) throws IOException {

        if (!badFile.exists()) {

            badFile.getParentFile().mkdirs();

            badFile.createNewFile();

        }

        //BufferedImage smallImage = new BufferedImage(SQUARE_SIDE, SQUARE_SIDE, BufferedImage.TYPE_3BYTE_BGR);

        //smallImage.setData(image.getRaster().createWritableChild(point.x - 35, point.y - 30, SQUARE_SIDE, SQUARE_SIDE, 0, 0, null));

        Rectangle rect = wh.getWindowRectangle();

        BufferedImage smallImage = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_3BYTE_BGR);

        smallImage.setData(image.getRaster().createWritableChild(0, 0, rect.width, rect.height, 0, 0, null));

        File file = new File(folderNegative, startCountNegativesFrom + ".jpg");

        if (!file.getParentFile().exists()) {

            file.getParentFile().mkdirs();
        }

        Mat mat = bufferedImageToMat(smallImage);

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(file.getAbsolutePath(), mat);

        //ImageIO.write(smallImage, "jpg", file);

        if (badFileWriter == null) {

            badFileWriter = new BufferedWriter(new FileWriter(badFile, true));
        }

        badFileWriter.write(file.getParentFile().getName() + "\\" + file.getName());
        badFileWriter.newLine();

        startCountNegativesFrom++;
    }

    public void close() throws IOException {

        if (goodFileWriter != null) {

            goodFileWriter.flush();

            goodFileWriter.close();
        }

        if (badFileWriter != null) {

            badFileWriter.flush();

            badFileWriter.close();
        }
    }

    public Rect detect(BufferedImage image, CascadeClassifier cascade) {

        Mat mat = bufferedImageToMat(image);

        Mat grayMat = new Mat();

        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        //Imgproc.equalizeHist(grayMat, grayMat);

        MatOfRect detections = new MatOfRect();

        long curms = System.currentTimeMillis();

        //cascade.detectMultiScale(grayMat, detections);

        cascade.detectMultiScale(grayMat, detections, 1.2, 2, 0, new Size(), new Size(100, 100));

        System.out.println("Detection time: " + (System.currentTimeMillis() - curms) + "ms");

        boolean detected = false;

        Rect[] rects = detections.toArray();

        for (Rect rect : rects) {

            detected = true;

            Imgproc.rectangle(grayMat, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255));

        }

        Imgcodecs.imwrite("D:\\PROJECTS\\OpenCV_Bobber_samples\\image.jpg", grayMat);

        if (detected) {

            System.out.println("detected!");



            return rects[0];

        } else {

            System.out.println("NOT detected!");

            return null;
        }
    }

    public boolean detect_splash(BufferedImage image, CascadeClassifier cascade, int round) {

        Mat mat = bufferedImageToMat(image);

        Mat grayMat = new Mat();

        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

        //Imgproc.equalizeHist(grayMat, grayMat);

        MatOfRect detections = new MatOfRect();

        long curms = System.currentTimeMillis();

        //cascade.detectMultiScale(grayMat, detections);

        cascade.detectMultiScale(grayMat, detections, 1.2, 2, 0, new Size(), new Size(40, 40));

        //System.out.println("Detection time: " + (System.currentTimeMillis() - curms) + "ms");

        boolean detected = false;

        Rect[] rects = detections.toArray();

        for (Rect rect : rects) {

            detected = true;

            Imgproc.rectangle(grayMat, rect.tl(), rect.br(), new Scalar(0, 255, 0, 255));

        }

        Imgcodecs.imwrite("D:\\PROJECTS\\OpenCV_Bobber_samples\\detect_splashes\\image" + round + ".jpg", grayMat);

        if (detected) {

            System.out.println("detected!");

        } else {

            //System.out.println("NOT detected!");
        }

        return detected;
    }

    public void runMouseListener(final int width, final int height, final String targetFolder) throws NativeHookException {

        if (!isNativeHookRegistered) {

            GlobalScreen.registerNativeHook();

            isNativeHookRegistered = true;
        }

        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {

            private Robot robot;
            private int filesCount;
            private File descriptor;

            {
                try {
                    robot = new Robot();

                } catch (AWTException e) {

                    e.printStackTrace();
                }

                descriptor = new File(targetFolder, "descriptor.dat");

            }

            @Override
            public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

                //GET screenshot here
                BufferedImage selectedAreaImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

                BufferedImage tmpImage = robot.createScreenCapture(new Rectangle(nativeMouseEvent.getX() - (width / 2),
                        nativeMouseEvent.getY() - (width / 2), width, height));

                Graphics2D g = selectedAreaImage.createGraphics();
                g.drawImage(tmpImage, 0, 0, width, height, null);
                g.dispose();

                File file = new File(targetFolder, filesCount + ".jpg");

                if (!file.getParentFile().exists()) {

                    file.getParentFile().mkdirs();
                }

                Mat mat = bufferedImageToMat(selectedAreaImage);

                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);

                Imgcodecs.imwrite(file.getAbsolutePath(), mat);

                try {

                    BufferedWriter writer = new BufferedWriter(new FileWriter(descriptor, true));

                    writer.write(file.getParentFile().getName() + "\\" + file.getName() + " 1 0 0 " + width + " " + height);

                    writer.newLine();

                    writer.flush();

                    writer.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }

                filesCount++;
            }

            @Override
            public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
            }
        });
    }

    public void renameSamples(String prefix, String samplesFolder, int width, int height) throws IOException {

        File folder = new File(samplesFolder);

        File[] files = folder.listFiles();

        int count = 0;

        File descriptor = new File(folder.getParent(), "descriptor.dat");

        BufferedWriter writer = new BufferedWriter(new FileWriter(descriptor));

        for (File file : files) {

            String newname = prefix + count + ".jpg";

            count++;

            Path source = Paths.get(file.getAbsolutePath());

            Files.move(source, source.resolveSibling(newname));

            writer.write(file.getParentFile().getName() + "\\" + newname + " 1 0 0 " + width + " " + height);

            writer.newLine();

        }

        writer.flush();

        writer.close();
    }

    public void unregisterNativeHook() throws NativeHookException {

        if (isNativeHookRegistered) {

            GlobalScreen.unregisterNativeHook();

            isNativeHookRegistered = false;
        }
    }

    public Mat bufferedImageToMat(BufferedImage bi) {

        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);

        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();

        mat.put(0, 0, data);

        return mat;
    }
}
