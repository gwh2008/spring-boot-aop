package com.gwh.axb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Created by gaowenhui on 2020/6/12.
 */
@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

}
