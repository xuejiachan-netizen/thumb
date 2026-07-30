package com.xuanjia.millionlikes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanjia.millionlikes.model.domain.Blog;
import org.apache.ibatis.annotations.Mapper;

/**
* @author chenxuanjia
* @description 针对表【blog】的数据库操作Mapper
* @createDate 2026-07-30 15:58:27
* @Entity generator.domain.Blog
*/
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

}




