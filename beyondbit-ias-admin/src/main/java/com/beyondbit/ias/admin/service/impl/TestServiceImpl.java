package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.TestMapper;
import com.beyondbit.ias.admin.entity.Test;
import com.beyondbit.ias.admin.service.TestService;
import com.beyondbit.ias.core.common.PageUtils;
import com.github.pagehelper.PageHelper;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private TestMapper testMapper;
    public int save(Test entity)
    {
        if(entity.getId()!=null && entity.getId()>0) {
            return testMapper.update(entity);
        }
        else
            return testMapper.insert(entity);

    }

    public Test getByID(int id)
    {
        return testMapper.getByID(id);
    }

    public PageUtils getlist(int pageIndex, int pageSize){
        PageHelper.startPage(pageIndex,pageSize);
        var lists= testMapper.getlist();
        return  new PageUtils(lists);
    }

    public int delete(int id)
    {
        return testMapper.delete(id);
    }

    public int updateSequence(int sequence, int id)
    {
        return testMapper.updateSequence(sequence,id);
    }
}
