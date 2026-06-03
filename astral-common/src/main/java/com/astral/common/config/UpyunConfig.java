package com.astral.common.config;

import com.astral.common.utils.UpYunUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpyunConfig {

    @Value("${upyun.bucket}")
    private String bucket;

    @Value("${upyun.operator}")
    private String operator;

    @Value("${upyun.password}")
    private String password;

    @Value("${upyun.domain}")
    private String domain;

    @Bean
    public void initUpyun(){
        UpYunUtil.setBucket(bucket);
        UpYunUtil.setOperator(operator);
        UpYunUtil.setPassword(password);
        UpYunUtil.setDomain(domain);
    }

}
