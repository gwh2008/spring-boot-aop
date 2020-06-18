package com.gwh.axb.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

//import javax.validation.constraints.NotEmpty;


@ApiModel(value = "电信虚拟号传输层",description = "电信虚拟号入参")
public class AXBDTO {

    @ApiModelProperty(value = "呼叫任务订单号")
    //@NotEmpty(message = "呼叫任务订单号")
    private String callId;

    @ApiModelProperty(value = "区号")
    private String areaCode;

    @ApiModelProperty(value = "任务过期时间，单位秒最大不得超过60天")
    //@NotEmpty(message = "任务过期时间，单位秒最大不得超过60天")
    private String expiredTime;

    @ApiModelProperty(value = "主叫号码")
    //@NotEmpty(message = "主叫号码")
    private String callerNbr;

    @ApiModelProperty(value = "被叫号码")
    //@NotEmpty(message = "被叫号码")
    private String calledNbr;

    @ApiModelProperty(value = "城市匹配类型，\n" +
            "0：精准匹配、\n" +
            "1：模糊匹配默认为0 ")
    private String matchType;

    @ApiModelProperty(value = "单向透传被叫号码来电显示，TRANSMISSION_A：AXB显示A，BXA显示X、 TRANSMISSION_NONE：AXB，BXA显示X、TRANSMISSION_B：AXB显示X，BXA显示B")
    private String singleTransmission;

    public AXBDTO() {
    }

    public AXBDTO( String callId, String areaCode,  String expiredTime,  String callerNbr,  String calledNbr, String matchType, String singleTransmission) {
        this.callId = callId;
        this.areaCode = areaCode;
        this.expiredTime = expiredTime;
        this.callerNbr = callerNbr;
        this.calledNbr = calledNbr;
        this.matchType = matchType;
        this.singleTransmission = singleTransmission;
    }

    @Override
    public String toString() {
        return "AXBDTO{" +
                "callId='" + callId + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", expiredTime='" + expiredTime + '\'' +
                ", callerNbr='" + callerNbr + '\'' +
                ", calledNbr='" + calledNbr + '\'' +
                ", matchType='" + matchType + '\'' +
                ", singleTransmission='" + singleTransmission + '\'' +
                '}';
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public String getCallerNbr() {
        return callerNbr;
    }

    public void setCallerNbr(String callerNbr) {
        this.callerNbr = callerNbr;
    }

    public String getCalledNbr() {
        return calledNbr;
    }

    public void setCalledNbr(String calledNbr) {
        this.calledNbr = calledNbr;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getSingleTransmission() {
        return singleTransmission;
    }

    public void setSingleTransmission(String singleTransmission) {
        this.singleTransmission = singleTransmission;
    }
}
