package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Content;
import com.beyondbit.ias.admin.entity.Subject;
import com.beyondbit.ias.admin.entity.dto.ContentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 信息发布 gsj 2019-08-31
 */
@Mapper
public interface ContentMapper {
    public Content get(Map condition);

    public int insert(Content entity);

    public int update(Content entity);

    public int delete(Map condition);

    public List<ContentDTO> queryList(Map condition);
}
