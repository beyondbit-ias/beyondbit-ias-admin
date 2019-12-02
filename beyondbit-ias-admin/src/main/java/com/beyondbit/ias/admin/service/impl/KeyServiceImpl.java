package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.KeyMapper;
import com.beyondbit.ias.admin.entity.Key;
import com.beyondbit.ias.admin.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyServiceImpl implements KeyService {
    @Autowired
    private KeyMapper keyMapper;
    public int save(Key entity)
    {
        if(entity.getId()!=null && entity.getId()>0)
            return keyMapper.update(entity);
        else
            return keyMapper.insert(entity);
    }

    public List<Key> getListByTopicID(int topicid)
    {
        return keyMapper.getListByTopicID(topicid);
    }

    public int delete(int id)
    {
        return keyMapper.delete(id);
    }

    public int deleteByTopicID(int topicid){
        return keyMapper.deleteByTopicID(topicid);
    }
}
