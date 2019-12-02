package com.beyondbit.ias.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.beyondbit.ias.admin.common.IconInfo;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;
import com.beyondbit.ias.core.entity.Dictionary;
import com.beyondbit.ias.core.entity.Privilege;
import com.beyondbit.ias.core.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("privilege")
public class PrivilegeController extends BaseController {
    @Autowired
    com.beyondbit.ias.core.service.PrivilegeService privilegeService;

    /**
     * 权限列表
     * @return
     */
    @RequestMapping("list")
    public String index(){
        return "privilege/list.html";
    }

    /**
     * 根据条件查询全部消息日志
     * @return
     */
    @RequestMapping("query")
    @ResponseBody
    public Map query(int page,int limit,String name) {

        Privilege privilege=new Privilege();
        privilege.setName(name);

        PageHelper.startPage(page, limit);
        List<Privilege> list = privilegeService.query(privilege);

        PageInfo<Privilege> pageInfo = new PageInfo<Privilege>(list);


        Map<String, Object> map = new HashMap<>(10);
        map.put("code", 0);
        map.put("count", pageInfo.getTotal());
        map.put("msg", "");
        map.put("data", pageInfo.getList());

        return map;
    }

    @PostMapping("insert")
    @ResponseBody
    public int insertPrivilege(@RequestBody Privilege privilege) {
        int result = privilegeService.insertPrivilege(privilege);

        return result;
    }

    @GetMapping("/queryChildren")
    @ResponseBody
    public Layui queryChildren(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                               @RequestParam(value="parentGuid",defaultValue="") String parentGuid,
                               @RequestParam(value="orderbyClause",defaultValue=" sequence asc,id asc") String orderbyClause)
    {
        PageUtils list = privilegeService.queryPrivilegesByParentGuid(pageIndex,pageSize,parentGuid,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    /**
     * 保存
     **/
    @RequestMapping("save")
    @ResponseBody
    public Layui save(Privilege privilege) {
        int result;
        //新增需要先判断code是否存在
        if(privilege.getId()==null || privilege.getId()==0) {
            Privilege entity = privilegeService.getByCode(privilege.getCode());
            if (entity != null && entity.getCode() != null && entity.getCode() != "") {
                return Layui.data(-1, null, "编码已存在");
            }
        }
        privilege.setGuid(UUID.randomUUID().toString());
        result = privilegeService.save(privilege);
        return Layui.data(result, null, "保存成功");
    }

    /**
     * 删除
     * @param privilege
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Layui deletePrivilege(Privilege privilege) {

        int i = privilegeService.deletePrivilege(privilege);

        return Layui.data(i, null, null);
    }

    /**
     * 获取树
     */
    @GetMapping("getZtree")
    @ResponseBody
    public List getZtree() {

        List<ZtreeUtil> zTree = privilegeService.getPrivilegeTree();

        return zTree;
    }

    /**
     * 根据角色id获取 显示已选
     * @param roleguid
     * @return
     */
    @GetMapping("getCheckedZtree")
    @ResponseBody
    public List getCheckedZtree(String roleguid) {

        List<ZtreeUtil> zTree = privilegeService.getCheckedZtree(roleguid);

        return zTree;
    }

    @GetMapping("updateSequence")
    @ResponseBody
    public int updateSequence(int sequence,int id)
    {
        return  privilegeService.updateSequence(sequence,id);
    }

    /**
     * 获得弹出新增页面
     *
     * @return
     */
    @GetMapping("add")
    public String add() {

        return "privilege/add.html";
    }

    @GetMapping("getById")
    @ResponseBody
    public Privilege getById(int id)
    {
        return  privilegeService.getById(id);
    }

    @GetMapping("getByGuid")
    @ResponseBody
    public Privilege getByGuid(String guid)
    {
        return  privilegeService.getByGuid(guid);
    }

    /**
     * 获得弹出ztree页面
     *
     * @return
     */
    @GetMapping("getCommonselector")
    public String getCommonselector() {

        return "privilege/commonselector.html";
    }

    @GetMapping("authorize")
    public String authorize() {
        return "privilege/authorize.html";
    }

    /**
     * 获得弹出ztree页面
     *
     * @return
     */
    @GetMapping("getIconselector")
    public String getIconselector(Model model) {

        //加载系统字体图标
        StringBuffer sysstringBuffer = new StringBuffer();
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("static/UCMFontText/iconset.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sysstringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("sysIcon", JSONObject.parseArray(sysstringBuffer.toString(), IconInfo.class));

        //加载自定义字体图标
        StringBuffer diystringBuffer = new StringBuffer();
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream("static/DIYFontText/iconset.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                diystringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("diyIcon", JSONObject.parseArray(diystringBuffer.toString(), IconInfo.class));



        return "privilege/iconselector.html";
    }


}
