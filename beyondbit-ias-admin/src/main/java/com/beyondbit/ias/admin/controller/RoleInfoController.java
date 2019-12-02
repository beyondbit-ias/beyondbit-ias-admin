package com.beyondbit.ias.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;
import com.beyondbit.ias.core.dao.RoleInfoMapper;
import com.beyondbit.ias.core.entity.DeptInfo;
import com.beyondbit.ias.core.entity.RoleInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("roleinfo")
public class RoleInfoController extends BaseController {
    @Autowired
    com.beyondbit.ias.core.service.RoleInfoService roleinfoService;


    /**
     * 列表
     * @return
     */
    @RequestMapping("list")
    public String index(){
        return "roleinfo/list.html";
    }

    /**
     * 根据条件查询全部
     * @return
     */
    @RequestMapping("query")
    @ResponseBody
    public Map query(int page,int limit,String name) {

        RoleInfo roleinfo=new RoleInfo();
        roleinfo.setRoleName(name);

        PageHelper.startPage(page, limit);
        List<RoleInfo> list = roleinfoService.query(roleinfo);

        PageInfo<RoleInfo> pageInfo = new PageInfo<RoleInfo>(list);


        Map<String, Object> map = new HashMap<>(10);
        map.put("code", 0);
        map.put("count", pageInfo.getTotal());
        map.put("msg", "");
        map.put("data", pageInfo.getList());

        return map;
    }

    @PostMapping("insert")
    @ResponseBody
    public int insertRoleInfo(@RequestBody RoleInfo roleinfo) {
        int result = roleinfoService.insertRoleInfo(roleinfo);

        return result;
    }

    @GetMapping("/queryChildren")
    @ResponseBody
    public Layui queryChildren(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                               @RequestParam(value="parentGuid",defaultValue="") String parentGuid,

                               @RequestParam(value="orderbyClause",defaultValue=" sequence asc,id asc") String orderbyClause)
    {
        PageUtils list = roleinfoService.queryRoleInfosByParentGuid(pageIndex,pageSize,parentGuid,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    /**
     * 保存
     **/
    @RequestMapping("save")
    @ResponseBody
    public Layui save(RoleInfo roleinfo) {
        int result;

        //新增需要先判断code是否存在
        if(roleinfo.getId()==null || roleinfo.getId()==0) {
            RoleInfo entity = roleinfoService.getByCode(roleinfo.getRoleCode());

            if (entity != null && entity.getRoleCode() != null && entity.getRoleCode() != "") {
                return Layui.data(-1, null, "编码已存在");
            }
        }
        roleinfo.setGuid(UUID.randomUUID().toString());
        result = roleinfoService.save(roleinfo);
        return Layui.data(result, null, "保存成功");
    }

    /**
     * 删除
     * @param roleinfo
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Layui deleteRoleInfo(RoleInfo roleinfo) {
        int i = roleinfoService.deleteRoleInfo(roleinfo);

        return Layui.data(i, null, null);
    }

    /**
     * 获取字典树
     */

    @GetMapping("getZtree")
    @ResponseBody
    public List getZtree() {

        List<ZtreeUtil> zTree = roleinfoService.getRoleInfoTree();

        return zTree;
    }

    @GetMapping("updateSequence")
    @ResponseBody
    public int updateSequence(int sequence,int id)
    {
        return  roleinfoService.updateSequence(sequence,id);
    }

    /**
     * 获得弹出新增页面
     *
     * @return
     */
    @GetMapping("add")
    public String add() {

        return "roleinfo/add.html";
    }

    @GetMapping("edit")
    public String edit(String roleguid,Model model) {
        model.addAttribute("roleguid",roleguid);
        return "roleinfo/edit.html";
    }

    @GetMapping("getById")
    @ResponseBody
    public RoleInfo getById(int id)
    {
        return  roleinfoService.getById(id);
    }

    @GetMapping("getByCode")
    @ResponseBody
    public RoleInfo getByCode(String code)
    {
        return  roleinfoService.getByCode(code);
    }

    @GetMapping("getByGuid")
    @ResponseBody
    public RoleInfo getByGuid(String guid)
    {
        return  roleinfoService.getByGuid(guid);
    }

    /**
     * 获得弹出ztree页面
     *
     * @return
     */
    @GetMapping("getCommonselector")
    public String getCommonselector() {

        return "roleinfo/commonselector.html";
    }

    @PostMapping("addroleuser")
    @ResponseBody
    public int addRoleUser(@RequestParam(value = "userids[]",required=false) String[] userids,String roleguid)
    {
        if(userids!=null)
        {
            return  roleinfoService.addRoleUser(userids,roleguid);
        }
        else
        {
            return roleinfoService.deleteRoleUserByRoleGuid(roleguid);
        }

    }

    @PostMapping("addaccess")
    @ResponseBody
    public int addAccess(@RequestParam(value = "resourceids[]",required=false) String[] resourceids,Integer resourcetype,String roleguid)
    {
        if(resourceids!=null)
        {
            return  roleinfoService.addAccess(resourceids,resourcetype,roleguid);
        }
        else
        {
            return roleinfoService.deleteAccessByRoleGuidResourceType(roleguid,resourcetype);
        }
    }



}
