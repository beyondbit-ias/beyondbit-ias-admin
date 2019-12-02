package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.WebpartMapper;
import com.beyondbit.ias.admin.entity.Webpart;
import com.beyondbit.ias.admin.service.WebpartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebpartServiceImpl implements WebpartService {
    @Autowired
    private WebpartMapper webpartMapper;
    public int insert(Webpart entity)
    {
        return webpartMapper.insert(entity);
    }

    public List<Webpart> getList(String useruid)
    {
        return webpartMapper.getList(useruid);
    }

    public int deleteList(String useruid)
    {
        return webpartMapper.deleteList(useruid);
    }
    public Webpart get(String useruid,String modularcode){
        return webpartMapper.get(useruid,modularcode);
    }
    public int updateIsdelete(int isdelete,String useruid){
        return webpartMapper.updateIsdelete(isdelete,useruid);
    }
    public int update(Webpart entity){
        return webpartMapper.update(entity);
    }
}
