package com.xuanjia.millionlikes.controller;

import com.xuanjia.millionlikes.common.BaseResponse;
import com.xuanjia.millionlikes.common.ResultUtils;
import com.xuanjia.millionlikes.constant.UserConstant;
import com.xuanjia.millionlikes.model.domain.User;
import com.xuanjia.millionlikes.model.dto.DoThumbRequest;
import com.xuanjia.millionlikes.service.ThumbService;
import com.xuanjia.millionlikes.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("thumb")
public class ThumbController {

    @Resource
    private ThumbService thumbService;
    @PostMapping("like")
    public BaseResponse<Boolean> doThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request){
        Boolean success = thumbService.doThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }

    @PostMapping("unlike")
    public BaseResponse<Boolean> undoThumb(@RequestBody DoThumbRequest doThumbRequest, HttpServletRequest request){
        Boolean success = thumbService.undoThumb(doThumbRequest, request);
        return ResultUtils.success(success);
    }
}
