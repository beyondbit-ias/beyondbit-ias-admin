package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Shortcut;

import java.util.List;

public interface ShortcutService  {
    public int insert(Shortcut entity);
    public int delete(String useraccount,String modularcode);
    public List<Shortcut> getList(String useraccount);
    public Shortcut get(String useraccount,String modularcode);
}
