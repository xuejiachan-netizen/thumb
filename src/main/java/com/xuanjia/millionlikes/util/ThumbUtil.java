package com.xuanjia.millionlikes.util;

import com.xuanjia.millionlikes.constant.ThumbConstant;

import java.util.UUID;

public class ThumbUtil {
    public static String getUserThumbKey(Long userId){
        return ThumbConstant.USER_THUMB_PREFIX_KEY + userId;
    }

    /**
     * 获取临时点赞记录
     * @param time
     * @return
     */
    public static String getTempThumbKey(String time){
        return ThumbConstant.TEMP_THUMB_PREFIX_KEY.formatted(time);
    }
}
