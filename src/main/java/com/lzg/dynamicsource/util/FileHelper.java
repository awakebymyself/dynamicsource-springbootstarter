package com.lzg.dynamicsource.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public final class FileHelper {

    public static void loadClasspathFile(String fileName) {
        InputStream resource = FileHelper.class.getClassLoader().getResourceAsStream(fileName);
        if (resource != null) {
            Properties properties = new Properties();
            try {
                properties.load(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
