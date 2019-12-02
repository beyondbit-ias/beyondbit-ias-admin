package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.ContentSubject;
import com.beyondbit.ias.admin.entity.dto.ContentDTO;

import java.util.List;
import java.util.Map;

/**
 * 栏目信息关联 gsj 2019-08-31
 */
public interface ContentSubjectService {
    public List<ContentSubject> queryList(Map condition);

    public int updateSequence(String contentUUID,String subjectUUID,Integer sequence);
}
