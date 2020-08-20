package com.qingcheng.controller;

import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.service.SeckillGoodService;
import com.qingcheng.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/seckill/goods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodService seckillGoodService;
    @GetMapping(value = "/seckill/goods/one")
    public SeckillGoods one(@RequestParam String time, Long id){
        return seckillGoodService.one(time,id);
    }

    @RequestMapping(value = "/menus")
    public List<Date> loadMenus(){
        return DateUtil.getDateMenus();
    }
}
