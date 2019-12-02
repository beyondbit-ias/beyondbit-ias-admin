package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.admin.entity.Content;
import com.beyondbit.ias.admin.entity.dto.ContentDTO;
import com.beyondbit.ias.admin.entity.ContentSubject;
import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.admin.service.AttachmentService;
import com.beyondbit.ias.admin.service.ContentService;
import com.beyondbit.ias.admin.service.ContentSubjectService;
import com.beyondbit.ias.admin.service.SubjectService;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信息发布 gsj 2019-08-31
 */
@Controller
@RequestMapping("content")
public class ContentController extends BaseController {

    @Autowired
    ContentService contentService;

    @Autowired
    ContentSubjectService contentSubjectService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    AttachmentService attachmentService;

    @RequestMapping("/list")
    public String list(@RequestParam(value = "subjectUUID", defaultValue = "root") String subjectUUID,
                       @RequestParam(value = "subjectName", defaultValue = "站点结构") String subjectName,
                       Model model) {


        model.addAttribute("subjectUUID", subjectUUID);
        model.addAttribute("subjectName", subjectName);

        return "content/list.html";
    }

    @RequestMapping("/queryPageList")
    @ResponseBody
    public Layui queryPageList(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                               @RequestParam(value = "limit", defaultValue = "10") int pageSize,
                               @RequestParam(value = "subjectUUIDList", defaultValue = "") String subjectUUIDList,
                               @RequestParam(value = "title",defaultValue = "") String title,
                               @RequestParam(value = "keyWords",defaultValue = "") String keyWords,
                               @RequestParam(value = "startTime",defaultValue = "") String startTime,
                               @RequestParam(value = "endTime",defaultValue = "") String endTime,
                               @RequestParam(value = "orderbyClause", defaultValue = " scs.sequence asc,scs.id desc") String orderbyClause) {
        Map condition = new HashMap();
        if (subjectUUIDList.equals("")==false){
            condition.put("cs_subjectUUIDList", subjectUUIDList.split(","));
        }
        if (title.equals("")==false){
            condition.put("c_title",title);
        }
        if (keyWords.equals("")==false){
            condition.put("c_keyWords",keyWords);
        }
        if (startTime.equals("")==false){
            condition.put("c_startTime",startTime);
        }
        if (endTime.equals("")==false){
            condition.put("c_endTime",endTime);
        }

        condition.put("cs_isDelete", "0");
        condition.put("c_isDelete", "0");
        PageUtils result = this.contentService.queryPageList(pageIndex, pageSize, condition, orderbyClause);
        return Layui.data(result.getTotalCount(), result.getList(), "OK");
    }

    @GetMapping("/canPublish")
    @ResponseBody
    public Layui canPublish(@RequestParam(value = "subjectUUID", defaultValue = "root") String subjectUUID,
                            @RequestParam(value = "subjectName", defaultValue = "站点结构") String subjectName){
        //如果当前栏目没有子栏目，则可以发布信息
        boolean canPublish = false;
        Subject condition = new Subject();
        condition.setParentUUID(subjectUUID);
        condition.setIsDelete("0");
        if (this.subjectService.queryList(condition).size()==0){
            return Layui.data(1,null,"可以发布信息！");
        }else{
            return Layui.data(-1,null,"该栏目存在子栏目，不可以发布信息！");
        }
    }

    @RequestMapping("/add")
    public String add(String subjectUUID, String subjectName, Model model) {
        model.addAttribute("subjectUUIDList", subjectUUID);
        model.addAttribute("subjectNameList", subjectName);
        return "content/add.html";
    }


    @PostMapping("/publish")
    @ResponseBody
    public Layui publish(ContentDTO entity) {
        try {
            String author = super.getCurrentUser().getUserUid();
            int res = this.contentService.publish(entity, author);
            return Layui.data(res, null, "发布成功！");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Layui.data(-1, null, "发布失败！" + ex.getMessage());
        }
    }

    @GetMapping("/edit")
    public String edit(String subjectUUID,String contentUUID, Model model) {
        Map condition = new HashMap();
        condition.put("uuid", contentUUID);
        condition.put("isDelete", "0");
        //信息
        Content entity = this.contentService.get(condition);

        //信息栏目关联
        Map csCondition = new HashMap();
        csCondition.put("contentUUID", contentUUID);
        csCondition.put("isDelete", "0");
        List<ContentSubject> csList = this.contentSubjectService.queryList(csCondition);

        StringBuffer subjectUUIDList = new StringBuffer();
        StringBuffer subjectNameList = new StringBuffer();
        for (int i = 0; i < csList.size(); i++) {
            ContentSubject item = csList.get(i);
            if (item.getSubjectUUID().equals(subjectUUID)){
                model.addAttribute("csEntity", item);
            }

//            Map sCondition = new HashMap();
//            sCondition.put("isDelete","0");
//            sCondition.put("uuid",item.getSubjectUUID());

            Subject subjectItem = new Subject();
            subjectItem.setUuid(item.getSubjectUUID());
            subjectItem.setIsDelete("0");
            subjectItem = this.subjectService.queryList(subjectItem).get(0);

            if (i == 0) {
                subjectUUIDList.append(item.getSubjectUUID());
                subjectNameList.append(subjectItem.getName());
            } else {
                subjectUUIDList.append("," + item.getSubjectUUID());
                subjectNameList.append("," + subjectItem.getName());
            }
        }

        //附件
        Map attachmentCondition = new HashMap();
        attachmentCondition.put("refTableID",entity.getUuid());
        attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_CONTENT");
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
        model.addAttribute("subjectUUID", subjectUUID);
        model.addAttribute("contentUUID", contentUUID);
        model.addAttribute("subjectUUIDList", subjectUUIDList.toString());
        model.addAttribute("subjectNameList", subjectNameList.toString());
        model.addAttribute("uploadFiles", uploadFiles);
        model.addAttribute("uploadFilesLength", arrFileID.length);
        return "content/edit.html";
    }

    @PostMapping("/publishUpdate")
    @ResponseBody
    public Layui publishUpdate(ContentDTO entity) {
        try {
            String author = super.getCurrentUser().getUserUid();
            int res = this.contentService.publishUpdate(entity, author);
            return Layui.data(res, null, "发布成功！");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Layui.data(-1, null, "发布失败！" + ex.getMessage());
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Layui delete(String contentUUID, String subjectUUID) {
        try {
            int res = this.contentService.delete(contentUUID, subjectUUID);
            return Layui.data(res, null, "删除成功！");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Layui.data(-1, null, "删除失败！" + ex.getMessage());
        }
    }

    @RequestMapping("/deleteList")
    @ResponseBody
    public Layui deleteList(String uuidList){
        try {
            String[] arrUUID = uuidList.split(",");
            int res = this.contentService.deleteList(arrUUID);
            return Layui.data(res, null, "删除成功！");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Layui.data(-1, null, "删除失败！" + ex.getMessage());
        }
    }

    @GetMapping("/updateSequence")
    @ResponseBody
    public int updateSequence(String contentUUID,String subjectUUID,Integer sequence){
        return this.contentSubjectService.updateSequence(contentUUID,subjectUUID,sequence);
    }

    @RequestMapping("/advanceList")
    public String advanceList(@RequestParam(value = "subjectUUID", defaultValue = "root") String subjectUUID,
                              @RequestParam(value = "subjectName", defaultValue = "站点结构") String subjectName,
                              Model model) {
        model.addAttribute("subjectUUID", subjectUUID);
        model.addAttribute("subjectName", subjectName);
        return "content/advanceList.html";
    }
}
