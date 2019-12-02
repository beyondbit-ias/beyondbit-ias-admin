package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.ShortcutMapper;
import com.beyondbit.ias.admin.entity.Shortcut;
import com.beyondbit.ias.admin.service.ShortcutService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShortcutServiceImpl implements ShortcutService {
    @Autowired
    private ShortcutMapper shortcutMapper;
    public int insert(Shortcut entity)
    {
        return shortcutMapper.insert(entity);
    }

    public int delete(String useraccount,String modularcode)
    {
        return shortcutMapper.delete(useraccount,modularcode);
    }
    public List<Shortcut> getList(String useraccount)
    {
        return shortcutMapper.getList(useraccount);
    }

    public Shortcut get(String useraccount,String modularcode)
    {
        return shortcutMapper.get(useraccount,modularcode);
    }
}
