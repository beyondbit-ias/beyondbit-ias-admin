package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Webpart;

import java.util.List;

public interface WebpartService {
    public int insert(Webpart entity);
    public List<Webpart> getList(String useruid);
    public int deleteList(String useruid);
    public Webpart get(String useruid,String modularcode);
    public int updateIsdelete(int isdelete,String useruid);
    public int update(Webpart entity);
}
