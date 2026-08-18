package com.xuanjia.millionlikes.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjia.millionlikes.constant.RedisLuaScriptConstant;
import com.xuanjia.millionlikes.enums.LuaStatusEnum;
import com.xuanjia.millionlikes.mapper.ThumbMapper;
import com.xuanjia.millionlikes.model.domain.Thumb;
import com.xuanjia.millionlikes.model.domain.User;
import com.xuanjia.millionlikes.model.dto.DoThumbRequest;
import com.xuanjia.millionlikes.service.ThumbService;
import com.xuanjia.millionlikes.service.UserService;
import com.xuanjia.millionlikes.util.ThumbUtil;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
* @author chenxuanjia
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2026-07-30 15:58:26
*/
@Service("ThumbService")
@RequiredArgsConstructor
@Primary
public class ThumbWithRedisServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserService userService;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Long blogId = doThumbRequest.getBlogId();
        User loginUser = userService.getLoginUser(request);
        Long user = loginUser.getId();
        if (blogId == null || user == null) {
            log.error("用户信息不存在!");
            return false;
        }

        String timeSplit = this.getTimeSplit();
        String userThumbKey = ThumbUtil.getUserThumbKey(user);
        String tempThumbKey = ThumbUtil.getTempThumbKey(timeSplit);

        Long result = redisTemplate.execute(
                RedisLuaScriptConstant.THUMB_SCRIPT,
                Arrays.asList(tempThumbKey, userThumbKey),
                user,
                blogId
        );

        if (result == LuaStatusEnum.FAIL.getValue()) {
            log.error("点赞失败！用户已经点赞！");
            return false;
        }

        return result == LuaStatusEnum.SUCCESS.getValue();
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        Long blogId = doThumbRequest.getBlogId();
        User loginUser = userService.getLoginUser(request);
        Long user = loginUser.getId();
        if (blogId == null || user == null) {
            log.error("用户信息不存在!");
            return false;
        }
        String timeSplit = this.getTimeSplit();
        String userThumbKey = ThumbUtil.getUserThumbKey(user);
        String tempThumbKey = ThumbUtil.getTempThumbKey(timeSplit);

        Long result = redisTemplate.execute(
                RedisLuaScriptConstant.UNTHUMB_SCRIPT,
                Arrays.asList(tempThumbKey, userThumbKey),
                user,
                blogId
        );
        if (result == LuaStatusEnum.FAIL.getValue()) {
            log.error("点赞失败！用户未点赞！");
            return false;
        }

        return result == LuaStatusEnum.SUCCESS.getValue();
    }

    @Override
    public Boolean hasThumb(Long userId, Long blogId) {
        return redisTemplate.opsForHash().hasKey(ThumbUtil.getUserThumbKey(userId), blogId.toString());
    }

    private String getTimeSplit() {
        String second = String.format("%02d",(DateTime.now().second() / 10) * 10);
        return DateUtil.format(DateTime.now(), "HH:mm:") + second;
    }
}




