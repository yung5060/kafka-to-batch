package com.kbank.eai.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SqlSessionConfig {

    @Qualifier("dataSourceMapBean")
    private final Map<String, DataSource> dataSourceMap;

    @Bean(value = "sqlSessionFactoryMapBean")
//    @DependsOn({"eaiSqlSessionFactory", "fepSqlSessionFactory"})
    public Map<String, SqlSessionFactory> sqlSessionFactoryMap() {
        Map<String, SqlSessionFactory> map = new HashMap<>();
        dataSourceMap.forEach((key, value) -> {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(value);
            sqlSessionFactoryBean.addMapperLocations(new ClassPathResource("mappers/eaiMapper.xml"));
            try {
                map.put(key, sqlSessionFactoryBean.getObject());
            } catch (Exception e) {
                throw new RuntimeException("failed to load mapper file", e);
            }
        });
        return map;
    }
}
