package com.xuanjia.millionlikes.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuanjia.millionlikes.enums.ThumbEnum;
import com.xuanjia.millionlikes.mapper.BlogMapper;
import com.xuanjia.millionlikes.model.domain.Blog;
import com.xuanjia.millionlikes.model.domain.Thumb;
import com.xuanjia.millionlikes.service.BlogService;
import com.xuanjia.millionlikes.service.ThumbService;
import com.xuanjia.millionlikes.util.ThumbUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SyncThumb2DBJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ThumbService thumbService;

    @Resource
    private BlogService blogService;

    @Resource
    private  BlogMapper blogMapper;

    @Scheduled(fixedRate = 1000)
    @Transactional(rollbackFor = Exception.class)
    public void run(){
        DateTime time = DateTime.now();
        String time1 = "";
        int second = time.second();
        Integer timesecond = (second / 10) * 10;
        if (second <10){
            int minute = time.minute() - 1;
            timesecond = 50;
            time1 = DateUtil.format(time, "hh:") + minute;
            time1 += ":" + timesecond;
        }else {
            time1 = DateUtil.format(time, "hh:MM:") + timesecond;
        }

        this.syncThumb2DBJobWithDate(time1);
        log.info("点赞业务定时任务开启！");
    }

    public void syncThumb2DBJobWithDate(String time){
        String tempThumbKey = ThumbUtil.getTempThumbKey(time);
        Map<Object, Object> syncData = redisTemplate.opsForHash().entries(tempThumbKey);
        boolean empty = CollectionUtils.isEmpty(syncData);

        if (empty) {
            log.info("本次定时任务没有需要同步的数据!");
            return;
        }

        Map<Long, Long> blogMap = new HashMap<>();

        Long thumbCount = 0L;
        //定义批量操作变量
        List<Thumb> thumbs = new ArrayList<>();
        LambdaQueryWrapper<Thumb> wrapper = new LambdaQueryWrapper<>();

        for (Object syncObject : syncData.keySet()) {
//            定义数据
            String user_blog_id = syncObject.toString();
            String[] split = user_blog_id.split(StrPool.COLON);
            Long userid = Long.valueOf(split[0]);
            Long blogid = Long.valueOf(split[1]);
            Integer thumbstate = (Integer) syncData.get(syncObject);
            Blog blog = blogService.getById(blogid);
            thumbCount = blog.getThumbCount();
            if (thumbstate == ThumbEnum.LIKE.getValue()){
                Thumb thumb = new Thumb();
                thumb.setUserId(userid);
                thumb.setBlogId(blogid);
                thumbs.add(thumb);
            }else if (thumbstate == ThumbEnum.UNLIKE.getValue()){
                wrapper.or()
                        .eq(Thumb::getUserId, userid)
                        .eq(Thumb::getUserId, userid);
            }else {
                if (thumbstate == ThumbEnum.NONE.getValue()){
                    log.warn("本次点赞没有进行操作！");
                }
                log.error("本次用户点赞出错！");

                return;
            }
            blogMap.put(blogid, blogMap.getOrDefault(blogid, 0L) + thumbstate);
        }

        //批量插入
        thumbService.saveBatch(thumbs);
        //批量删除
        thumbService.remove(wrapper);
        //批量更新点赞量
        if (!CollectionUtils.isEmpty(blogMap)){
            blogMapper.batchUpdateThumbCount(blogMap);
        }

        Thread.startVirtualThread(()->{
            redisTemplate.delete(tempThumbKey);
        });
    }
}
