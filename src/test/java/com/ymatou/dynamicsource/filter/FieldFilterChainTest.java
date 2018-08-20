package com.ymatou.dynamicsource.filter;

import com.ymatou.dynamicsource.regist.DbObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 刘志钢
 */
public class FieldFilterChainTest {

    @Test
    @Disabled
    public void parseFields() {
        TestObject testObject = new TestObject();
        testObject.setAlphaWriteUrl("alphaWriteUrl");
        testObject.setMasterWriteUrl("masterWriteUrl");
        testObject.setBetaReadUser("betaReadUser");
        testObject.setSlaveReadDriver("slaveReadDriver");
        testObject.setAlphaWriteDriver("alphaWriteDriver");

        Field[] fields = testObject.getClass().getDeclaredFields();
        Map<String, DbObject> dbObjectMap = FieldFilterChain.parseFields(fields, testObject);

        assertEquals(4, dbObjectMap.size());
        assertEquals("alphaWriteDriver", dbObjectMap.get("alphaWrite").getDriver());
    }


    private static class TestObject {
        private String masterWriteUrl;

        private String slaveReadDriver;

        private String alphaWriteUrl;
        private String alphaWriteDriver;

        private String betaReadUser;

        public String getAlphaWriteDriver() {
            return alphaWriteDriver;
        }

        public void setAlphaWriteDriver(String alphaWriteDriver) {
            this.alphaWriteDriver = alphaWriteDriver;
        }

        public String getMasterWriteUrl() {
            return masterWriteUrl;
        }

        public void setMasterWriteUrl(String masterWriteUrl) {
            this.masterWriteUrl = masterWriteUrl;
        }

        public String getSlaveReadDriver() {
            return slaveReadDriver;
        }

        public void setSlaveReadDriver(String slaveReadDriver) {
            this.slaveReadDriver = slaveReadDriver;
        }

        public String getAlphaWriteUrl() {
            return alphaWriteUrl;
        }

        public void setAlphaWriteUrl(String alphaWriteUrl) {
            this.alphaWriteUrl = alphaWriteUrl;
        }

        public String getBetaReadUser() {
            return betaReadUser;
        }

        public void setBetaReadUser(String betaReadUser) {
            this.betaReadUser = betaReadUser;
        }
    }

}