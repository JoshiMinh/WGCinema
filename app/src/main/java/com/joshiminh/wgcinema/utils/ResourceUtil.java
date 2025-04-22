package com.joshiminh.wgcinema.utils;

import javax.swing.*;
import java.awt.Image;
import java.net.URL;

public class ResourceUtil {
    private static final Class<?> BASE_CLASS = com.joshiminh.wgcinema.App.class;

    public static URL getResource(String path) {
        return BASE_CLASS.getResource(path);
    }

    public static Image loadImage(String path) {
        URL resource = getResource(path);
        return resource != null ? new ImageIcon(resource).getImage() : null;
    }

    public static Image loadAppIcon() {
        return loadImage("/images/icon.png");
    }
}