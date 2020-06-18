package com.gwh.axb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.gwh.axb.entity.AXBDTO;
import com.gwh.axb.util.AesUtils;
import com.gwh.axb.util.RsaUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Created by gaowenhui on 2020/6/16
 */
@Service
public class AXBService {
    private static final Logger log = LoggerFactory.getLogger(AXBService.class);

    private static final String APP_ID = "App-Id";
    private static final String ACCESS_KEY = "Access-Key";
    private static final String ACCESS_OFFSET = "Access-Offset";
//    private static final String AXB_HOST = ConfigUtil.getParamAsStr("AXB_HOST");
//    // 公钥
//    private static final String PUBLIC_KEY_AXB =  ConfigUtil.getParamAsStr("PUBLIC_KEY_AXB");
//    // appId
//    private static final String APP_ID_VALUE_AXB =  ConfigUtil.getParamAsStr("APP_ID_VALUE_AXB");
//    // app secret
//    private static final String APP_SECRET_VALUE_AXB =  ConfigUtil.getParamAsStr("APP_SECRET_VALUE_AXB");
    private static final String AXB_HOST = "http://www.ctyunting.com:10000/";
    // 公钥
    private static final String PUBLIC_KEY_AXB = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApTcx6g6Q+3d3YWcI+mz5DVIG2zBsjXldkJn+cqpPCLFcjyoAtNvUx1IbQSD+VtaBdBFbQJan3Vmb9HMDPoz6NMzJIHJmuhZEK4fMabnPagfl0Udec3tQs2IdBhcfthnB9XXspswizoPt6ZhRTK5sTGXcJtkiGpvVPq5c2Ynzs9St4IWzL0Crx8G87i9k1P3cVgyi0++Dz7JZIQHeGAOMpEEpys/R50Aumk0Gq5y1VziBG4+7xv6OvNuGPmg/WgJclqAYDzU1bQVFFatuNNx2e4DvcxBDxgagmN+XHfH4Q6KBlfD0bEZZwh+zGiB+cKzEzGxeD3iQzRuEOawqp5do9wIDAQAB";
    // appId
    private static final String APP_ID_VALUE_AXB = "5a52b4bdc5757b8f3536";
    // app secret
    private static final String APP_SECRET_VALUE_AXB = "57306f68565a624b717039791744dd29b681aca1";

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    //@SystemControllerLog(operation = "电信虚拟号",type = "info")
    public String bind(@RequestParam("callId")String callId,
                       @RequestParam("areaCode")String areaCode, @RequestParam("expiredTime")String expiredTime,
                       @RequestParam("callerNbr")String callerNbr, @RequestParam("calledNbr")String calledNbr,
                       @RequestParam("matchType")String matchType, @RequestParam("singleTransmission")String singleTransmission) throws UnsupportedEncodingException {

        System.out.println("--AXBService--bind-callId "+ callId+" callerNbr "+callerNbr +" calledNbr "+ calledNbr +" areaCode "+ areaCode +" expiredTime "+ expiredTime +" matchType "+ matchType +" singleTransmission "+ singleTransmission );
        log.info("--AXBService--bind--callId-{},callerNbr--{},--calledNbr-{},--areaCode-{},--expiredTime-{},--matchType-{},--singleTransmission-{}",callId,callerNbr,calledNbr,areaCode,expiredTime,matchType,singleTransmission);
        String BASE_URL = AXB_HOST + "/api/open/axb/v1.4/number/bind";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String aesKey = AesUtils.getRandomString(16);
        String aesIv = AesUtils.getRandomString(16);
        String accessKey = RsaUtils.encryptByPublicKey(aesKey, PUBLIC_KEY_AXB);
        String accessIv = RsaUtils.encryptByPublicKey(aesIv, PUBLIC_KEY_AXB);
        Map<String, Object> body = new HashMap<>();
        body.put("callId", callId==null?"":callId);
        if(StringUtils.isBlank(areaCode)){
            body.put("areaCode", 010);
        }else {
            body.put("areaCode", areaCode);
        }
        if(StringUtils.isBlank(expiredTime)){
            body.put("expiredTime", 600);
        }else {
            body.put("expiredTime", expiredTime);
        }
        body.put("callerNbr", callerNbr);
        body.put("calledNbr", calledNbr);
        if(StringUtils.isBlank(matchType)){
            body.put("matchType", 1);
        }else {
            body.put("matchType", matchType);
        }
        if(StringUtils.isBlank(singleTransmission)){
            body.put("singleTransmission", "TRANSMISSION_NONE");
        }else {
            body.put("singleTransmission", singleTransmission);
        }
        JSONObject jsonObject = new JSONObject(body);
        String requestBodyString = jsonObject.toString() + DigestUtils.sha256Hex(getValues(jsonObject) + APP_SECRET_VALUE_AXB);
        headers.add(APP_ID, APP_ID_VALUE_AXB);
        headers.add(ACCESS_KEY, accessKey);
        headers.add(ACCESS_OFFSET, accessIv);
        headers.setContentType(MediaType.TEXT_PLAIN);
        String cipherText = AesUtils.encrypt(requestBodyString, aesKey, aesIv);
        HttpEntity<String> entity = new HttpEntity<>(URLEncoder.encode(cipherText, "UTF-8"), headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(URI.create(BASE_URL), entity, String.class);
            String decryptKey = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_KEY), PUBLIC_KEY_AXB);
            String decryptIv = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_OFFSET), PUBLIC_KEY_AXB);
            String responseBodyString = AesUtils.decrypt(URLDecoder.decode(Objects.requireNonNull(responseEntity.getBody()),
                    StandardCharsets.UTF_8.name()), decryptKey, decryptIv);
            int index = responseBodyString.lastIndexOf("}") + 1;
            String jsonString = responseBodyString.substring(0, index);
            log.info("电信返回的数据=>{}", jsonString);
            if (responseBodyString.length() > jsonString.length()) {
                String originSign = responseBodyString.substring(index);
                String localSign = DigestUtils.sha256Hex(getValues(originSign) + APP_SECRET_VALUE_AXB);
                if (Objects.equals(originSign, localSign)) {// 签名校验通过
                    log.info("解密后数据=>{}", jsonString);
                } else {// 签名校验失败
                    log.warn("签名不一致");
                }
            }
            JSONObject resJson = JSONObject.parseObject(jsonString);
            if(resJson.getString("code").equals("0")){
                JSONObject resultJson = resJson.getJSONObject("result");
                String workNbr = resultJson.getString("workNbr");
                return workNbr;
            }else {
                return resJson.getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "电信虚拟号";
    }

    //@SystemControllerLog(operation = "绑定电信虚拟号",type = "info")
    public String bindaxb(AXBDTO dto)throws UnsupportedEncodingException{
        String BASE_URL = AXB_HOST + "/api/open/axb/v1.4/number/bind";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String aesKey = AesUtils.getRandomString(16);
        String aesIv = AesUtils.getRandomString(16);
        String accessKey = RsaUtils.encryptByPublicKey(aesKey, PUBLIC_KEY_AXB);
        String accessIv = RsaUtils.encryptByPublicKey(aesIv, PUBLIC_KEY_AXB);
        Map<String, Object> body = new HashMap<>();
        body.put("callId", dto.getCallId()==null?"": dto.getCallId());
        if(StringUtils.isBlank(dto.getAreaCode())){
            body.put("areaCode", 010);
        }else {
            body.put("areaCode", dto.getAreaCode());
        }
        if(StringUtils.isBlank(dto.getExpiredTime())){
            body.put("expiredTime", 600);
        }else {
            body.put("expiredTime", dto.getExpiredTime());
        }
        body.put("callerNbr", dto.getCallerNbr());
        body.put("calledNbr", dto.getCallerNbr());
        if(StringUtils.isBlank(dto.getMatchType())){
            body.put("matchType", 1);
        }else {
            body.put("matchType", dto.getMatchType());
        }
        if(StringUtils.isBlank(dto.getSingleTransmission())){
            body.put("singleTransmission", "TRANSMISSION_NONE");
        }else {
            body.put("singleTransmission", dto.getSingleTransmission());
        }
        JSONObject jsonObject = new JSONObject(body);
        String requestBodyString = jsonObject.toString() + DigestUtils.sha256Hex(getValues(jsonObject) + APP_SECRET_VALUE_AXB);
        headers.add(APP_ID, APP_ID_VALUE_AXB);
        headers.add(ACCESS_KEY, accessKey);
        headers.add(ACCESS_OFFSET, accessIv);
        headers.setContentType(MediaType.TEXT_PLAIN);
        String cipherText = AesUtils.encrypt(requestBodyString, aesKey, aesIv);
        HttpEntity<String> entity = new HttpEntity<>(URLEncoder.encode(cipherText, "UTF-8"), headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(URI.create(BASE_URL), entity, String.class);
            String decryptKey = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_KEY), PUBLIC_KEY_AXB);
            String decryptIv = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_OFFSET), PUBLIC_KEY_AXB);
            String responseBodyString = AesUtils.decrypt(URLDecoder.decode(Objects.requireNonNull(responseEntity.getBody()),
                    StandardCharsets.UTF_8.name()), decryptKey, decryptIv);
            int index = responseBodyString.lastIndexOf("}") + 1;
            String jsonString = responseBodyString.substring(0, index);
            log.info("电信返回的数据=>{}", jsonString);
            if (responseBodyString.length() > jsonString.length()) {
                String originSign = responseBodyString.substring(index);
                String localSign = DigestUtils.sha256Hex(getValues(originSign) + APP_SECRET_VALUE_AXB);
                if (Objects.equals(originSign, localSign)) {// 签名校验通过
                    log.info("解密后数据=>{}", jsonString);
                } else {// 签名校验失败
                    log.warn("签名不一致");
                }
            }
            JSONObject resJson = JSONObject.parseObject(jsonString);
            if(resJson.getString("code").equals("0")){
                JSONObject resultJson = resJson.getJSONObject("result");
                String workNbr = resultJson.getString("workNbr");
                return workNbr;
            }else {
                return resJson.getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "电信虚拟号";
    }

    public String unbind(@RequestParam("callId")String callId) throws UnsupportedEncodingException {
        String BASE_URL = AXB_HOST + "/api/open/axb/v1.4/number/unbind";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String aesKey = AesUtils.getRandomString(16);
        String aesIv = AesUtils.getRandomString(16);
        String accessKey = RsaUtils.encryptByPublicKey(aesKey, PUBLIC_KEY_AXB);
        String accessIv = RsaUtils.encryptByPublicKey(aesIv, PUBLIC_KEY_AXB);
        Map<String, Object> body = new HashMap<>();
        body.put("callId", callId);
        JSONObject jsonObject = new JSONObject(body);
        String requestBodyString = jsonObject.toString() + DigestUtils.sha256Hex(getValues(jsonObject) + APP_SECRET_VALUE_AXB);
        headers.add(APP_ID, APP_ID_VALUE_AXB);
        headers.add(ACCESS_KEY, accessKey);
        headers.add(ACCESS_OFFSET, accessIv);
        headers.setContentType(MediaType.TEXT_PLAIN);
        String cipherText = AesUtils.encrypt(requestBodyString, aesKey, aesIv);
        HttpEntity<String> entity = new HttpEntity<>(URLEncoder.encode(cipherText, "UTF-8"), headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(URI.create(BASE_URL), entity, String.class);
            String decryptKey = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_KEY), PUBLIC_KEY_AXB);
            String decryptIv = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_OFFSET), PUBLIC_KEY_AXB);
            String responseBodyString = AesUtils.decrypt(URLDecoder.decode(Objects.requireNonNull(responseEntity.getBody()),
                    StandardCharsets.UTF_8.name()), decryptKey, decryptIv);
            int index = responseBodyString.lastIndexOf("}") + 1;
            String jsonString = responseBodyString.substring(0, index);
            if (responseBodyString.length() > jsonString.length()) {
//                String originSign = responseBodyString.substring(index);
//                JSONObject respObj = new JSONObject(jsonString);
//                String localSign = DigestUtils.sha256Hex(getValues(respObj) + APP_SECRET_VALUE_AXB);
//                if (Objects.equals(originSign, localSign)) {// 签名校验通过
//                    log.info("解密后数据=>{}", jsonString);
//                } else {// 签名校验失败
//                    log.warn("签名不一致");
//                }
                String originSign = responseBodyString.substring(index);
                String localSign = DigestUtils.sha256Hex(getValues(originSign) + APP_SECRET_VALUE_AXB);
                if (Objects.equals(originSign, localSign)) {// 签名校验通过
                    log.info("解密后数据=>{}", jsonString);
                } else {// 签名校验失败
                    log.warn("签名不一致");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    public String getInfo(@RequestParam("callId")String callId, @RequestParam("number")String number, @RequestParam("type")Integer type) throws UnsupportedEncodingException {


        String BASE_URL = AXB_HOST + "/api/open/axb/v1.4/number/getInfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String aesKey = AesUtils.getRandomString(16);
        String aesIv = AesUtils.getRandomString(16);
        String accessKey = RsaUtils.encryptByPublicKey(aesKey, PUBLIC_KEY_AXB);
        String accessIv = RsaUtils.encryptByPublicKey(aesIv, PUBLIC_KEY_AXB);
        Map<String, Object> body = new HashMap<>();
        if (type==1){
            body.put("callId", callId);
        }
        body.put("workNbr", number);

        JSONObject jsonObject = new JSONObject(body);
        String requestBodyString = jsonObject.toString() + DigestUtils.sha256Hex(getValues(jsonObject) + APP_SECRET_VALUE_AXB);
        headers.add(APP_ID, APP_ID_VALUE_AXB);
        headers.add(ACCESS_KEY, accessKey);
        headers.add(ACCESS_OFFSET, accessIv);
        headers.setContentType(MediaType.TEXT_PLAIN);
        String cipherText = AesUtils.encrypt(requestBodyString, aesKey, aesIv);
        HttpEntity<String> entity = new HttpEntity<>(URLEncoder.encode(cipherText, "UTF-8"), headers);
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.postForEntity(URI.create(BASE_URL), entity, String.class);
            String decryptKey = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_KEY), PUBLIC_KEY_AXB);
            String decryptIv = RsaUtils.decryptByPublicKey(responseEntity.getHeaders().getFirst(ACCESS_OFFSET), PUBLIC_KEY_AXB);
            String responseBodyString = AesUtils.decrypt(URLDecoder.decode(Objects.requireNonNull(responseEntity.getBody()),
                    StandardCharsets.UTF_8.name()), decryptKey, decryptIv);
            int index = responseBodyString.lastIndexOf("}") + 1;
            String jsonString = responseBodyString.substring(0, index);
            if (responseBodyString.length() > jsonString.length()) {
//                String originSign = responseBodyString.substring(index);
//                JSONObject respObj = new JSONObject(jsonString);
//                String localSign = DigestUtils.sha256Hex(getValues(respObj) + APP_SECRET_VALUE_AXB);
//                if (Objects.equals(originSign, localSign)) {// 签名校验通过
//                    log.info("解密后数据=>{}", jsonString);
//                } else {// 签名校验失败
//                    log.warn("签名不一致");
//                }
                String originSign = responseBodyString.substring(index);
                String localSign = DigestUtils.sha256Hex(getValues(originSign) + APP_SECRET_VALUE_AXB);
                if (Objects.equals(originSign, localSign)) {// 签名校验通过
                    log.info("解密后数据=>{}", jsonString);
                } else {// 签名校验失败
                    log.warn("签名不一致");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "hello";
    }

    // 获取json values String
    private String getValues(Object obj) {
        StringBuilder result = new StringBuilder();
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            TreeSet<String> keys = new TreeSet<>(jsonObject.keySet());
            for (String key : keys) {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    result.append(getValues(value));
                } else {
                    result.append(value);
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (Object value : jsonArray) {
                result.append(getValues(value));
            }
        } else {
            result.append(obj);
        }
        return result.toString();
    }

    @Bean
    public RestTemplate providerRest() {

        return new RestTemplate();
    }
}
