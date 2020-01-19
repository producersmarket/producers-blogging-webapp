package com.producersmarket.blog.model;

import java.util.Date;

public class Session
{

    private int id = -1;
    private int userId = -1;
    private String sessionId = null;
    private String remoteAddr = null;
    private String accept = null;
    private String acceptEncoding = null;
    private String acceptLanguage = null;
    private String acceptCharset = null;
    private String host = null;
    private String userAgent = null;
    private String protocol = null;
    private String characterEncoding = null;
    private String locale = null;
    private String referer = null;
    private String serverInfo = null;
    private String serverName = null;
    private int serverPort = -1;
    private Date dateCreated = null;

    public Session() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAccept() {
        return accept;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptCharset(String acceptCharset) {
        this.acceptCharset = acceptCharset;
    }

    public String getAcceptCharset() {
        return acceptCharset;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol(){
        return protocol;
    }
    
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getReferer() {
        return referer;
    }

    public void setServerInfo(String serverInfo) {
        this.serverInfo = serverInfo;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

}
