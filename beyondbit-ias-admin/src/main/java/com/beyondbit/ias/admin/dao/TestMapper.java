package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper {

    public int insert(Test entity);

    public int update(Test entity);

    public Test getByID(int id);

    public List<Test> getlist();

    public int delete(int id);

    public int updateSequence(int sequence,int id);

}
