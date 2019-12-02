package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.user.entity.PrivilegeInfo;
import com.beyondbit.ias.core.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@CrossOrigin
@Controller
@RequestMapping("portal")
public class PortalController extends BaseController  {


    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("signOutUrl",getSignOutUrl());
        model.addAttribute("logoImgSrc",getWebConfig("SystemLogoImgURL"));
        model.addAttribute("systemTitle",getWebConfig("ApplicationName"));
        return "portal/index.html";
    }


    @GetMapping("/default")
    public String defaultPage(){
        return "portal/default.html";
    }

    @GetMapping("/testdefault")
    public String testdefaultPage(){
        return "portal/testdefault.html";
    }

    @GetMapping("/updatepsw")
    public String updatepsw(){
        return "portal/updatepsw.html";
    }

    @GetMapping("/testpage")
    public String testpage() {

        return "portal/testpage.html";
    }
    @ResponseBody
    @RequestMapping("/getWebpart")
    public List<PrivilegeInfo> getPrivileges()
    {
        return getPrivilege(getCurrentUser().getUserUid(),"indexWebpart") ;
    }

    @ResponseBody
    @RequestMapping("/getMenu")
    public List<PrivilegeInfo> getPriviliage()
    {
//        var privilegeList=getAllPrivileges();
//        var menuList=new ArrayList<PrivilegeInfo>();
//        for(PrivilegeInfo p:privilegeList)
//        {
//            if(p.getType()=="Menu")
//                menuList.add(p);
//
//        }
//        return menuList ;
        return getCurrentMenu();
    }



    @ResponseBody
    @RequestMapping("/getUser")
    public UserInfo getUser(){
        return getCurrentUser();
    }

    @ResponseBody
    @RequestMapping("/updateMyPassword")
    public int updateMyPassword(String oldpsw,String newpsw,String newpsw2)
    {
        String userUid=getCurrentUser().getUserUid();
       // return 0;
        return updatePassword(userUid,oldpsw,newpsw);

    }


}


