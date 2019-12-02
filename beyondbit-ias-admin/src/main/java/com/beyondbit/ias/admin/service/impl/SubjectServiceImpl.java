package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.common.TrueOrFalse;
import com.beyondbit.ias.admin.dao.AttachmentMapper;
import com.beyondbit.ias.admin.dao.ContentSubjectMapper;
import com.beyondbit.ias.admin.dao.SubjectMapper;
import com.beyondbit.ias.admin.entity.Attachment;
import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.admin.entity.dto.SubjectDTO;
import com.beyondbit.ias.admin.service.SubjectService;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 栏目管理 gsj 2019-08-31
 */
@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private ContentSubjectMapper contentSubjectMapper;

    @Transactional(rollbackFor = Exception.class)
    public int insert(SubjectDTO entityDTO, String creater){
        //新增栏目
        Subject newEntity = new Subject();
        newEntity.setUuid(java.util.UUID.randomUUID().toString());
        newEntity.setName(entityDTO.getName());
        newEntity.setParentUUID(entityDTO.getParentUUID());
        newEntity.setUrl(entityDTO.getUrl());
        newEntity.setCode(entityDTO.getCode());
        newEntity.setIsVisible(entityDTO.getIsVisible());
        newEntity.setSubjectDESC(entityDTO.getSubjectDESC());
        newEntity.setCreateTime(new Date());
        newEntity.setCreater(creater);
        newEntity.setSequence(entityDTO.getSequence());
        newEntity.setIsDelete("0");


        subjectMapper.insert(newEntity);

        //更新附件记录
        if (entityDTO.getUploadFiles().length()!=0) {
            int[] arrFileID = Arrays.stream(entityDTO.getUploadFiles().split(",")).mapToInt(Integer::valueOf).toArray();
            this.attachmentMapper.updateRef(arrFileID,newEntity.getUuid(),"ADMIN_INFOPUBLISH_SUBJECT",0);
        }

        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public int update(SubjectDTO entityDTO,String creater) throws Exception{
        //更新栏目
        Subject updateEntity = new Subject();
        updateEntity.setUuid(entityDTO.getUuid());
        updateEntity = this.subjectMapper.queryList(updateEntity).get(0);

        //过滤上级栏目不可以选择当前栏目
        if (entityDTO.getParentUUID().equals(updateEntity.getUuid())){
            throw new Exception("上级栏目不可以选择当前栏目！");
        }

        updateEntity.setName(entityDTO.getName());
        updateEntity.setParentUUID(entityDTO.getParentUUID());
        updateEntity.setUrl(entityDTO.getUrl());
        updateEntity.setIsVisible(entityDTO.getIsVisible());
        updateEntity.setSubjectDESC(entityDTO.getSubjectDESC());
        updateEntity.setSequence(entityDTO.getSequence());

        this.subjectMapper.update(updateEntity);

        //更新附件记录
        Map attachmentCondition = new HashMap();
        attachmentCondition.put("refTableID",entityDTO.getUuid());
        attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_SUBJECT");
        attachmentCondition.put("isDelete", 0);
        List<Attachment> attachmentHadList = this.attachmentMapper.queryList(attachmentCondition);

        String uploadFiles = entityDTO.getUploadFiles();
        if (uploadFiles!=null&&uploadFiles.length()!=0){
            Integer[] arrFileID = ArrayUtils.toObject(Arrays.stream(uploadFiles.split(",")).mapToInt(Integer::valueOf).toArray());

            for (int i = 0; i < arrFileID.length; i++) {
                int item = arrFileID[i];
                //附件已有记录中不存在的附件ID需要新增
                boolean isNewAttachment = attachmentHadList.stream().noneMatch(a->a.getId().equals(item));
                if (isNewAttachment){
                    this.attachmentMapper.updateRef(new int[]{item},entityDTO.getUuid(),"ADMIN_INFOPUBLISH_SUBJECT",0);
                }
            }

            for (int i = 0; i < attachmentHadList.size(); i++) {
                Attachment item = attachmentHadList.get(i);
                //附件ID集合中不存在的附件记录需要删除
                boolean isNeedDel = Arrays.stream(arrFileID).noneMatch(id->id.equals(item.getId()));
                if (isNeedDel){
                    this.attachmentMapper.delete(new int[]{item.getId()});
                }
            }
        }else {
            //删除所有附件
            if(attachmentHadList.size()>0){
                this.attachmentMapper.delete(attachmentHadList.stream().map(m->m.getId()).mapToInt(Integer::valueOf).toArray());
            }
        }

        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete(String uuid) throws Exception{
        Subject condition = new Subject();
        condition.setParentUUID(uuid);
        if (this.subjectMapper.queryList(condition).size()!=0){
            throw new Exception("当前栏目存在子栏目，不可删除！");
        }

        Map csCondition = new HashMap();
        csCondition.put("subjectUUID",uuid);
        csCondition.put("isDelete","0");
        if (this.contentSubjectMapper.queryList(csCondition).size()>0){
            throw new Exception("当前栏目存在信息，不可删除！");
        }

        Subject delEntity = new Subject();
        delEntity.setUuid(uuid);
        delEntity = this.subjectMapper.queryList(delEntity).get(0);
        delEntity.setIsDelete("1");
        this.subjectMapper.update(delEntity);

        //删除附件记录
        Map attachmentCondition = new HashMap();
        attachmentCondition.put("refTableID",uuid);
        attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_SUBJECT");
        attachmentCondition.put("isDelete", 0);
        List<Attachment> attachmentHadList = this.attachmentMapper.queryList(attachmentCondition);
        if (attachmentHadList.size()>0){
            this.attachmentMapper.delete(attachmentHadList.stream().map(m->m.getId()).mapToInt(Integer::valueOf).toArray());
        }


        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteList(String[] uuidList) throws Exception{

        for (int i = 0; i < uuidList.length; i++) {
            String item = uuidList[i];
            this.delete(item);
//            Subject condition = new Subject();
//            condition.setParentUUID(item);
//            if (this.subjectMapper.queryList(condition).size()!=0){
//                throw new Exception("当前栏目存在子栏目，不可删除！");
//            }

//            Subject delEntity = new Subject();
//            delEntity.setUuid(item);
//            delEntity = this.subjectMapper.queryList(delEntity).get(0);
//            delEntity.setIsDelete("1");
//            this.subjectMapper.update(delEntity);
//
//            //删除附件记录
//            Map attachmentCondition = new HashMap();
//            attachmentCondition.put("refTableID",item);
//            attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_SUBJECT");
//            attachmentCondition.put("isDelete", 0);
//            List<Attachment> attachmentHadList = this.attachmentMapper.queryList(attachmentCondition);
//            this.attachmentMapper.delete(attachmentHadList.stream().map(m->m.getId()).mapToInt(Integer::valueOf).toArray());
        }

        return 1;
    }

    public List<Subject> queryList(Subject condition){
        return this.subjectMapper.queryList(condition);
    }

    public PageUtils queryPageList(int pageIndex, int pageSize, Subject condition, String orderbyClause) {
        PageHelper.startPage(pageIndex, pageSize, orderbyClause);
        List<Subject> subjectList = this.subjectMapper.queryList(condition);
        for (Subject item:subjectList) {
            item.setIsVisible(TrueOrFalse.getName(Integer.parseInt(item.getIsVisible())));
        }
        return new PageUtils(subjectList);
    }

    public List<ZtreeUtil> queryTreeList(Subject condition){
        List<ZtreeUtil> list = new ArrayList();
        List<Subject> allSubjectList = this.subjectMapper.queryList(condition);
        for (Subject item:allSubjectList) {
            ZtreeUtil ztreeUtil = new ZtreeUtil();
            ztreeUtil.setId(item.getUuid());
            ztreeUtil.setPId(item.getParentUUID());
            ztreeUtil.setName(item.getName());
            ztreeUtil.setChecked(false);
            ztreeUtil.setNocheck(false);
            ztreeUtil.setOpen(false);
            list.add(ztreeUtil);
        }
        return list;
    }

    public int updateSequence(Subject entity){
        return this.subjectMapper.updateSequence(entity);
    }

    public Subject get(String uuid){
        return this.subjectMapper.get(uuid);
    }

    public List<ZtreeUtil> getCheckedZtree(String roleguid) {
        List<ZtreeUtil> list=new ArrayList<ZtreeUtil>();
        List<Subject> allsubject = this.subjectMapper.findAllSubject();
        List<Subject> subjectlist = this.subjectMapper.querySubjectByRoleGuid(roleguid);
        List<String> subjectIds=subjectlist.stream().map(Subject::getUuid).collect(Collectors.toList());
        for(Subject d :allsubject)
        {
            ZtreeUtil ztreeUtil=new ZtreeUtil();
            ztreeUtil.setId(d.getUuid());
            ztreeUtil.setPId(d.getParentUUID());
            ztreeUtil.setName(d.getName());
            if(subjectIds!=null&&subjectIds.contains(d.getUuid()))
            {
                ztreeUtil.setChecked(true);
            }
            else
            {
                ztreeUtil.setChecked(false);
            }

            ztreeUtil.setNocheck(false);
            ztreeUtil.setOpen(false);
            list.add(ztreeUtil);
        }
        return list;
    }
}
