package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Shortcut;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShortcutMapper {
    public int insert(Shortcut entity);
    public int delete(String useraccount,String modularcode);
    public List<Shortcut> getList(String useraccount);
    public Shortcut get(String useraccount,String modularcode);


}
