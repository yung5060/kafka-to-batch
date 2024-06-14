package com.kbank.eai.listener;

import com.kbank.eai.dto.EaiJobInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "eaiChunkListener")
@RequiredArgsConstructor
public class EaiChunkListener implements ChunkListener {

    @Qualifier("eaiIntfKeyList")
    private final List<Long> eaiIntfKeyList;
    @Qualifier("eaiJobInfo")
    private final EaiJobInfo eaiJobInfo;
    @Qualifier("sqlSessionFactoryMapBean")
    private final Map<String, SqlSessionFactory> sqlSessionFactoryMap;

    @Override
    public void beforeChunk(ChunkContext context) {
        ChunkListener.super.beforeChunk(context);
    }

    @Override
    @Transactional
    public void afterChunk(ChunkContext context) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactoryMap.get(eaiJobInfo.getSBiz()));
        for (Long key : eaiIntfKeyList) {
            Map<String, String> map = new HashMap<>();
            map.put("ID", key.toString());
            sqlSessionTemplate.update(eaiJobInfo.getSBiz() + "Mapper.update", map);
        }
        log.info("■ chunk context size : " + eaiIntfKeyList.size());
        eaiIntfKeyList.clear();
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        ChunkListener.super.afterChunkError(context);
    }
}
