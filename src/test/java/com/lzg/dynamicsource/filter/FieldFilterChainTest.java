package com.lzg.dynamicsource.filter;

import com.lzg.dynamicsource.regist.DbObject;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author 刘志钢
 */
public class FieldFilterChainTest {

    @Test
    public void parseFields() {
        TestObject testObject = new TestObject();
        testObject.setAlphaUrl("alphaUrl");
        testObject.setMasterUrl("masterUrl");
        testObject.setBetaUser("betaUser");
        testObject.setSlaveDriver("slaveDriver");
        testObject.setAlphaDriver("alphaDriver");

        Field[] fields = testObject.getClass().getDeclaredFields();
        Map<String, DbObject> dbObjectMap = FieldFilterChain.parseFields(fields, testObject);

        Assert.assertEquals(4, dbObjectMap.size());
        Assert.assertEquals("alphaDriver", dbObjectMap.get("alpha").getDriver());
    }


    private static class TestObject {
        private String masterUrl;

        private String slaveDriver;

        private String alphaUrl;
        private String alphaDriver;

        private String betaUser;

        public String getAlphaDriver() {
            return alphaDriver;
        }

        public void setAlphaDriver(String alphaDriver) {
            this.alphaDriver = alphaDriver;
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

        public String getAlphaUrl() {
            return alphaUrl;
        }

        public void setAlphaUrl(String alphaUrl) {
            this.alphaUrl = alphaUrl;
        }

        public String getBetaUser() {
            return betaUser;
        }

        public void setBetaUser(String betaUser) {
            this.betaUser = betaUser;
        }
    }
}