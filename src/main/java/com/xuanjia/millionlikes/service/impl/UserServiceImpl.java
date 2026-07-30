package com.xuanjia.millionlikes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuanjia.millionlikes.constant.UserConstant;
import com.xuanjia.millionlikes.model.domain.User;
import com.xuanjia.millionlikes.mapper.UserMapper;
import com.xuanjia.millionlikes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/**
* @author chenxuanjia
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-07-30 15:58:27
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getLoginUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(UserConstant.LOGIN_USER);
    }

}




