package com.qingcheng.controller;

import com.qingcheng.entity.Result;
import com.qingcheng.service.service.SeckillOderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.plugin.liveconnect.SecurityContextHelper;

@RestController
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOderService seckillOderService;

    /**
     * 用户下单操作
     */
    public Result add(Long id,String time){
        //获取用户名信息
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果用户未登录，
        if(name.equals("annoymousUser")){
            return new Result(403,"用户未登录或者登录失效重新登录");
        }
        Boolean add = seckillOderService.add(id, time, name);
        if(add){
            return new Result(33333,"下单成功");
        }
        return new Result(5000,"下单失败");
    }
}
