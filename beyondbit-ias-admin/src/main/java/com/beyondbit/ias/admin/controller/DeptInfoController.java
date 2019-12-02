package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;
import com.beyondbit.ias.core.entity.DeptInfo;
import com.beyondbit.ias.core.entity.Privilege;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("deptinfo")
public class DeptInfoController extends BaseController {
    @Autowired
    com.beyondbit.ias.core.service.DeptInfoService deptinfoService;

    /**
     * 列表
     * @return
     */
    @RequestMapping("list")
    public String index(){
        return "deptinfo/list.html";
    }

    /**
     * 根据条件查询全部消息日志
     * @return
     */
    @RequestMapping("query")
    @ResponseBody
    public Map query(int page,int limit,String name) {

        DeptInfo deptinfo=new DeptInfo();
        deptinfo.setDeptName(name);

        PageHelper.startPage(page, limit);
        List<DeptInfo> list = deptinfoService.query(deptinfo);

        PageInfo<DeptInfo> pageInfo = new PageInfo<DeptInfo>(list);


        Map<String, Object> map = new HashMap<>(10);
        map.put("code", 0);
        map.put("count", pageInfo.getTotal());
        map.put("msg", "");
        map.put("data", pageInfo.getList());

        return map;
    }

    @PostMapping("insert")
    @ResponseBody
    public int insertDeptInfo(@RequestBody DeptInfo deptinfo) {
        int result = deptinfoService.insertDeptInfo(deptinfo);

        return result;
    }

    @GetMapping("/queryChildren")
    @ResponseBody
    public Layui queryChildren(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                               @RequestParam(value="parentGuid",defaultValue="") String parentGuid,
                               @RequestParam(value="orderbyClause",defaultValue=" deptType asc,sequence asc,id asc") String orderbyClause)
    {
        PageUtils list = deptinfoService.queryDeptUserByParentGuid(pageIndex,pageSize,parentGuid,orderbyClause);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    /**
     * 保存
     **/
    @RequestMapping("save")
    @ResponseBody
    public Layui save(DeptInfo deptinfo) {
        int result;

        //新增需要先判断code是否存在
        if(deptinfo.getId()==null|| deptinfo.getId()==0) {
            DeptInfo entity = deptinfoService.getByCode(deptinfo.getDeptCode());

            if (entity != null && entity.getDeptCode() != null && entity.getDeptCode() != "") {
                return Layui.data(-1, null, "编码已存在");
            }
        }
        deptinfo.setGuid(UUID.randomUUID().toString());
        deptinfo.setIsDelete(0);
        result = deptinfoService.save(deptinfo);
        return Layui.data(result, null, "保存成功");
    }

    /**
     * 删除
     * @param deptinfo
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Layui deleteDeptInfo(DeptInfo deptinfo) {

        int i = deptinfoService.deleteDeptInfo(deptinfo);

        return Layui.data(i, null, null);
    }

    /**
     * 获取树
     */

    @GetMapping("getZtree")
    @ResponseBody
    public List getZtree() {

        List<ZtreeUtil> zTree = deptinfoService.getDeptInfoTree();

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

        List<ZtreeUtil> zTree = deptinfoService.getCheckedZtree(roleguid);

        return zTree;
    }

    @GetMapping("updateSequence")
    @ResponseBody
    public int updateSequence(int sequence,int id)
    {
        return  deptinfoService.updateSequence(sequence,id);
    }

    /**
     * 获得弹出新增页面
     *
     * @return
     */
    @GetMapping("add")
    public String add() {

        return "deptinfo/add.html";
    }

    @GetMapping("getById")
    @ResponseBody
    public DeptInfo getById(int id)
    {
        return  deptinfoService.getById(id);
    }


    @GetMapping("getByGuid")
    @ResponseBody
    public DeptInfo getByGuid(String guid)
    {
        return  deptinfoService.getByGuid(guid);
    }

    /**
     * 获得弹出ztree页面
     *
     * @return
     */
    @GetMapping("getCommonselector")
    public String getCommonselector() {

        return "deptinfo/commonselector.html";
    }

    @GetMapping("authorize")
    public String authorize() {
        return "deptinfo/authorize.html";
    }


}
