package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Key;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeyMapper {
    public int insert(Key entity);

    public int update(Key entity);

    public List<Key> getListByTopicID(int topicid);

    public int delete(int id);

    public int deleteByTopicID(int topicid);

}
