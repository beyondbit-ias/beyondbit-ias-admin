package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AttachmentMapper {
    public int insert(Attachment entity);

    public int updateRef(@Param("arrID") int[] arrID,@Param("refTableID") String refTableID,@Param("refTableName") String refTableName,@Param("isDelete") Integer isDelete);

    public List<Attachment> queryList(Map condition);

    public int delete(@Param("idList")int[] idList);
}
