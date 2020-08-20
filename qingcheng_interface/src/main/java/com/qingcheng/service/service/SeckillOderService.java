package com.qingcheng.service.service;

public interface SeckillOderService {

    /**
     *
     * @param id  商品id
     * @param time  时间区间
     * @param username  用户名
     * @return
     */
    Boolean add(Long id,String time,String username);
}
