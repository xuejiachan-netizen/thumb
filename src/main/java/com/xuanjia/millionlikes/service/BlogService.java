package com.xuanjia.millionlikes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuanjia.millionlikes.model.domain.Blog;
import com.xuanjia.millionlikes.model.vo.BlogVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author chenxuanjia
* @description 针对表【blog】的数据库操作Service
* @createDate 2026-07-30 15:58:27
*/
public interface BlogService extends IService<Blog> {

    BlogVo getBlogVOById(long blogId, HttpServletRequest request);

    List<BlogVo> getBlogVOList(List<Blog> blogList, HttpServletRequest request);

}
