package net.engining.gm.autoconfigure.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 通用配置开关项
 *
 * @author : Eric Lu
 **/
@ConfigurationProperties("gm.config.enabled")
public class GmEnabledProperties {

    boolean async = false;
    boolean parameter = false;
    boolean retry = false;
    boolean ribbon = false;
    boolean snowflakeSequence = false;
    boolean swagger = false;
    boolean scheduling = false;
    boolean web = false;
    boolean webmvc = false;
    boolean webflux = false;
    boolean security = false;
    boolean oauth2 = false;

    public boolean isOauth2() {
        return oauth2;
    }

    public void setOauth2(boolean oauth2) {
        this.oauth2 = oauth2;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isParameter() {
        return parameter;
    }

    public void setParameter(boolean parameter) {
        this.parameter = parameter;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public boolean isRibbon() {
        return ribbon;
    }

    public void setRibbon(boolean ribbon) {
        this.ribbon = ribbon;
    }

    public boolean isSnowflakeSequence() {
        return snowflakeSequence;
    }

    public void setSnowflakeSequence(boolean snowflakeSequence) {
        this.snowflakeSequence = snowflakeSequence;
    }

    public boolean isSwagger() {
        return swagger;
    }

    public void setSwagger(boolean swagger) {
        this.swagger = swagger;
    }

    public boolean isScheduling() {
        return scheduling;
    }

    public void setScheduling(boolean scheduling) {
        this.scheduling = scheduling;
    }

    public boolean isWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    public boolean isWebmvc() {
        return webmvc;
    }

    public void setWebmvc(boolean webmvc) {
        this.webmvc = webmvc;
    }

    public boolean isWebflux() {
        return webflux;
    }

    public void setWebflux(boolean webflux) {
        this.webflux = webflux;
    }
}
