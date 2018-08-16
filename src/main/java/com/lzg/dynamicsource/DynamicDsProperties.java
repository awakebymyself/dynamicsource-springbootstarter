package com.lzg.dynamicsource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("dynamic.ds")
public class DynamicDsProperties {

    private String pointCut;

    private String defaultWrite;

    private String defaultRead;


    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }

    public String getDefaultWrite() {
        return defaultWrite;
    }

    public void setDefaultWrite(String defaultWrite) {
        this.defaultWrite = defaultWrite;
    }

    public String getDefaultRead() {
        return defaultRead;
    }

    public void setDefaultRead(String defaultRead) {
        this.defaultRead = defaultRead;
    }
}
