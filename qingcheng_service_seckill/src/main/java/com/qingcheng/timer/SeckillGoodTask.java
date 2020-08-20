package com.qingcheng.timer;


import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillGoodTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Scheduled(cron = "0/30 * * * * ?")
    public void loadGood(){
        //1、查询所有时间区间
        List<Date> dateMenus = DateUtil.getDateMenus();
        //2、循环时间区间，查询每个时间区间的秒杀商品
        for(Date startTime : dateMenus){
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //2.1商品必须审核通过
            criteria.andEqualTo("status",1);
            //2.2库存大于0
            criteria.andGreaterThan("stockCount",0);
            //2.3秒杀开始时间大于等于当前循环的时间的开始时间
            criteria.andGreaterThanOrEqualTo("startTime",startTime);
            //2.4秒杀结束时间<当前循环的时间的开始时间+2小时
            criteria.andLessThan("endTime",DateUtil.addDateHour(startTime,2));
            //过滤查询条件
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + DateUtil.date2Str(startTime)).keys();
            if(CollectionUtils.isNotEmpty(keys)){
                //select * from where not in();
                criteria.andNotIn("id",keys);
            }
            //2.5执行查询
            List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
            for(SeckillGoods seckillGoods : seckillGoodsList){
                //3、将秒杀商品存入Redis
                redisTemplate.boundHashOps("SeckillGoods_"+DateUtil.date2Str(startTime)).put(seckillGoods.getId(),seckillGoods);
            }
        }

    }
}
