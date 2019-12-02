package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.entity.AuditLog;
import com.beyondbit.ias.core.service.AuditLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/auditlog")
public class AuditLogController extends BaseController {
    @Autowired
    com.beyondbit.ias.core.service.AuditLogService AuditLogService;

    /**
     * 审计日志列表
     * @return
     */
    @RequestMapping("/list")
    public String index(){
        return "auditlog/list.html";
    }

    /**
     * 根据条件查询全部审计日志
     * @return
     */
    @RequestMapping("/query")
    @ResponseBody
    public Map query(int page,int limit,String contentTitle) {

        AuditLog auditLog=new AuditLog();
        auditLog.setContentTitle(contentTitle);

        PageHelper.startPage(page, limit,"createTime desc");
        List<AuditLog> list = AuditLogService.query(auditLog);

        PageInfo<AuditLog> pageInfo = new PageInfo<AuditLog>(list);


        Map<String, Object> map = new HashMap<>(10);
        map.put("code", 0);
        map.put("count", pageInfo.getTotal());
        map.put("msg", "");
        map.put("data", pageInfo.getList());

        return map;
    }

    @RequestMapping(value="/insert",method = RequestMethod.POST)
    @ResponseBody
    public int insertAuditLog(@RequestBody AuditLog auditLog) {

        int result = AuditLogService.insertAuditLog(auditLog);

        return result;
    }
}
