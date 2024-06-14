package com.kbank.eai.config;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class EaiCommonConfig {

    @Bean(value = "eaiIntfKeyList")
    @JobScope
    public List<Long> eaiIntfKeyList() {
        return new ArrayList<>();
    }
}
