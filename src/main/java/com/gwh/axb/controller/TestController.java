package com.gwh.axb.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gwh.axb.config.SystemControllerLog;
import com.gwh.axb.util.AesUtils;
import com.gwh.axb.util.RsaUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

//import org.json.JSONArray;
//import org.json.JSONObject;


@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    private static final String APP_ID = "App-Id";
    private static final String ACCESS_KEY = "Access-Key";
    private static final String ACCESS_OFFSET = "Access-Offset";

    private static final String HOST = "http://127.0.0.1:10000";
    // 公钥
    private static final String PUBLIC_KEY_AXB = "";
    // appId
    private static final String APP_ID_VALUE_AXB = "";
    // app secret
    private static final String APP_SECRET_VALUE_AXB = "";

    @Resource
    private RestTemplate restTemplate;


    /*测试返回体*/
    @SystemControllerLog(operation = "测试",type = "info")
    @GetMapping("/api/test")
    @ResponseBody
    public String test(){
        String str="相信未来";
        if (str!=null){
            //RUtils是我自己的统一返回体，根据自己情况
            return "str: "+str;
        }else {
            //这是自己的全局异常处理
            //throw new Exception();
            return "str-null: "+str;
        }
    }



    @SystemControllerLog(operation = "测试",type = "info")
    @PostMapping("/axb/bind")
    public String bind(@RequestParam("callId")String callId,
                       @RequestParam("areaCode")String areaCode,@RequestParam("expiredTime")String expiredTime,
                       @RequestParam("callerNbr")String callerNbr,@RequestParam("calledNbr")String calledNbr,
                       @RequestParam("matchType")String matchType,@RequestParam("singleTransmission")String singleTransmission) throws UnsupportedEncodingException {

        System.out.println("--orderService--bind-callId "+ callId+" callerNbr "+callerNbr +" calledNbr "+ calledNbr );
        log.info("--orderService--bind--callId-{},callerNbr--{},--calledNbr-{}",callId,callerNbr,calledNbr);

        String BASE_URL = HOST + "/api/open/axb/v1.4/number/bind";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        String aesKey = AesUtils.getRandomString(16);
        String aesIv = AesUtils.getRandomString(16);
        String accessKey = RsaUtils.encryptByPublicKey(aesKey, PUBLIC_KEY_AXB);
        String accessIv = RsaUtils.encryptByPublicKey(aesIv, PUBLIC_KEY_AXB);
        Map<String, Object> body = new HashMap<>();
        body.put("callId", callId);
        body.put("areaCode", areaCode);
        body.put("expiredTime", expiredTime);
        body.put("callerNbr", callerNbr);
        body.put("calledNbr", calledNbr);
        body.put("matchType", matchType);
        body.put("singleTransmission", singleTransmission);


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


    @PostMapping("/axb/unbind")
    public String bind(@RequestParam("callId")String callId) throws UnsupportedEncodingException {


        String BASE_URL = HOST + "/api/open/axb/v1.4/number/unbind";

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
        return "hello";
    }


    @PostMapping("/axb/getInfo")
    public String getInfo(@RequestParam("callId")String callId,@RequestParam("number")String number,@RequestParam("type")Integer type) throws UnsupportedEncodingException {


        String BASE_URL = HOST + "/api/open/axb/v1.4/number/getInfo";

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
