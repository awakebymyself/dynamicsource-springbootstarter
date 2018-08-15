package com.lzg.dynamicsource.util;

import com.lzg.dynamicsource.regist.DbObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * @author 刘志钢
 */
class PropertiesLoaderTest {

    @Test
    @DisplayName("从资源中加载解析properties文件")
    void loadDynamicFile() {
        Map<String, DbObject> dbObjectMap = PropertiesLoader.loadDynamicFile();

        assertEquals(5, dbObjectMap.size());
        assertEquals(2, dbObjectMap.values().stream().filter(DbObject::isWrite).count());

        assumeTrue(dbObjectMap.containsKey("bSecondRead"));
        assertEquals("bSecondReadDriver", dbObjectMap.get("bSecondRead").getDriver());
    }

    @Test
    @Tag("external")
    void loadPropertiesFromExternal() {
        System.setProperty("dynamic.path","C:\\usr\\local\\config\\dynamic.properties");


        loadDynamicFile();
    }

}