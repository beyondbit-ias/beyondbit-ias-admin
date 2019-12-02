package com.beyondbit.ias.admin.controller;

import com.beyondbit.bua.client.PrivilegeService;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.dao.PrivilegeMapper;
import com.beyondbit.ias.core.entity.Privilege;
import com.beyondbit.ias.core.user.PrivilegeAdapter;
import com.beyondbit.ias.core.user.entity.PrivilegeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LoginController extends BaseController {
    @Autowired
    PrivilegeMapper privilegeMapper;

    @Autowired
    PrivilegeAdapter privilegeAdapter;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("signOutUrl",getSignOutUrl());
        return "portal/index.html";
    }

    @GetMapping("/login")
    public String login() {
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

