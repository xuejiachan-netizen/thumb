package com.xuanjia.millionlikes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuanjia.millionlikes.model.domain.Thumb;
import com.xuanjia.millionlikes.model.dto.DoThumbRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author chenxuanjia
* @description 针对表【thumb】的数据库操作Service
* @createDate 2026-07-30 15:58:26
*/
public interface ThumbService extends IService<Thumb> {
    /**
     * 点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean doThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);


    /**
     * 取消点赞
     * @param doThumbRequest
     * @param request
     * @return {@link Boolean }
     */
    Boolean undoThumb(DoThumbRequest doThumbRequest, HttpServletRequest request);


    Boolean hasThumb(Long userId, Long BlogId);
}
