package com.qingcheng.service.impl;

import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.pojo.seckill.SeckillOrder;
import com.qingcheng.service.service.SeckillOderService;
import com.qingcheng.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

public class SeckillOderServiceImpl implements SeckillOderService {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    /**
     * 下单实现
     * @param id  商品id
     * @param time  时间区间
     * @param username  用户名
     * @return
     */
    public Boolean add(Long id, String time, String username) {
        //查询商品信息
        SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);
        //创建订单
        if(seckillGoods != null && seckillGoods.getStockCount() > 0){
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setSeckillId(id);
            seckillOrder.setUserId(username);
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            seckillOrder.setStatus("0");
            redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);
            //扣减库存
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
            //如果商品库存为0 同步到mysql  并清理Redis缓存
            if(seckillGoods.getStockCount()<= 0){
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
                //清理Redis缓存
                redisTemplate.boundHashOps("SeckillGoods_"+time).delete();
            }else{
                redisTemplate.boundHashOps("SeckillGoods_"+time).put(id,seckillGoods);
            }
            return true;
        }
        return null;
    }
}
