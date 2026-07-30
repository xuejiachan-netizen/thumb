package com.xuanjia.millionlikes.controller;

import com.xuanjia.millionlikes.common.BaseResponse;
import com.xuanjia.millionlikes.common.ResultUtils;
import com.xuanjia.millionlikes.model.domain.Blog;
import com.xuanjia.millionlikes.model.vo.BlogVo;
import com.xuanjia.millionlikes.service.BlogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("blog")
public class BlogController {
    @Resource
    private BlogService blogService;

    @GetMapping("/get")
    public BaseResponse<BlogVo> get(long blogId, HttpServletRequest request) {
        BlogVo blogVO = blogService.getBlogVOById(blogId, request);
        return ResultUtils.success(blogVO);
    }

    @GetMapping("/list")
    public BaseResponse<List<BlogVo>> getBlogVOList(HttpServletRequest request){
        List<Blog> blogList = blogService.list();
        List<BlogVo> blogVOList = blogService.getBlogVOList(blogList, request);
        return ResultUtils.success(blogVOList);
    }
 }

