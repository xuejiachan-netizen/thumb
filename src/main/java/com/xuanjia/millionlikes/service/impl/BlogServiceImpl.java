package com.xuanjia.millionlikes.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Override
    public BlogVo getBlogVOById(long blogId, HttpServletRequest request) {
        Blog blog = this.getById(blogId);
        User loginUser = userService.getLoginUser(request);
        return this.getBlogVO(blog, loginUser);
    }

    @Override
    public List<BlogVo> getBlogVOList(List<Blog> blogList, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Map<Long, Boolean> blogThumbHashMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(loginUser)){
            Set<Long> blogId = blogList.stream()
                    .map(blog -> blog.getId())
                    .collect(Collectors.toSet());

            List<Thumb> listId = thumbService.lambdaQuery()
                    .eq(Thumb::getUserId, loginUser.getId())
                    .in(Thumb::getBlogId, blogId)
                    .list();
            listId.forEach(blogThumb -> blogThumbHashMap.put(blogThumb.getBlogId(), true));
        }

        return blogList.stream()
                .map(blog -> {
                    BlogVo blogVo = BeanUtil.copyProperties(blog, BlogVo.class);
                    blogVo.setHasThumb(blogThumbHashMap.get(blog.getId()));
                    return blogVo;
                }).toList();
    }

    private BlogVo getBlogVO(Blog blog, User loginUser) {
        BlogVo blogVO = new BlogVo();
        BeanUtil.copyProperties(blog, blogVO);

        if (loginUser == null) {
            return blogVO;
        }

        Thumb thumb = thumbService.lambdaQuery()
                .eq(Thumb::getUserId, loginUser.getId())
                .eq(Thumb::getBlogId, blog.getId())
                .one();
        blogVO.setHasThumb(thumb != null);

        return blogVO;
    }

}




