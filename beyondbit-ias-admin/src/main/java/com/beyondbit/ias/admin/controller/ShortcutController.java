package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.admin.entity.Shortcut;
import com.beyondbit.ias.admin.service.ShortcutService;
import com.beyondbit.ias.core.base.BaseController;
import lombok.var;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("shortcut")
public class ShortcutController extends BaseController {
    @Autowired
    private ShortcutService shortcutService;
    @RequestMapping("/insert")
    @ResponseBody
    public String insert(Shortcut entity)
    {
        //判断是否已经存在快捷方式
        var useraccount=getCurrentUser().getUserUid();
        if(useraccount!=null) {
            var isExist = shortcutService.get(useraccount, entity.getModularcode());
            if (isExist == null) {
                entity.setUseraccount(useraccount);
                entity.setCreatetime(new Date());
                var insertResult = shortcutService.insert(entity);
                if (insertResult > 0)
                    return "success";//添加快捷方式成功
                else
                    return "failed";//添加快捷方式失败
            } else
                return "exist";//该快捷方式已存在;
        }
        else
            return "loginfailed";//用户信息获取失败。需要重新登陆。
    }
    @RequestMapping("/delete")
    @ResponseBody
    public String delete(String modularcode)
    {
        var useraccount=getCurrentUser().getUserUid();
        if(useraccount!=null) {
            var result=shortcutService.delete(useraccount,modularcode);
            if(result>0)
                return "success";//删除快捷方式成功
            else
                return "failed";//删除快捷方式失败

        }
        else
            return "loginfailed";//用户信息获取失败。需要重新登陆。
    }

    @RequestMapping("/getList")
    @ResponseBody
    public List<Shortcut> getList(){
        return shortcutService.getList(getCurrentUser().getUserUid());
    }
}
