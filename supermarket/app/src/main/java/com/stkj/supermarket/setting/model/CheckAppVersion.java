package com.stkj.supermarket.setting.model;

public class CheckAppVersion {

    private String version;
    private String content;
    private String url;
    private String versionForce;

    public CheckAppVersion() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionForce() {
        return versionForce;
    }

    public void setVersionForce(String versionForce) {
        this.versionForce = versionForce;
    }
}