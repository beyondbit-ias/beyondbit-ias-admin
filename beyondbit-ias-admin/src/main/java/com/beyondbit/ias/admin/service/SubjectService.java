package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.admin.entity.dto.SubjectDTO;
import com.beyondbit.ias.core.common.PageUtils;
import com.beyondbit.ias.core.common.ZtreeUtil;

import java.util.List;

/**
 * 栏目管理 gsj 2019-08-31
 */
public interface SubjectService {
    int insert(SubjectDTO entityDTO, String creater);

    int update(SubjectDTO entityDTO,String creater) throws Exception;

    int delete(String uuid) throws Exception;

    int deleteList(String[] uuidList) throws Exception;

    List<Subject> queryList(Subject condition);

    PageUtils queryPageList(int pageIndex, int pageSize, Subject condition, String orderbyClause);

    List<ZtreeUtil> queryTreeList(Subject condition);

    int updateSequence(Subject entity);

    Subject get(String uuid);

    /**
     * 前台 展示 已选
     * @return
     */
    public List<ZtreeUtil> getCheckedZtree(String roleguid);
}
