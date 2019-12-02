package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.admin.common.IconInfo;
import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.admin.entity.dto.SubjectDTO;
import com.beyondbit.ias.admin.service.AttachmentService;
import com.beyondbit.ias.admin.service.SubjectService;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 栏目管理 gsj 2019-08-31
 */
@Controller
@RequestMapping("subject")
public class SubjectController extends BaseController {

    @Autowired
    SubjectService subjectService;

    @Autowired
    AttachmentService attachmentService;

    @RequestMapping("/list")
    public String list() {
        return "subject/list.html";
    }

    @RequestMapping("/queryPageList")
    @ResponseBody
    public Layui queryPageList(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                               @RequestParam(value = "parentUUID", defaultValue = "") String parentUUID,
                               @RequestParam(value = "orderbyClause", defaultValue = " sequence asc,id desc") String orderbyClause) {
        Subject condition = new Subject();
        condition.setParentUUID(parentUUID);
        condition.setIsDelete("0");

        PageUtils result = subjectService.queryPageList(pageIndex, pageSize, condition, orderbyClause);
        return Layui.data(result.getTotalCount(), result.getList(), "OK");
    }

    @GetMapping("/queryTreeList")
    @ResponseBody
    public List queryTreeList(String parentUUID) {
        Subject condition = new Subject();
        condition.setIsDelete("0");
        if (parentUUID != null) {
            condition.setParentUUID(parentUUID);
        }
        return subjectService.queryTreeList(condition);
    }

    @GetMapping("/updateSequence")
    @ResponseBody
    public int updateSequence(Subject entity) {
        return this.subjectService.updateSequence(entity);
    }

    @RequestMapping("/add")
    public String add(@RequestParam("parentUUID") String parentUUID, @RequestParam("parentName") String parentName, Model model) {
        model.addAttribute("parentUUID", parentUUID);
        model.addAttribute("parentName", parentName);
        return "subject/add.html";
    }

    @RequestMapping("/insert")
    @ResponseBody
    public Layui insert(SubjectDTO entity) {

        Subject condition = new Subject();
        condition.setCode(entity.getCode());
        if (this.subjectService.queryList(condition).size() != 0) {
            return Layui.data(-1, null, "编码已存在");
        }

        String creater = super.getCurrentUser().getUserUid();

        int res = this.subjectService.insert(entity, creater);
        return Layui.data(res, null, "新增成功");
    }

    @RequestMapping("/edit")
    public String edit(@RequestParam("uuid") String uuid, Model model) {
        Subject entity = new Subject();
        entity.setUuid(uuid);
        entity = this.subjectService.queryList(entity).get(0);

        Subject parent = new Subject();
        parent.setUuid(entity.getParentUUID());
        parent = this.subjectService.queryList(parent).get(0);

        //附件
        Map attachmentCondition = new HashMap();
        attachmentCondition.put("refTableID",entity.getUuid());
        attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_SUBJECT");
        attachmentCondition.put("isDelete", 0);
        int[] arrFileID = this.attachmentService.queryList(attachmentCondition).stream().map(m->m.getId()).mapToInt(Integer::valueOf).toArray();
        String uploadFiles = "";
        for (int i = 0; i < arrFileID.length; i++) {
            if (i==0){
                uploadFiles = String.valueOf(arrFileID[i]);
            }else{
                uploadFiles += "," + String.valueOf(arrFileID[i]);
            }
        }

        model.addAttribute("entity", entity);
        model.addAttribute("parentName", parent.getName());
        model.addAttribute("uploadFiles", uploadFiles);
        model.addAttribute("uploadFilesLength", arrFileID.length);
        return "subject/edit.html";
    }

    @RequestMapping("/update")
    @ResponseBody
    public Layui update(SubjectDTO entity) {
        try {
            String creater = super.getCurrentUser().getUserUid();
            int res = this.subjectService.update(entity, creater);
            return Layui.data(res, null, "更新成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Layui.data(-1, null, "更新异常！" + ex.getMessage());
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Layui delete(String uuid) {
        try {
            //        if (this.subjectService.queryList(entity).size()==0){
//            return Layui.data(-1, null, "删除异常！");
//        }
            int res = this.subjectService.delete(uuid);
            return Layui.data(res, null, "删除成功！");
        } catch (Exception ex) {
            return Layui.data(-1, null, "删除异常！" + ex.getMessage());
        }
    }

    @RequestMapping("/deleteList")
    @ResponseBody
    public Layui deleteList(String uuidList){
        try{
            String[] toDelUUIDList = uuidList.split(",");
            int res = this.subjectService.deleteList(toDelUUIDList);
            return Layui.data(res, null, "删除成功！");
        }
        catch(Exception ex){
            return Layui.data(-1, null, "删除异常！" + ex.getMessage());
        }
    }

    @RequestMapping("get")
    @ResponseBody
    public Subject get(String uuid) {
        return this.subjectService.get(uuid);
    }

    @RequestMapping("/getParentSelector")
    public String getParentSelector(String parentUUID, Model model) {
        model.addAttribute("parentUUID", parentUUID);
        return "subject/parentSelector.html";
    }

    @RequestMapping("/getSubjectSelector")
    public String getSubjectSelector(String selectedSubjectUUIDList, Model model) {
        model.addAttribute("SelectedSubjectUUIDList", selectedSubjectUUIDList);
        return "subject/subjectSelector.html";
    }

    /**
     * 根据角色id获取 显示已选
     * @param roleguid
     * @return
     */
    @GetMapping("getCheckedZtree")
    @ResponseBody
    public List getCheckedZtree(String roleguid) {

        List<ZtreeUtil> zTree = subjectService.getCheckedZtree(roleguid);

        return zTree;
    }

    @GetMapping("authorize")
    public String authorize() {
        return "subject/authorize.html";
    }
}
