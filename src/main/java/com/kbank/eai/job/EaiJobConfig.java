package com.kbank.eai.job;

import com.kbank.eai.dto.EaiJobInfo;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class EaiJobConfig {

    @Qualifier("eaiIntfKeyList")
    private final List<Long> eaiIntfKeyList;
    private final EaiJobInfo eaiJobInfo;
    private final JobRepository jobRepository;
//    @Qualifier("dataSourceMapBean")
//    private final Map<String, DataSource> dataSourceMap;
    @Qualifier("transactionManagerMapBean")
    private final Map<String, PlatformTransactionManager> transactionManagerMap;
    @Qualifier("sqlSessionFactoryMapBean")
    private final Map<String, SqlSessionFactory> sqlSessionFactoryMap;
    @Qualifier("eaiChunkListener")
    private final ChunkListener eaiChunkListener;

    @Bean(value = "eaiJob")
    public Job eaiJob() {
        return new JobBuilder("eaiJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(eaiStep())
                .build();
    }

    @Bean(value = "eaiStep")
    @JobScope
    public Step eaiStep() {
        return new StepBuilder("eaiStep", jobRepository)
                .<HashMap, HashMap>chunk(eaiJobInfo.getChunkSize().intValue(), transactionManagerMap.get(eaiJobInfo.getDBiz()))
                .reader(eaiItemReader())
                .processor(eaiItemProcessor())
                .writer(eaiItemWriter())
                .listener(eaiChunkListener)
                .build()
                ;
    }

    @Bean(value = "eaiItemReader")
    @StepScope
    public MyBatisPagingItemReader<HashMap> eaiItemReader() {
        MyBatisPagingItemReader<HashMap> itemReader = new MyBatisPagingItemReader<>();
        itemReader.setSqlSessionFactory(sqlSessionFactoryMap.get(eaiJobInfo.getSBiz()));
        itemReader.setQueryId(eaiJobInfo.getSBiz() + "Mapper.select");
        itemReader.setPageSize(eaiJobInfo.getChunkSize().intValue());
        return itemReader;
    }

    @Bean(value = "eaiItemProcessor")
    @StepScope
    public ItemProcessor<HashMap, HashMap> eaiItemProcessor() {
        return item -> {
            eaiIntfKeyList.add((Long)item.get("ID"));
            return item;
        };
    }

    @Bean(value = "eaiItemWriter")
    @StepScope
    public MyBatisBatchItemWriter<HashMap> eaiItemWriter() {
        MyBatisBatchItemWriter<HashMap> itemWriter = new MyBatisBatchItemWriter<>();
        itemWriter.setSqlSessionTemplate(new SqlSessionTemplate(sqlSessionFactoryMap.get(eaiJobInfo.getDBiz()), ExecutorType.BATCH));
        itemWriter.setStatementId(eaiJobInfo.getSBiz() + "Mapper.insert");
        itemWriter.setAssertUpdates(false);
        return itemWriter;
    }

//    private PlatformTransactionManager checkDestinationTM() {
//        if(eaiJobInfo.getDBiz().equals("EAI")) {
//            return eaiTransactionManager;
//        } else if (eaiJobInfo.getDBiz().equals("FEP")) {
//            return fepTransactionManager;
//        } else {
//            throw new RuntimeException("Cannot find compatible transaction manager bean");
//        }
//    }
}
