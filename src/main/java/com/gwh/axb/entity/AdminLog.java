package com.gwh.axb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

//import lombok.Data;

/**
 * @version V1.0
 * @Package com.gwh.axb.entity
 * @author: gaowenhui
 * @Date: 10:49
 */
//@Data
@Component
public class AdminLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer logId;                   //日志主键
    private String type;                     //日志类型
    private String operation;                 //日志操作事件描述
    private String remoteAddr;                //请求地址ip
    private String requestUri;                //URI
    private String method;                   //请求方式
    private String params;                   //提交参数
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateDate;                    //开始时间
    private Integer userId;                    //用户ID
    private String userName;                 //用户名称
    private String resultParams;            //返回参数
    private String exceptionLog;           //异常描述


    public AdminLog() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getResultParams() {
        return resultParams;
    }

    public void setResultParams(String resultParams) {
        this.resultParams = resultParams;
    }

    public String getExceptionLog() {
        return exceptionLog;
    }

    public void setExceptionLog(String exceptionLog) {
        this.exceptionLog = exceptionLog;
    }


    public AdminLog(Integer logId, String type, String operation, String remoteAddr, String requestUri, String method, String params, Date operateDate, Integer userId, String userName, String resultParams, String exceptionLog) {
        this.logId = logId;
        this.type = type;
        this.operation = operation;
        this.remoteAddr = remoteAddr;
        this.requestUri = requestUri;
        this.method = method;
        this.params = params;
        this.operateDate = operateDate;
        this.userId = userId;
        this.userName = userName;
        this.resultParams = resultParams;
        this.exceptionLog = exceptionLog;
    }
}
