package com.qingcheng.task;

import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.pojo.seckill.SeckillOrder;
import com.qingcheng.util.IdWorker;
import com.qingcheng.util.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class MultiThreadingCreateOrder {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Async
    public void createOrder() {
        try {
            TimeUnit.SECONDS.sleep(2000);


            SeckillStatus seckillStatus =(SeckillStatus) redisTemplate.boundListOps("SeckillOderQueue").rightPop();
            String username = seckillStatus.getUsername();
            Long id = seckillStatus.getGoodsId();
            String  time = seckillStatus.getTime();

            //获取队列中的商品id
            Object ids = redisTemplate.boundListOps("SeckillGoods_" + id).rightPop();
            //售空
            if(ids == null){
                //清理排队信息
                clearQueue(seckillStatus);
                return;
            }
            //查询商品信息
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_" + time).get(id);
            //创建订单
            if (seckillGoods != null && seckillGoods.getStockCount() > 0) {
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(idWorker.nextId());
                seckillOrder.setCreateTime(new Date());
                seckillOrder.setSeckillId(id);
                seckillOrder.setUserId(username);
                seckillOrder.setSellerId(seckillGoods.getSellerId());
                seckillOrder.setStatus("0");
                redisTemplate.boundHashOps("SeckillOrder").put(username, seckillOrder);
                //扣减库存
                Long seckillGoodsCount = redisTemplate.boundHashOps("SeckillGoodsCount").increment(seckillGoods.getId(), -1);
               // seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                seckillGoods.setStockCount(seckillGoodsCount.intValue());
                //如果商品库存为0 同步到mysql  并清理Redis缓存
                if (seckillGoods.getStockCount() <= 0) {
                    seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
                    //清理Redis缓存
                    redisTemplate.boundHashOps("SeckillGoods_" + time).delete();
                } else {
                    redisTemplate.boundHashOps("SeckillGoods_" + time).put(id, seckillGoods);
                }
                //变更抢单状态
                seckillStatus.setOrderId(seckillOrder.getId());
                seckillStatus.setMoney(seckillOrder.getMoney().floatValue());
                seckillStatus.setStatus(2);
                redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStatus);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //清理排队信息
    private void clearQueue(SeckillStatus seckillStatus) {
        //清理重复排队标识
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStatus.getUsername());
        //清理排队信息
        redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStatus.getUsername());
    }
}
