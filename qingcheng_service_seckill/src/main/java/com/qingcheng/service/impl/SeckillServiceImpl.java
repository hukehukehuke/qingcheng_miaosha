package com.qingcheng.service.impl;

import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.service.SeckillGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillServiceImpl implements SeckillGoodService {
    @Autowired
    private RedisTemplate redisTemplate;

    public SeckillGoods one(String time, Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
    }

    public List<SeckillGoods> list(String time) {
        String key = "SeckillGoods_"+time;
        return redisTemplate.boundHashOps(key).values();
    }
}
