package com.lzg.dynamicsource.util;

import com.lzg.dynamicsource.regist.DbObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;

import static com.lzg.dynamicsource.config.Constants.*;

public final class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

    private static final String DYNAMIC_FILE_PATH = "dynamic.path";
    private static final String DYNAMIC_FILE_NAME = "dynamic.properties";

    public static Map<String, DbObject> loadDynamicFile() {
        String path = System.getProperty(DYNAMIC_FILE_PATH);
        InputStream inputStream;

        if (StringUtils.isBlank(path)) {
            inputStream = PropertiesLoader.class.getResourceAsStream("/" + DYNAMIC_FILE_NAME);
            if (inputStream == null) {
                throw new IllegalStateException(DYNAMIC_FILE_NAME + " doesn't exist in classpath");
            }
        } else {
            try {
                inputStream = new FileInputStream(new File(path));
            } catch (FileNotFoundException e) {
                LOGGER.error("Cannot read properties file from: {}", path);
                throw new RuntimeException(e);
            }
        }
        return getDynamicSource(inputStream);
    }

    private static Map<String, DbObject> getDynamicSource(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Fail to load: {}", DYNAMIC_FILE_NAME);
        }

        Map<String, DbObject> dbObjectMap = new HashMap<>();
        properties.forEach((key, value) -> {
            Matcher matcher = PATTERN.matcher((String) key);
            if (matcher.matches()) {
                String keyName = matcher.group(1);
                String writeOrRead = matcher.group(2);
                dbObjectMap.putIfAbsent(keyName + writeOrRead, new DbObject());
                DbObject dbObject = dbObjectMap.get(keyName + writeOrRead);

                setFiledValue((String) key, dbObject, writeOrRead);
            }
        });
        return dbObjectMap;
    }

    static void setFiledValue(String keyName, DbObject dbObject, String writeOrRead) {
        getFieldValuePerSuffix(keyName, DRIVER_SUFFIX).ifPresent(dbObject::setDriver);
        getFieldValuePerSuffix(keyName, PASS_SUFFIX).ifPresent(dbObject::setPassword);
        getFieldValuePerSuffix(keyName, USER_SUFFIX).ifPresent(dbObject::setUser);
        getFieldValuePerSuffix(keyName, URL_SUFFIX).ifPresent(dbObject::setUrl);
        dbObject.setWrite(Objects.equals(WRITE_DATASOURCE, writeOrRead));
    }

    static Optional<String> getFieldValuePerSuffix(String keyName, String suffix) {
        if (keyName.endsWith(suffix)) {
            return Optional.of(keyName);
        }
        return Optional.empty();
    }

}
