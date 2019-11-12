package com.yq.jforgame.controller.rank;


import com.alibaba.fastjson.JSONObject;
import com.yq.jforgame.common.Result;
import com.yq.jforgame.controller.rank.service.RankService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
//允许跨域请求
@CrossOrigin("*")
//只允许json格式的请求数据  redis启动失败,
//我这边是定义post 请求     method = RequestMethod.POST   produces = "application/json;charset=UTF-8" 是以json的格式
@RequestMapping(value = "/rank", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
public class RankController {
    @Resource
    private RankService rankService;
//@RequestBody  在游戏中不可能是这样的   www.xxxx.com/aa/bb?username=?&password=?  很少 但是尽量统一
    @RequestMapping("/rank")
    public Result rank(@RequestBody  JSONObject jsonObject){
        //打个断点 跟踪
        return rankService.entrance(jsonObject);
    }
}
