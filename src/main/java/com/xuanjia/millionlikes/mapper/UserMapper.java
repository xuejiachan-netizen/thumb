package com.xuanjia.millionlikes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanjia.millionlikes.model.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author chenxuanjia
* @description 针对表【user】的数据库操作Mapper
* @createDate 2026-07-30 15:58:27
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




