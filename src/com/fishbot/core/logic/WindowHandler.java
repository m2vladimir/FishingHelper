package com.fishbot.core.logic;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.W32APIOptions;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class allows to get window info and take screenshot.
 *
 * @author VMyakushin
 */
public class WindowHandler {

    private Rectangle areaOnScreen;
    private HWND windowHandler;


    /**
     * Constructor sets default window.
     */
    public WindowHandler(String windowName) {

        setWindow(windowName);

    }

    /**
     * Brings the specified window to the top of the Z order.
     */
    public void bringWindowToTop() {

        User32.INSTANCE.SetForegroundWindow(windowHandler);

    }


    /**
     * Take screenshot of current window center
     *
     * @return image that contains area of a window.
     */
    public BufferedImage takePicture() {

        setWindowRectangle();

        // coordinates of 3/4 window in center of current window

        int windowX = 0;

        int windowY = 0;

        int windowWidth = areaOnScreen.width * 3 / 4;

        int windowHeight = areaOnScreen.height * 3 / 4;

        return takePicture(windowX, windowY, windowWidth, windowHeight);
    }

    /**
     * Take screenshot of current window center
     *
     * @return image that contains area of a window.
     */
    public BufferedImage takePicture(int windowX, int windowY, int windowWidth, int windowHeight) {

        setWindowRectangle();

        windowX = areaOnScreen.width / 8 + windowX;

        windowY = areaOnScreen.height / 8 + windowY;

        //fix defect with IllegalArgumentException: Width (0) and height (0) cannot be <= 0
        if (windowHeight <= 0) windowHeight = 1;
        if (windowWidth <= 0) windowWidth = 1;

        // final java image structure we're returning.
        BufferedImage image = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_3BYTE_BGR);

        BufferedImage fullScreenImage = new BufferedImage(areaOnScreen.width, areaOnScreen.height, BufferedImage.TYPE_3BYTE_BGR);

        try {

            HDC hdcTarget = User32.INSTANCE.GetDC(windowHandler);

            HDC hdcTargetMem = GDI32.INSTANCE.CreateCompatibleDC(hdcTarget);

            HBITMAP hBitmap = GDI32.INSTANCE.CreateCompatibleBitmap(hdcTarget, areaOnScreen.width, areaOnScreen.height);

            HANDLE hOriginal = GDI32.INSTANCE.SelectObject(hdcTargetMem, hBitmap);

            // draw to the bitmap
            //GDI32Extra.INSTANCE.BitBlt(hdcTargetMem, 0, 0, windowWidth, windowHeight, hdcTarget, windowX, windowY, WinGDIExtra.SRCCOPY);
            User32Extra.INSTANCE.PrintWindow(windowHandler, hdcTargetMem, 1);

            BITMAPINFO bmi = new BITMAPINFO();
            bmi.bmiHeader.biWidth = areaOnScreen.width;
            bmi.bmiHeader.biHeight = -areaOnScreen.height;
            bmi.bmiHeader.biPlanes = 1;
            bmi.bmiHeader.biBitCount = 32;
            bmi.bmiHeader.biCompression = WinGDI.BI_RGB;

            Memory buffer = new Memory(areaOnScreen.width * areaOnScreen.height * 4);

            GDI32.INSTANCE.GetDIBits(hdcTarget, hBitmap, 0, areaOnScreen.height, buffer, bmi, WinGDI.DIB_RGB_COLORS);

            fullScreenImage.setRGB(0, 0, areaOnScreen.width, areaOnScreen.height, buffer.getIntArray(0, areaOnScreen.width * areaOnScreen.height), 0, areaOnScreen.width);

            image.setData(fullScreenImage.getRaster().createWritableChild(windowX, windowY, windowWidth, windowHeight, 0, 0, null));

            GDI32.INSTANCE.SelectObject(hdcTargetMem, hOriginal);

            GDI32.INSTANCE.DeleteObject(hBitmap);

            GDI32.INSTANCE.DeleteDC(hdcTargetMem);

            User32.INSTANCE.ReleaseDC(windowHandler, hdcTarget);


        } catch (Exception e) {

            throw new Win32Exception(Native.getLastError());
        }

        return image;
    }

    /**
     * Getter for areaOnScreen
     *
     * @return coordinates and size of current window
     */
    public Rectangle getWindowRectangle() {

        // coordinates of 3/4 window in center of current window
        int x = areaOnScreen.x + areaOnScreen.width / 8;

        int y = areaOnScreen.y + areaOnScreen.height / 8;

        int width = areaOnScreen.width * 3 / 4;

        int height = areaOnScreen.height * 3 / 4;

        return new Rectangle(x, y, width, height);
    }

    /**
     * Sets a new value of window rectangle
     */
    private void setWindowRectangle() {

        RECT rect = new RECT();

        User32Extra.INSTANCE.GetClientRect(windowHandler, rect);

        POINT point = new POINT();
        User32Extra.INSTANCE.ClientToScreen(windowHandler, point);

        int x = point.x;
        int y = point.y;
        int width = rect.right;
        int height = rect.bottom;

        areaOnScreen = new Rectangle(x, y, width, height);
    }

    /**
     * Checks if window is enabled for keyboard input
     *
     * @return true if window exists
     */
    public boolean isWindowEnabled() {

        return User32Extra.INSTANCE.IsWindowEnabled(windowHandler);

    }

    /**
     * Checks if window is minimized
     *
     * @return true if window minimized
     */
    public boolean isWindowMinimized() {

        return User32Extra.INSTANCE.IsIconic(windowHandler);

    }

    /**
     * Search for a window with given name and sets a window handler
     *
     * @param windowName full or part of window title
     */
    public void setWindow(final String windowName) {

        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(HWND hwnd, Pointer pointer) {

                char[] windowText = new char[1024];

                User32.INSTANCE.GetWindowText(hwnd, windowText, windowText.length);

                String title = Native.toString(windowText);

                if (title.contains(windowName)) {

                    windowHandler = hwnd;

                    return true;
                }

                return true;
            }

        }, null);

    }

    private interface User32Extra extends User32 {

        User32Extra INSTANCE = (User32Extra) Native.loadLibrary("user32", User32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean IsWindowEnabled(HWND windowHandler);

        boolean IsIconic(HWND windowHandler);

        boolean GetClientRect(HWND windowHandler, RECT rect);

        boolean ClientToScreen(HWND windowHandler, POINT point);

        boolean PrintWindow(HWND hWnd, HDC dest, int flags);

    }

    private interface WinGDIExtra extends WinGDI {

        DWORD SRCCOPY = new DWORD(0x00CC0020);

        DWORD CAPTUREBLT = new DWORD(0x00CC0020 | 0x40000000);

    }

    private interface GDI32Extra extends GDI32 {

        GDI32Extra INSTANCE = (GDI32Extra) Native.loadLibrary("gdi32", GDI32Extra.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean BitBlt(HDC hObject, int nXDest, int nYDest, int nWidth, int nHeight, HDC hObjectSource, int nXSrc, int nYSrc, DWORD dwRop);

    }
}
