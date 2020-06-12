package com.gwh.axb.service;

import com.gwh.axb.entity.AdminLog;
import com.gwh.axb.mapper.AdminLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by gaowenhui on 2020/6/12.
 */
@Service
public class AddLogService {

    @Resource
    AdminLogMapper adminLogMapper;


    public void insert(AdminLog po) {
        adminLogMapper.insert(po);

    }

}
