package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.core.common.ZtreeUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 栏目管理 gsj 2019-08-31
 */
@Mapper
public interface SubjectMapper {
    public int insert(Subject entity);

    public int update(Subject entity);

    public int delete(Subject entity);

    public List<Subject> queryList(Subject condition);

    public int updateSequence(Subject entity);

    public Subject get(String uuid);

    public List<Subject> findAllSubject();

    public List<Subject> querySubjectByRoleGuid(@Param("roleGuid") String roleGuid);

    //查询用户所有栏目
    public List<Subject> findAllByAccount(@Param("account") String account);
}
