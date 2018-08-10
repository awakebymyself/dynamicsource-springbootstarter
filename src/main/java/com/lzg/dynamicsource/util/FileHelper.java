package com.lzg.dynamicsource.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public final class FileHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);

    private static final String DYNAMIC_FILE_PATH = "dynamic.path";
    private static final String DYNAMIC_FILE_NAME = "dynamic.properties";

    public static void loadDynamicFile() {
        String path = System.getProperty(DYNAMIC_FILE_PATH);
        InputStream inputStream;

        if (StringUtils.isBlank(path)) {
            inputStream = FileHelper.class.getResourceAsStream(DYNAMIC_FILE_NAME);
            if (inputStream == null) {
                throw new IllegalStateException(DYNAMIC_FILE_NAME + "doesn't exist in classpath");
            }
        } else {
            try {
                inputStream = new FileInputStream(new File(DYNAMIC_FILE_PATH));
            } catch (FileNotFoundException e) {
                LOGGER.error("Cannot read properties file from: {}", path);
                throw new RuntimeException(e);
            }
        }

        getDynamicSource(inputStream);
    }


    private static void getDynamicSource(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Fail to load: {}", DYNAMIC_FILE_NAME);
        }
        Set<String> keyNames = properties.stringPropertyNames();

    }


}
