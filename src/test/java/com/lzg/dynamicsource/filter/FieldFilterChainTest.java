package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 刘志钢
 */
public class FieldFilterChainTest {

    @Test
    public void parseFields() {
        TestObject testObject = new TestObject();
        testObject.setAlphaWriteUrl("alphaWriteUrl");
        testObject.setMasterUrl("masterUrl");
        testObject.setBetaReadUser("betaReadUser");
        testObject.setSlaveDriver("slaveDriver");
        testObject.setAlphaWriteDriver("alphaWriteDriver");

        Field[] fields = testObject.getClass().getDeclaredFields();
        Map<String, DbObject> dbObjectMap = FieldFilterChain.parseFields(fields, testObject);

        assertEquals(4, dbObjectMap.size());
        assertEquals("alphaWriteDriver", dbObjectMap.get("alphaWrite").getDriver());
    }


    private static class TestObject {
        private String masterUrl;

        private String slaveDriver;

        private String alphaWriteUrl;
        private String alphaWriteDriver;

        private String betaReadUser;

        public String getAlphaWriteDriver() {
            return alphaWriteDriver;
        }

        public void setAlphaWriteDriver(String alphaWriteDriver) {
            this.alphaWriteDriver = alphaWriteDriver;
        }

        public String getMasterUrl() {
            return masterUrl;
        }

        public void setMasterUrl(String masterUrl) {
            this.masterUrl = masterUrl;
        }

        public String getSlaveDriver() {
            return slaveDriver;
        }

        public void setSlaveDriver(String slaveDriver) {
            this.slaveDriver = slaveDriver;
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