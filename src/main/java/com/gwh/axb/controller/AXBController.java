package com.gwh.axb.controller;


import com.gwh.axb.entity.AXBDTO;
import com.gwh.axb.service.AXBService;
import com.gwh.axb.util.ErrorEnum;
import com.gwh.axb.util.ResponseEntity;
import com.gwh.axb.util.ResponseUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * Created by gaowenhui on 2020/6/16
 */
@RestController
@RequestMapping("/virtual")
@Api(tags = "电信虚拟号接口控制层")
public class AXBController {

    private static final Logger log = LoggerFactory.getLogger(AXBController.class);

    @Resource
    AXBService AXBService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @PostMapping("/axb/bind")
    @ApiOperation("绑定电信虚拟号")
    public ResponseEntity bind(@RequestParam("callId")String callId,
                               @RequestParam("areaCode")String areaCode, @RequestParam("expiredTime")String expiredTime,
                               @RequestParam("callerNbr")String callerNbr, @RequestParam("calledNbr")String calledNbr,
                               @RequestParam("matchType")String matchType, @RequestParam("singleTransmission")String singleTransmission) throws UnsupportedEncodingException {

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForZSet().add("gaowhSet", "0618uuu", 10);
        redisTemplate.opsForValue().set("gaowh","0",15, TimeUnit.SECONDS);

        String workNbr = AXBService.bind(callId, areaCode, expiredTime, callerNbr, calledNbr, matchType,singleTransmission);
        return ResponseUtils.toSuccess(ErrorEnum.SUCCESS,workNbr);
    }


    @PostMapping("/axb/bindaxb")
    @ApiOperation("绑定电信虚拟号dto")
    public ResponseEntity bindaxb(  @RequestBody AXBDTO dto) throws UnsupportedEncodingException {
        String workNbr = AXBService.bindaxb(dto);
        return ResponseUtils.toSuccess(ErrorEnum.SUCCESS,workNbr);
    }


    @PostMapping("/axb/unbind")
    @ApiOperation("呼叫任务解绑")
    public ResponseEntity unbind(@RequestParam("callId")String callId) throws UnsupportedEncodingException {
        String message =  AXBService.unbind(callId);
        return ResponseUtils.toSuccess(ErrorEnum.SUCCESS,message);
    }
}
