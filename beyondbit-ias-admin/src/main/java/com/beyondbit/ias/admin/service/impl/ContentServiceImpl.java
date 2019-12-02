package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.common.TrueOrFalse;
import com.beyondbit.ias.admin.dao.AttachmentMapper;
import com.beyondbit.ias.admin.dao.ContentMapper;
import com.beyondbit.ias.admin.dao.ContentSubjectMapper;
import com.beyondbit.ias.admin.dao.SubjectMapper;
import com.beyondbit.ias.admin.entity.Attachment;
import com.beyondbit.ias.admin.entity.Content;
import com.beyondbit.ias.admin.entity.dto.ContentDTO;
import com.beyondbit.ias.admin.entity.ContentSubject;
import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.admin.service.ContentService;
import com.beyondbit.ias.core.common.PageUtils;
import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private ContentSubjectMapper contentSubjectMapper;

    @Autowired
    private AttachmentMapper attachmentMapper;

    public Content get(Map condition){
        return this.contentMapper.get(condition);
    }

    public int insert(Content entity,String author){
        entity.setUuid(java.util.UUID.randomUUID().toString());
        entity.setCreateTime(new Date());
        entity.setAuthor(author);
        return this.contentMapper.insert(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    public int delete(String contentUUID,String subjectUUID) throws Exception{
        //删除信息栏目关联记录
        Map csCondition = new HashMap();
        csCondition.put("contentUUID",contentUUID);
        csCondition.put("subjectUUID",subjectUUID);
        csCondition.put("isDelete","0");
        int resCS = this.contentSubjectMapper.delete(csCondition);

        //条件判断删除信息记录
        csCondition.clear();
        csCondition.put("contentUUID",contentUUID);
        csCondition.put("isDelete","0");
        if (this.contentSubjectMapper.queryList(csCondition).size()==0){

            Map cCondition = new HashMap();
            cCondition.put("uuid",contentUUID);
            cCondition.put("isDelete","0");
            this.contentMapper.delete(cCondition);
        }

        //删除附件记录
        Map attachmentCondition = new HashMap();
        attachmentCondition.put("refTableID",contentUUID);
        attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_CONTENT");
        attachmentCondition.put("isDelete", 0);
        List<Attachment> attachmentHadList = this.attachmentMapper.queryList(attachmentCondition);
        if (attachmentHadList.size()>0) {
            this.attachmentMapper.delete(attachmentHadList.stream().map(m -> m.getId()).mapToInt(Integer::valueOf).toArray());
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteList(String[] arrUUID) throws Exception{
        for (int i = 0; i < arrUUID.length; i++) {
            String item = arrUUID[i];
            Map condition = new HashMap();
            condition.put("uuid",item);
            condition.put("isDelete","0");
            ContentSubject entity = this.contentSubjectMapper.get(condition);
            this.delete(entity.getContentUUID(),entity.getSubjectUUID());
        }

        return 1;
    }

    public List<ContentDTO> queryList(Map condition){
        return this.contentMapper.queryList(condition);
    }

    public PageUtils queryPageList(int pageIndex, int pageSize, Map condition, String orderByClause){
        PageHelper.startPage(pageIndex,pageSize,orderByClause);
        List<ContentDTO> resList = this.contentMapper.queryList(condition);
        for (ContentDTO item:resList) {
            item.setIsVisible(TrueOrFalse.getName(Integer.parseInt(item.getIsVisible())));
        }
        return new PageUtils(resList);
    }

    @Transactional(rollbackFor = Exception.class)
    public int publish(ContentDTO entity, String author) throws Exception{
        //解析栏目数组
        String[] arrSubjectUUID = entity.getSubjectUUIDList().split(",");

        //如果栏目下面有子栏目，要抛出异常
        Subject condition = new Subject();
        condition.setIsDelete("0");
        for (int i = 0; i < arrSubjectUUID.length; i++) {
            String item = arrSubjectUUID[i];
            condition.setParentUUID(item);
            if (this.subjectMapper.queryList(condition).size() > 0) {
                System.out.println("栏目uuid："+item+"，有子栏目，不可以发布信息！");
                throw new Exception("栏目下有子栏目，不可以发布信息！");
            }
        }

        //新增信息内容
        String contentUUID = java.util.UUID.randomUUID().toString();
        Content entityContent = new Content();
        entityContent.setUuid(contentUUID);
        entityContent.setTitle(entity.getTitle());
        entityContent.setSubTitle(entity.getSubTitle());
        entityContent.setKeyWords(entity.getKeyWords());
        entityContent.setUrl(entity.getUrl());
        entityContent.setSource(entity.getSource());
        entityContent.setStartTime(entity.getStartTime());
        entityContent.setEndTime(entity.getEndTime());
        entityContent.setContent(entity.getContent());
        entityContent.setAbstract(entity.getAbstract());

        entityContent.setCreateTime(new Date());
        entityContent.setAuthor(author);
        entityContent.setIsDelete("0");
        int resContent = this.contentMapper.insert(entityContent);

        //根据栏目发布关联记录
        ContentSubject entityContentSubject = null;
        for (int i = 0; i < arrSubjectUUID.length; i++) {
            String item = arrSubjectUUID[i];

            entityContentSubject = new ContentSubject();
            entityContentSubject.setUuid(java.util.UUID.randomUUID().toString());
            entityContentSubject.setContentUUID(contentUUID);
            entityContentSubject.setSubjectUUID(item);
            entityContentSubject.setIsVisible(entity.getIsVisible());
            entityContentSubject.setIsDelete("0");
            int resContentSubject = this.contentSubjectMapper.insert(entityContentSubject);
        }

        //更新附件记录
        if (entity.getUploadFiles().length()!=0) {
            int[] arrFileID = Arrays.stream(entity.getUploadFiles().split(",")).mapToInt(Integer::valueOf).toArray();
            this.attachmentMapper.updateRef(arrFileID,contentUUID,"ADMIN_INFOPUBLISH_CONTENT",0);
        }
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    public int publishUpdate(ContentDTO entity, String author) throws Exception{

        //解析栏目数组
        String[] arrSubjectUUID = entity.getSubjectUUIDList().split(",");

        //如果栏目下面有子栏目，要抛出异常
        Subject condition = new Subject();
        condition.setIsDelete("0");
        for (int i = 0; i < arrSubjectUUID.length; i++) {
            String item = arrSubjectUUID[i];
            condition.setParentUUID(item);
            if (this.subjectMapper.queryList(condition).size() > 0) {
                System.out.println("栏目uuid："+item+"，有子栏目，不可以发布信息！");
                throw new Exception("栏目下有子栏目，不可以发布信息！");
            }
        }
        //更新信息
        Map cCondition = new HashMap();
        cCondition.put("uuid",entity.getUuid());
        cCondition.put("isDelete","0");
        Content cEntity = this.contentMapper.get(cCondition);
        cEntity.setTitle(entity.getTitle());
        cEntity.setSubTitle(entity.getSubTitle());
        cEntity.setKeyWords(entity.getKeyWords());
        cEntity.setUrl(entity.getUrl());
        cEntity.setSource(entity.getSource());
        cEntity.setStartTime(entity.getStartTime());
        cEntity.setEndTime(entity.getEndTime());
        cEntity.setContent(entity.getContent());
        cEntity.setAbstract(entity.getAbstract());
        int resUpdate = this.contentMapper.update(cEntity);


        //根据新栏目数组去处理已有的CS记录
        //  新关联的栏目，则新增CS记录
        //  如果CS记录得SubjectUUID不包含在栏目数组中的记录需要删除，则删除
        Map csCondition = new HashMap();
        csCondition.put("contentUUID",entity.getUuid());
        csCondition.put("isDelete","0");
        List<ContentSubject> csHadList = this.contentSubjectMapper.queryList(csCondition);

        for (int i = 0; i < arrSubjectUUID.length; i++) {
            String item = arrSubjectUUID[i];

            //新关联的栏目，则新增CS记录
            boolean isNewSubject = csHadList.stream().noneMatch(m->m.getSubjectUUID().equals(item));

            if (isNewSubject){
                //先去数据库查是否有已删除得关联记录，如果有就重新利用，否则会产生冗余数据
//                csCondition.clear();
//                csCondition.put("contentUUID",cEntity.getUuid());
//                csCondition.put("subjectUUID",item);
//                csCondition.put("isDelete","1");
//                ContentSubject entityContentSubject = this.contentSubjectMapper.get(csCondition);
//                if (entityContentSubject==null){
//
//                }
                ContentSubject entityContentSubject = new ContentSubject();
                entityContentSubject.setUuid(java.util.UUID.randomUUID().toString());
                entityContentSubject.setContentUUID(entity.getUuid());
                entityContentSubject.setSubjectUUID(item);
                entityContentSubject.setIsVisible(entity.getIsVisible());
                entityContentSubject.setIsDelete("0");
                int resContentSubject = this.contentSubjectMapper.insert(entityContentSubject);
            }
        }
        for (int i = 0; i < csHadList.size(); i++) {
            ContentSubject csItem = csHadList.get(i);
            //如果CS记录得SubjectUUID不包含在栏目数组中的记录需要删除
            boolean needDel = Arrays.stream(arrSubjectUUID).noneMatch(m->m.equals(csItem.getSubjectUUID()));
            if (needDel){
                csCondition.clear();
                csCondition.put("uuid",csItem.getUuid());
                csCondition.put("isDelete","0");
                int redDel = this.contentSubjectMapper.delete(csCondition);
            }
        }

        //附件
        Map attachmentCondition = new HashMap();
        attachmentCondition.put("refTableID",entity.getUuid());
        attachmentCondition.put("refTableName","ADMIN_INFOPUBLISH_CONTENT");
        attachmentCondition.put("isDelete", 0);
        List<Attachment> attachmentHadList = this.attachmentMapper.queryList(attachmentCondition);

        String uploadFiles = entity.getUploadFiles();
        if (uploadFiles!=null&&uploadFiles.length()!=0){
            Integer[] arrFileID = ArrayUtils.toObject(Arrays.stream(uploadFiles.split(",")).mapToInt(Integer::valueOf).toArray());

            for (int i = 0; i < arrFileID.length; i++) {
                int item = arrFileID[i];
                //附件已有记录中不存在的附件ID需要新增
                boolean isNewAttachment = attachmentHadList.stream().noneMatch(a->a.getId().equals(item));
                if (isNewAttachment){
                    this.attachmentMapper.updateRef(new int[]{item},entity.getUuid(),"ADMIN_INFOPUBLISH_CONTENT",0);
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
            if (attachmentHadList.size()>0){
                this.attachmentMapper.delete(attachmentHadList.stream().map(m->m.getId()).mapToInt(Integer::valueOf).toArray());
            }

        }


        return resUpdate;
    }

}
