package com.xuanjia.millionlikes.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.xuanjia.millionlikes.constant.ThumbConstant;
import com.xuanjia.millionlikes.util.ThumbUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 定时将 redis 中不存在的数据同步到数据库的补偿措施
 */
@Slf4j
@Component
public class SyncThumb2DBCompensatoryJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SyncThumb2DBJob syncThumb2DBJob;

    @Scheduled(cron = "0 0 2 * * *")
    public void run(){
        log.info("开始补偿数据");
        Set<String> thumbkeys = redisTemplate.keys(ThumbUtil.getTempThumbKey("") + "*");
        Set<String> needHandleDataSet = new HashSet<>();

        thumbkeys.stream()
                .filter(ObjectUtil::isNotNull)
                .forEach(thumbKey -> needHandleDataSet
                        .add(ThumbConstant.TEMP_THUMB_PREFIX_KEY.formatted("")));

        if (CollUtil.isEmpty(needHandleDataSet)){
            log.info("没有需要补偿的资源");
            return;
        }

        //进行数据补偿
        for (String Date : needHandleDataSet) {
            syncThumb2DBJob.syncThumb2DBJobWithDate(Date);
        }
    }
}
