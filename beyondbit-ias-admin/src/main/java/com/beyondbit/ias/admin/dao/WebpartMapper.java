package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Webpart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WebpartMapper {
    public int insert(Webpart entity);
    public List<Webpart> getList(String useruid);
    public int deleteList(String useruid);
    public Webpart get(String useruid,String modularcode);
    public int updateIsdelete(int isdelete,String useruid);
    public int update(Webpart entity);
}
