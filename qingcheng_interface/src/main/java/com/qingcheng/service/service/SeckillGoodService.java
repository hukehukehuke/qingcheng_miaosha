package com.qingcheng.service.service;

import com.qingcheng.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodService {

    /**
     * 根据时间区间 和商品id获取商品信息
     * @param time
     * @param id
     * @return
     */
    SeckillGoods one(String time,Long id);

    /**
     * 根据时间区间查询商品列表
     * @param time
     * @return
     */
    List<SeckillGoods> list(String time);
}
