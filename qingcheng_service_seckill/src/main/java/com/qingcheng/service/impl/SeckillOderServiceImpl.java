package com.qingcheng.service.impl;

import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.dao.SeckillOrderMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.pojo.seckill.SeckillOrder;
import com.qingcheng.service.service.SeckillOderService;
import com.qingcheng.task.MultiThreadingCreateOrder;
import com.qingcheng.util.IdWorker;
import com.qingcheng.util.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

public class SeckillOderServiceImpl implements SeckillOderService {
    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    /**
     * 修改订单状态
     *
     * @param outtradeno
     * @param username
     * @param transactionId
     */
    public void updateStatus(String outtradeno, String username, String transactionId) {
        //更具用户名查询订单信息
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("SeckillOrder").get(username);
        if(seckillOrder != null){
            seckillOrder.setStatus("1");
            seckillOrder.setPayTime(new Date());
            seckillOrderMapper.insertSelective(seckillOrder);
            //清理重复排队标识
            redisTemplate.boundHashOps("UserQueueCount").delete(username);
            //清理排队信息
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
        }
        //持久化到Mysql中

        //清理排队信息
    }

    public SeckillStatus queryStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps("userQueueStatus").get(username);
    }

    /**
     * 下单实现
     *
     * @param id       商品id
     * @param time     时间区间
     * @param username 用户名
     * @return
     */
    public Boolean add(Long id, String time, String username) {
        //自增特性
        //incr(key,value) 让指定key自增，这个自增是单线成操作的
        //利用自增 如果用户多次提交或者多次排队 自增数值就大于1
        Long userCountQueue = redisTemplate.boundHashOps("UserCountQueue").increment(username, 1);
        if (userCountQueue > 1) {
            throw new RuntimeException("100");
        }
        //减少无效排队
        Long size = redisTemplate.boundHashOps("SeckillGoodsCountList" + id).size();
        if (size <= 0) {
            throw new RuntimeException("101");
        }
        //c创建订单所需要的排队信息
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, id, time);
        //将排队信息存入队列
        redisTemplate.boundListOps("SeckillOderQueue").leftPush(seckillStatus);

        //存储排队信息
        redisTemplate.boundHashOps("userQueueStatus").put(username, seckillStatus);
        multiThreadingCreateOrder.createOrder();
        ;
        return true;
    }
}
