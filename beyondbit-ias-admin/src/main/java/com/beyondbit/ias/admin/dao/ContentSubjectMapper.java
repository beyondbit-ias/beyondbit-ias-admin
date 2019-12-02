package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.ContentSubject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 栏目信息关联 gsj 2019-08-31
 */
@Mapper
public interface ContentSubjectMapper {
    public ContentSubject get(Map condition);

    public int insert(ContentSubject entity);

    public int delete(Map condition);

    public int updateSequence(String contentUUID,String subjectUUID,Integer sequence);

    public List<ContentSubject> queryList(Map condition);
}
