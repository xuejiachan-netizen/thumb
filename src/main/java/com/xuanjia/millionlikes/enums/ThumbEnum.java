package com.xuanjia.millionlikes.enums;

import lombok.Getter;

@Getter
public enum ThumbEnum {

    //点赞
    LIKE(1),

    //取消点赞
    UNLIKE(-1),

    //不作处理
    NONE(0);

    private final int value;

    ThumbEnum(int value){
        this.value = value;
    }
}
