package com.qingcheng.controller;

import com.qingcheng.entity.Result;
import com.qingcheng.service.service.SeckillOderService;
import com.qingcheng.util.SeckillStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.plugin.liveconnect.SecurityContextHelper;

@RestController
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOderService seckillOderService;

    @GetMapping(value = "/queryStatus")
    public Result queryStatus(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if(name.equals("annomous")){
            return new Result(430,"用户未登录");
        }
        SeckillStatus seckillStatus = seckillOderService.queryStatus(name);
        return new Result(200,seckillStatus.getStatus().toString());
    }

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
