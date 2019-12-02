package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.entity.SystemLog;
import com.beyondbit.ias.core.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/systemlog")
public class SystemLogController extends BaseController {

    @Autowired
    com.beyondbit.ias.core.service.SystemLogService SystemLogService;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 消息日志列表
     * @return
     */
    @RequestMapping("/list")
    public String index(){
        return "systemlog/list.html";
    }

    /**
     * 根据条件查询全部消息日志
     * @return
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map query(int page,int limit,String method) {

        SystemLog systemLog=new SystemLog();
        systemLog.setMethod(method);

        PageHelper.startPage(page, limit,"gmtCreate desc");
        List<SystemLog> list = SystemLogService.query(systemLog);

        PageInfo<SystemLog> pageInfo = new PageInfo<SystemLog>(list);


        Map<String, Object> map = new HashMap<>(10);
        map.put("code", 0);
        map.put("count", pageInfo.getTotal());
        map.put("msg", "");
        map.put("data", pageInfo.getList());

        return map;
    }

    @PostMapping("/insert")
    @ResponseBody
    public int insertSystemLog(@RequestBody SystemLog systemLog) {
        int result = SystemLogService.insertSystemLog(systemLog);

        return result;
    }

    /**
     * 将redis中的系统日志信息保存到数据库中
     * @return
     */
    @RequestMapping("/redistodatabase")
    @ResponseBody
    public String redisToDatabase(){
        List<Object> list=redisUtil.lGet("SystemLog",0,-1);
        for(int i = list.size()-1 ; i >= 0 ; i--) {
            SystemLogService.insertSystemLog((SystemLog)list.get(i));
            redisUtil.lRemove("SystemLog",1,list.get(i));
        }
        return "success";
    }
}
