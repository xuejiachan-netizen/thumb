package com.xuanjia.millionlikes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuanjia.millionlikes.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author chenxuanjia
* @description 针对表【user】的数据库操作Service
* @createDate 2026-07-30 15:58:27
*/
public interface UserService extends IService<User> {

    User getLoginUser(HttpServletRequest request);
}
