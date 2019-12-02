package com.beyondbit.ias.admin.controller;

import com.alibaba.fastjson.JSON;
import com.beyondbit.ias.admin.entity.Key;
import com.beyondbit.ias.admin.entity.Webpart;
import com.beyondbit.ias.admin.service.WebpartService;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.user.entity.PrivilegeInfo;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("webpart")
public class WebpartController extends BaseController {
    @Autowired
    private WebpartService webpartService;
    @RequestMapping("/insertList")
    @ResponseBody
    public int insertList(@RequestBody List<Webpart> list)
    {
        //其实是update其中的boxid和sequence
        var result=0;
        var useruid=getCurrentUser().getUserUid();
        for (var webpart: list) {
            var entity=webpartService.get(useruid,webpart.getModularcode());
            entity.setUpdatetime(new Date());
            entity.setBoxid(webpart.getBoxid());
            entity.setSequence(webpart.getSequence());
            entity.setIsdelete(0);
            result+=webpartService.update(entity);
        }
        if(result==list.size())
            return 1;
        else
            return result;
        //return 1;
    }

    //由于权限可能在自定义保存webpart表后又有新增或删除，所以，每次获取的时候都进行一次比对，并且将多的insert进入webpart表，默认sequence为0，少的话删除webpart中的该数据，最后返回webpartlist
    @RequestMapping("/getList")
    @ResponseBody
    public List<Webpart> getList()
    {
        var useruid=getCurrentUser().getUserUid();

        var menuList=getPrivilege(useruid,"indexWebpart");
        //先webpart中该用户的webpart全部设为isdelete=1，如果menu中有权限的再将isdelete设为0
        webpartService.updateIsdelete(1,useruid);
        for( var menu : menuList){
            var code=menu.getCode();
            var webpart=webpartService.get(useruid,code);
            if(webpart!=null)
            {
                //将权限表中的url赋值给webpart的url,以防权限中的url修改
                webpart.setIsdelete(0);
                webpart.setUrl(menu.getUrl());
                webpartService.update(webpart);
            }
            else
            {
                //将权限表中的值新增给webpart表
                var entity=new Webpart();
                entity.setUrl(menu.getUrl());
                entity.setIsdelete(0);
                entity.setUpdatetime(new Date());
                entity.setUseruid(useruid);
                entity.setBoxid("0");
                entity.setModularcode(menu.getCode());
                entity.setSequence(0);
                webpartService.insert(entity);
            }

        }
        return webpartService.getList(useruid);
    }
}
