package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Content;
import com.beyondbit.ias.admin.entity.dto.ContentDTO;
import com.beyondbit.ias.core.common.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 信息发布 gsj 2019-08-31
 */
public interface ContentService {

    public Content get(Map condition);

    public int insert(Content entity,String author);

    public int delete(String contentUUID,String subjectUUID) throws Exception;

    public int deleteList(String[] arrUUID) throws Exception;

    public List<ContentDTO> queryList(Map condition);

    public PageUtils queryPageList(int pageIndex, int pageSize, Map condition, String orderByClause);

    public int publish(ContentDTO entity, String author) throws Exception;

    public int publishUpdate(ContentDTO entity, String author) throws Exception;
}
