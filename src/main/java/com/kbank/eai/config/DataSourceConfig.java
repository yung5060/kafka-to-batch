package com.kbank.eai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfig {

    // 타 클래스에서 편하게 데이터소스 찾을 수 있게 해주는 데이터소스 해시맵 빈
    @Bean(value = "dataSourceMapBean")
    @DependsOn({"eaiDataSource", "fepDataSource"})
    public Map<String, DataSource> dataSourceMapBean() {
        Map<String, DataSource> map = new HashMap<>();
        map.put("eai", eaiDataSource());
        map.put("fep", fepDataSource());
        return map;
    }

    // 타 클래스에서 편하게 트랜잭션 매니저 찾을 수 있게 해주는 트랜잭션 매니저 해시맵 빈
    @Bean(value = "transactionManagerMapBean")
    public Map<String, PlatformTransactionManager> transactionManagerMapBean(
            @Qualifier("dataSourceMapBean") final Map<String, DataSource> dataSourceMap
    ) {
        Map<String, PlatformTransactionManager> map = new HashMap<>();
        dataSourceMap.forEach((key, value) -> {
            map.put(key, new DataSourceTransactionManager(value));
        });
        return map;
    }

    // application.properties 에서 eai 데이베이스 정보 읽어옴
    @Bean(value = "eaiDataSourceProperties")
    @ConfigurationProperties("datasources.eai")
    public DataSourceProperties eaiDataSourceProperties() {
        return new DataSourceProperties();
    }

    // eai 데이터소스 빈
    @Bean(value = "eaiDataSource")
    public DataSource eaiDataSource() {
        return eaiDataSourceProperties().initializeDataSourceBuilder().build();
    }

    // eai DB 트랜잭션 매니저
//    @Bean(value = "eaiTransactionManager")
//    public DataSourceTransactionManager eaiTransactionManager(@Qualifier("eaiDataSource") final DataSource eaiDataSource) {
//        return new DataSourceTransactionManager(eaiDataSource);
//    }

    // application.properties 에서 fep 데이베이스 정보 읽어옴
    @Bean(value = "fepDataSourceProperties")
    @ConfigurationProperties("datasources.fep")
    public DataSourceProperties fepDataSourceProperties() {
        return new DataSourceProperties();
    }

    // fep 데이터소스 빈
    @Bean(value = "fepDataSource")
    public DataSource fepDataSource() {
        return fepDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(value = "fepDataBaseInitializer")
    public DataSourceInitializer fepDataBaseInitializer() {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("customer_schema.sql"));

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(fepDataSource());
        initializer.setDatabasePopulator(databasePopulator);

        return initializer;
    }

    // fep DB 트랜잭션 매니저
//    @Bean(value = "fepTransactionManager")
//    public DataSourceTransactionManager fepTransactionManager(@Qualifier("fepDataSource") final DataSource fepDataSource) {
//        return new DataSourceTransactionManager(fepDataSource);
//    }

}
