package com.qingcheng.service.service;

import com.qingcheng.util.SeckillStatus;

public interface SeckillOderService {

    /**
     * 修改订单状态
     * @param username
     * @return
     */
    void updateStatus(String outtradeno,String username,String transactionId);

    SeckillStatus queryStatus(String username);

    /**
     *
     * @param id  商品id
     * @param time  时间区间
     * @param username  用户名
     * @return
     */
    Boolean add(Long id,String time,String username);
}
