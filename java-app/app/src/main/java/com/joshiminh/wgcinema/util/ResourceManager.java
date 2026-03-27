package com.joshiminh.wgcinema.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Utility class to manage resources like icons and images.
 */
public class ResourceManager {

    // Prevent instantiation
    private ResourceManager() {
        super();
    }

    private static final Class<?> BASE_CLASS = com.joshiminh.wgcinema.App.class;

    /**
     * Loads an icon for the application.
     *
     * @return The app icon or null if not found.
     */
    public static Image loadAppIcon() {
        return loadImage("/images/app_icon.png");
    }

    /**
     * Loads an image from the resources folder.
     *
     * @param path The relative path to the image.
     * @return The loaded image or null if not found.
     */
    public static Image loadImage(String path) {
        URL url = BASE_CLASS.getResource(path);
        if (url == null) {
            System.err.println("Resource not found: " + path);
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    /**
     * Resizes an icon to the specified width and height.
     *
     * @param path   The path to the icon.
     * @param width  The target width.
     * @param height The target height.
     * @return The resized ImageIcon.
     */
    public static ImageIcon loadResizedIcon(String path, int width, int height) {
        Image img = loadImage(path);
        if (img == null)
            return null;
        return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}