package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.entity.MessageLog;
import com.beyondbit.ias.core.service.MessageLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/message")
public class MessageLogController extends BaseController {
    @Autowired
    com.beyondbit.ias.core.service.MessageLogService MessageLogService;

    /**
     * 消息日志列表
     * @return
     */
    @RequestMapping("/list")
    public String index(){
        return "message/list.html";
    }

    /**
     * 根据条件查询全部消息日志
     * @return
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map query(int page,int limit,String sendContent) {

        MessageLog messageLog=new MessageLog();
        messageLog.setSendContent(sendContent);

        PageHelper.startPage(page, limit);
        List<MessageLog> list = MessageLogService.query(messageLog);

        PageInfo<MessageLog> pageInfo = new PageInfo<MessageLog>(list);


        Map<String, Object> map = new HashMap<>(10);
        map.put("code", 0);
        map.put("count", pageInfo.getTotal());
        map.put("msg", "");
        map.put("data", pageInfo.getList());

        return map;
    }

    @PostMapping("/insert")
    @ResponseBody
    public int insertMessageLog(@RequestBody MessageLog messageLog) {
        int result = MessageLogService.insertMessageLog(messageLog);

        return result;
    }
}
