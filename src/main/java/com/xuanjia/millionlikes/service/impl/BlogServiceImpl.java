package com.xuanjia.millionlikes.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjia.millionlikes.constant.ThumbConstant;
import com.xuanjia.millionlikes.model.domain.Blog;
import com.xuanjia.millionlikes.mapper.BlogMapper;
import com.xuanjia.millionlikes.model.domain.Thumb;
import com.xuanjia.millionlikes.model.domain.User;
import com.xuanjia.millionlikes.model.vo.BlogVo;
import com.xuanjia.millionlikes.service.BlogService;
import com.xuanjia.millionlikes.service.ThumbService;
import com.xuanjia.millionlikes.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jodd.util.CollectionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author chenxuanjia
* @description 针对表【blog】的数据库操作Service实现
* @createDate 2026-07-30 15:58:27
*/
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private ThumbService thumbService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public BlogVo getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        User loginUser = userService.getLoginUser(request);
        return this.getBlogVO(blog, loginUser);
    }

    @Override
    public List<BlogVo> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<String, Boolean> blogThumbHashMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(loginUser)){
            List<Object> blogIdList = blogList.stream().map(blog->blog.getId().toString()).collect(Collectors.toList());
            List<Object> redisObject = redisTemplate.opsForHash().multiGet(ThumbConstant.USER_THUMB_PREFIX_KEY + loginUser.getId().toString(), blogIdList);
            for (int i = 0; i < redisObject.size(); i++) {
                if (redisObject.get(i)== null){
                    continue;
                }
                blogThumbHashMap.put(blogIdList.get(i).toString(),true);
            }
        }

        return blogList.stream()
                .map(blog -> {
                    BlogVo blogVo = BeanUtil.copyProperties(blog, BlogVo.class);
                    blogVo.setHasThumb(blogThumbHashMap.get(blog.getId().toString()));
                    return blogVo;
                }).toList();
    }

    private BlogVo getBlogVO(Blog blog, User loginUser) {
        BlogVo blogVO = new BlogVo();
        BeanUtil.copyProperties(blog, blogVO);

        if (loginUser == null) {
            return blogVO;
        }
        Boolean hasThumb = thumbService.hasThumb(loginUser.getId(), blog.getId());
        blogVO.setHasThumb(hasThumb);
        return blogVO;
    }

}




