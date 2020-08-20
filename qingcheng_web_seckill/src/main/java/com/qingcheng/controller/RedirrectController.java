package com.qingcheng.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/redirect")
public class RedirrectController {

    @RequestMapping(value = "/back")
    public String back(@RequestHeader(value = "refer",required = false) String refer){
        if(!StringUtils.isEmpty(refer)){
            return "redirect:"+refer;
        }
        return "seckill-index.html";
    }
}
