package com.kbank.eai.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@JobScope
@Getter @Setter
public class EaiJobInfo {

    @Value("#{jobParameters[sBiz]}")
    private String sBiz;

    @Value("#{jobParameters[dBiz]}")
    private String dBiz;

    @Value("#{jobParameters[isKafka]}")
    private String isKafka;

    @Value("#{jobParameters[chunkSize]}")
    private Long chunkSize;

    @Value("#{jobParameters[guid]}")
    private String guid;
}
