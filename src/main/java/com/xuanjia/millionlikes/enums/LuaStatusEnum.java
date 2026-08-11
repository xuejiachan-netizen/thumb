package com.xuanjia.millionlikes.enums;

import lombok.Getter;

@Getter
public enum LuaStatusEnum {

    //lua 执行成功
    SUCCESS(1L),
    //lua 执行失败
    FAIL(-1L);

    private final long value;
    LuaStatusEnum(long value) {
        this.value = value;
    }
}
