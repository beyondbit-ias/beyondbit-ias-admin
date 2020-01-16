package com.beyondbit.ias.admin.controller;

import com.beyondbit.bua.client.PrivilegeService;
import com.beyondbit.ias.admin.entity.FRConfig;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.dao.PrivilegeMapper;
import com.beyondbit.ias.core.entity.Privilege;
import com.beyondbit.ias.core.user.PrivilegeAdapter;
import com.beyondbit.ias.core.user.entity.PrivilegeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LoginController extends BaseController {
    @Autowired
    PrivilegeMapper privilegeMapper;

    @Autowired
    PrivilegeAdapter privilegeAdapter;

    @Autowired
    private FRConfig frConfig;

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("signOutUrl",getSignOutUrl());
        return "portal/index.html";
    }

    @GetMapping("/login")
    public String login(Model model) {
        //构建法人一证通权限参数
        String loginurl = frConfig.getMainHost();
        String response_type = frConfig.getResponseType();
        String scope = frConfig.getScope();
        String client_id = frConfig.getClientID();
        String otherLoginFunctionName = "OtherLogin";
        String redirectUrl = "http://jrtj.shmh.gov.cn/account/otherlogin";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssS");//设置日期格式
        String otherUrl = String.format("%soauth/authorize?response_type=%s&scope=%s&client_id=%s&redirect_uri=%s&state=%s",
                loginurl, response_type, scope, client_id, redirectUrl, df.format(new Date()));
        model.addAttribute("frurl", otherUrl);
        model.addAttribute("systemTitle",getWebConfig("ApplicationName"));
        return "login";
    }

    @GetMapping("/test")
    public String test(Model model)
    {

        privilegeAdapter.getAllPrivileges();


        model.addAttribute("name", getCurrentUser().getName());
        model.addAttribute("account", getCurrentUser().getUserUid());
        model.addAttribute("deptname", getCurrentOrg().getOrgName());

        model.addAttribute("webconfig", getWebConfig("WebUrl1"));
        return "test";
    }
}

