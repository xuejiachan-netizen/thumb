package com.xuanjia.millionlikes.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjia.millionlikes.exception.BusinessException;
import com.xuanjia.millionlikes.exception.ErrorCode;
import com.xuanjia.millionlikes.model.domain.Blog;
import com.xuanjia.millionlikes.model.domain.Thumb;
import com.xuanjia.millionlikes.mapper.ThumbMapper;
import com.xuanjia.millionlikes.model.domain.User;
import com.xuanjia.millionlikes.model.dto.DoThumbRequest;
import com.xuanjia.millionlikes.service.BlogService;
import com.xuanjia.millionlikes.service.ThumbService;
import com.xuanjia.millionlikes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

/**
* @author chenxuanjia
* @description 针对表【thumb】的数据库操作Service实现
* @createDate 2026-07-30 15:58:26
*/
@Service
@RequiredArgsConstructor
public class ThumbServiceImpl extends ServiceImpl<ThumbMapper, Thumb> implements ThumbService {

    private final TransactionTemplate transactionTemplate;

    private final BlogService blogService;

    private final UserService userService;

    private final RedissonClient redissonClient;

    @Override
    public Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        synchronized (loginUser.getId().toString().intern()){
            return transactionTemplate.execute(
                   status -> {
                       boolean exists = this.lambdaQuery()
                               .eq(Thumb::getUserId, loginUser.getId())
                               .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                               .exists();
                       if (exists){
                           throw new RuntimeException("用户已点赞!");
                       }

                       boolean update = blogService.lambdaUpdate()
                               .eq(Blog::getId, doThumbRequest.getBlogId())
                               .setSql("thumb_count = thumb_count + 1")
                               .update();
                        Thumb thumb = new Thumb();
                        thumb.setBlogId(doThumbRequest.getBlogId());
                        thumb.setUserId(loginUser.getId());
                       boolean save = this.save(thumb);
                       return save && update;
                   });
        }
    }

    @Override
    public Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        synchronized (loginUser.getId().toString().intern()){
            return transactionTemplate.execute(
                    retustatus -> {
                        Thumb thumb = this.lambdaQuery()
                                .eq(Thumb::getUserId, loginUser.getId())
                                .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                                .one();
                        if (thumb == null){
                        throw new RuntimeException("用户未点赞!");
                    }

                    boolean update = blogService.lambdaUpdate()
                            .eq(Blog::getId, doThumbRequest.getBlogId())
                            .setSql("thumb_count = thumb_count - 1")
                            .update();
                    boolean save = this.removeById(thumb.getId());
                    return save && update;
                });
        }

    }


    @Override
    public Boolean doThumbWithdisButritedLock(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        String redisson_key = "thumb:lock:" + loginUser.getId() + ":" + doThumbRequest.getBlogId();

        RLock lock = redissonClient.getLock(redisson_key);


        try {
            if (lock.tryLock(3,30,TimeUnit.SECONDS)){
                boolean exists = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                        .exists();
                if (exists){
                    throw new RuntimeException("用户已点赞!");
                }

                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, doThumbRequest.getBlogId())
                        .setSql("thumb_count = thumb_count + 1")
                        .update();
                Thumb thumb = new Thumb();
                thumb.setBlogId(doThumbRequest.getBlogId());
                thumb.setUserId(loginUser.getId());
                boolean save = this.save(thumb);
                return save && update;
            }else{
                log.error("分布式锁上锁失败！");
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("获取锁失败",e);
        }

    }

    @Override
    public Boolean undoThumbWithDisbutritedLock(DoThumbRequest doThumbRequest, HttpServletRequest request) {
        if (doThumbRequest == null || request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        String redisson_key = "thumb:lock:" + loginUser.getId() + ":" + doThumbRequest.getBlogId();
        RLock lock = redissonClient.getLock(redisson_key);

        try {
            if (lock.tryLock(3,30, TimeUnit.SECONDS)){
                Thumb thumb = this.lambdaQuery()
                        .eq(Thumb::getUserId, loginUser.getId())
                        .eq(Thumb::getBlogId, doThumbRequest.getBlogId())
                        .one();
                if (thumb == null){
                    throw new RuntimeException("用户未点赞!");
                }

                boolean update = blogService.lambdaUpdate()
                        .eq(Blog::getId, doThumbRequest.getBlogId())
                        .setSql("thumb_count = thumb_count - 1")
                        .update();
                boolean save = this.removeById(thumb.getId());
                return save && update;
            }else {
                return false;
            }
        }catch (Exception e){
            throw new RuntimeException("获取分布式锁失败!", e);
        }

    }
}




