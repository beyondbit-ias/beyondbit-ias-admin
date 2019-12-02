package com.beyondbit.ias.admin.controller;


import com.beyondbit.ias.admin.common.TrueOrFalse;
import com.beyondbit.ias.core.entity.Dictionary;
import com.beyondbit.ias.core.entity.WebConfig;
import com.beyondbit.ias.core.service.DictionaryService;
import com.beyondbit.ias.core.service.WebConfigService;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.*;

/**
 * WebConfigController class
 * 配置项Controller类
 */

@Controller
@RequestMapping("configuration")
public class WebConfigController extends BaseController {
    @Autowired
    private WebConfigService webConfigServiceimpl;
    @Autowired
    DictionaryService dictionaryServiceimpl;


    /**
     * 获取所有配置项列表
     *
     * @return
     */
    @RequestMapping("/findAllWebConfig")
    @ResponseBody
    public Layui findAllWebConfig(int page, int limit, Model model) {
        List<WebConfig> list = webConfigServiceimpl.findAllWebConfig();

        int fromIndex = 0;
        int toIndex = list.size();
        if (page > 0 && limit > 0) {
            fromIndex = (page - 1) * limit;
            toIndex = (page * limit) > list.size() ? list.size() : (page * limit);
        }



       return Layui.data(list.size(), list.subList(fromIndex, toIndex), "配置项加载成功");
    }

    /**
     * 获取所有配置项列表
     *
     * @return
     */
    @GetMapping("/findAllWebConfigList")
    @ResponseBody
    public Layui findAllWebConfigList(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                                      @RequestParam(value = "limit", defaultValue = "10") int pageSize, WebConfig webConfig) {

//        List<WebConfig> list = webConfigServiceimpl.findAllWebConfigList(webConfig);
//        int fromIndex = 0;
//
//
//        int toIndex = list.size();
//        if (page > 0 && limit > 0) {
//            fromIndex = (page - 1) * limit;
//            toIndex = (page * limit) > list.size() ? list.size() : (page * limit);
//        }
//        return Layui.data(list.size(), list.subList(fromIndex, toIndex), "配置项搜索查询成功");

        PageUtils list = webConfigServiceimpl.findAllWebConfigList(pageIndex,pageSize,webConfig);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    @PostMapping("/updateWebConfig")
    @ResponseBody
    public Layui updateWebConfig(WebConfig webConfig) {
        //判断是否数据库已经存在

        int i = webConfigServiceimpl.updateWebConfig(webConfig);
        String msgstr = "修改成功";
        if (i <= 0) msgstr = "修改失败";
        return Layui.data(i, null, msgstr);

    }


    @PostMapping("/insertWebConfig")
    @ResponseBody
    public Layui insertWebConfig(WebConfig webConfig) {
        webConfig.setSettingtime(new Date());
        //webConfig.setIssystemmust(TrueOrFalse.FALSE.getIndex());
        webConfig.setIsdelete(TrueOrFalse.FALSE.getIndex());
       // webConfig.setOptiontype("自定义配置");

        //判断是否数据库已经存在
        List<WebConfig> list = webConfigServiceimpl.getWebConfigByKeyName(webConfig);
        if (list.size() > 0) {
            return Layui.data(list.size(), null, "当前配置项名称已经存在，请重新输入");
        } else {
            int i = webConfigServiceimpl.insertWebConfig(webConfig);

            String msgstr = "新增成功";
            if (i <= 0) msgstr = "新增失败";
            return Layui.data(i, null, msgstr);
        }
        //webConfig.setParentid(8);

    }


    @PostMapping("/deleteWebConfig")
    @ResponseBody
    public Layui deleteWebConfig(int Id) {
        int i = webConfigServiceimpl.deleteWebConfig(Id);
        String msgstr = "删除成功";
        if (i <= 0) msgstr = "删除失败";
        return Layui.data(i, null, msgstr);
    }

    @PostMapping("/getWebConfigByID")
    @ResponseBody
    public Layui GetWebConfigByID(Integer id) {


        WebConfig config = webConfigServiceimpl.GetWebConfigByID(id);
        ArrayList<WebConfig> list = new ArrayList<WebConfig>();
        list.add(config);
        int i = 0;

        if (config != null)
            i = 1;
        return Layui.data(i, list, null);
    }
    //配置项新增页面
    @RequestMapping("/add")
    public String addWebConfig(Model model) {
        model.addAttribute("dic",getOptionType(0));

        return "/configuration/add.html";
    }
//配置项修改页面
    @RequestMapping("/edit")
    public String getWebConfig(Integer id, Model model) {

        WebConfig config = webConfigServiceimpl.GetWebConfigByID(id);
        model.addAttribute("dic",getOptionType(0));
        model.addAttribute("data", config);

        return "/configuration/edit.html";
    }
//    配置项列表页面
    @RequestMapping("/index")
    public String Index(Model model) {

        model.addAttribute("dic",getOptionType(1));

        return "/configuration/list.html";
    }

    public List<Dictionary> getOptionType(int type){
        //获取配置项分类字典项

        String code="configurationCategory";
        List<Dictionary> allNodes=dictionaryServiceimpl.findDictsByParentCode(code);
        //query为1，add/edit都为0
        if(type==1) {
            Dictionary dic = new Dictionary();

            dic.setName("全部");
            dic.setCode("");
            dic.setValue("");

            allNodes.add(0, dic);
        }

        return allNodes;
    }
    @RequestMapping("/getconfigValue")
    public WebConfig getconfigValue()
    {
        var config=webConfigServiceimpl.getByKeyName("proposalSubmitTitle");
        return config;
    }

}
