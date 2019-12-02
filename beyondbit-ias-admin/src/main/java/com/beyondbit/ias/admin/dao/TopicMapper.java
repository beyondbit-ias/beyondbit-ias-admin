package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Topic;
import net.sf.jsqlparser.statement.select.Top;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TopicMapper {
    public int insert(Topic entity);

    public int update(Topic entity);

    public List<Topic> getListByTestID(int testid);

    public Topic getByID(int id);

    public int delete(int id);

    public int deleteByTestID(int testid);
}
