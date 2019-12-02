package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.ContentSubjectMapper;
import com.beyondbit.ias.admin.entity.ContentSubject;
import com.beyondbit.ias.admin.service.ContentSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 栏目信息关联 gsj 2019-08-31
 */
@Service
public class ContentSubjectServiceImpl implements ContentSubjectService {

    @Autowired
    private ContentSubjectMapper contentSubjectMapper;

    public List<ContentSubject> queryList(Map condition){
        return this.contentSubjectMapper.queryList(condition);
    }

    public int updateSequence(String contentUUID,String subjectUUID,Integer sequence){
        return this.contentSubjectMapper.updateSequence(contentUUID,subjectUUID,sequence);
    }
}
