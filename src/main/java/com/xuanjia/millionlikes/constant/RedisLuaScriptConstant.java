package com.xuanjia.millionlikes.constant;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisLuaScriptConstant {

    /** 用户点赞脚本*/
    public static final RedisScript<Long> THUMB_SCRIPT = new DefaultRedisScript<>("""
            local tempthumbkey = KEY[1]  --设置临时点赞统计key
            local userthumbkey = KEY[2]   --设置用户点赞状态key
            local userid = ARGV[1]   
            local blogid = ARGV[2]
            
            -- 第一步：
            -- 检查用户是否点赞，避免重复操作
            -- 如果同时存在 blogid 和 userkey 说明用户已经进行了点赞
            if (redis.call("HEXISTS",userthumbkey,blogid) == 1) then
                return -1
            end
            
            -- 第二步：
            -- 获取旧的点赞值，如果点赞不存在的话就默认为0
            local hashkey = userid .. ':' .. blogid
            local oldNumber = tonumber(redis.call("HGET",tempthumbkey,hashkey) or 0);
            local newNumber = oldNumber + 1;
            
            
            -- 第三步:
            -- 写入 redis
            redis.call("HSET", tempthumbkey, hashkey, newNumber)
            redis.call("HSET", userthumbkey , blogid, 1)
            
            return 1
            """, Long.class);


    /** 用户取消点赞脚本*/
    public static final RedisScript<Long> UNTHUMB_SCRIPT = new DefaultRedisScript<>("""
            local tempthumbkey = KEY[1]
            local userthumbkey = KEY[2]
            local userid = ARGV[1]
            local blogid = ARGV[2]
              
            -- 第一步：判断用户是否已经点赞，如果未点赞直接结束
            if(redis.call("HEXISTS",userthumbkey,blogid) ~= 1) then
                return -1
            end
            
            -- 第二步 获取对应的点赞次数
            local hashkey = userid .. ':' .. blogid
            local oldnumber = tonumber(redis.call("HGET", tempthumbkey,hashkey) or 0 )
            local newnumber = oldnumber -1
            
            if newnumber <= 0 then
            newnumber = 0
            end
            
            --第三步 取消点赞
            redis.call("HSET", tempthumbkey, hashkey, newnumber)
            redis.call("HDEL", userthumbkey, blogid)
            return 1
            """, Long.class);

}
