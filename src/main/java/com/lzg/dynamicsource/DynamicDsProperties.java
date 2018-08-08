package com.lzg.dynamicsource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dynamic.ds")
public class DynamicDsProperties {

    private String pointCut;

    private String masterUrl;

    private String masterName;

    private String masterUserName;

    private String masterPassword;

    private String masterJdbcDriver;


    ///
    private String slaveUrl;

    private String slaveName;

    private String slaveUserName;

    private String slavePassword;

    private String slaveJdbcDriver;



    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }

    public String getMasterUrl() {
        return masterUrl;
    }

    public void setMasterUrl(String masterUrl) {
        this.masterUrl = masterUrl;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getMasterUserName() {
        return masterUserName;
    }

    public void setMasterUserName(String masterUserName) {
        this.masterUserName = masterUserName;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public String getMasterJdbcDriver() {
        return masterJdbcDriver;
    }

    public void setMasterJdbcDriver(String masterJdbcDriver) {
        this.masterJdbcDriver = masterJdbcDriver;
    }

    public String getSlaveUrl() {
        return slaveUrl;
    }

    public void setSlaveUrl(String slaveUrl) {
        this.slaveUrl = slaveUrl;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public void setSlaveName(String slaveName) {
        this.slaveName = slaveName;
    }

    public String getSlaveUserName() {
        return slaveUserName;
    }

    public void setSlaveUserName(String slaveUserName) {
        this.slaveUserName = slaveUserName;
    }

    public String getSlavePassword() {
        return slavePassword;
    }

    public void setSlavePassword(String slavePassword) {
        this.slavePassword = slavePassword;
    }

    public String getSlaveJdbcDriver() {
        return slaveJdbcDriver;
    }

    public void setSlaveJdbcDriver(String slaveJdbcDriver) {
        this.slaveJdbcDriver = slaveJdbcDriver;
    }

    @Override
    public String toString() {
        return "DynamicDsProperties{" +
                "pointCut='" + pointCut + '\'' +
                ", masterUrl='" + masterUrl + '\'' +
                ", masterName='" + masterName + '\'' +
                ", masterUserName='" + masterUserName + '\'' +
                ", masterPassword='" + masterPassword + '\'' +
                ", masterJdbcDriver='" + masterJdbcDriver + '\'' +
                ", slaveUrl='" + slaveUrl + '\'' +
                ", slaveName='" + slaveName + '\'' +
                ", slaveUserName='" + slaveUserName + '\'' +
                ", slavePassword='" + slavePassword + '\'' +
                ", slaveJdbcDriver='" + slaveJdbcDriver + '\'' +
                '}';
    }
}
