package com.lzg.dynamicsource.regist;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author 刘志钢
 */
public class DbObject {

    private String user;
    private String password;
    private String driver;
    private boolean write;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DbObject{");
        sb.append("user='").append(user).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", driver='").append(driver).append('\'');
        sb.append(", write=").append(write);
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
