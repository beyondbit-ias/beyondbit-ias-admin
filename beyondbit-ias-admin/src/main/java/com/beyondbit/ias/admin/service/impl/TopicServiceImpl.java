package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.TopicMapper;
import com.beyondbit.ias.admin.entity.Topic;
import com.beyondbit.ias.admin.service.TopicService;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicMapper topicMapper;
    public int save(Topic entity)
    {
        if(entity.getId()!=null && entity.getId()>0)
        {
            return topicMapper.update(entity);
        }
        else
            return topicMapper.insert(entity);
    }

    public List<Topic> getListByTestID(int testid)
    {
        return topicMapper.getListByTestID(testid);

    }

    public Topic getByID(int id)
    {
        return topicMapper.getByID(id);
    }

    public int delete(int id)
    {
        return topicMapper.delete(id);
    }

    public int deleteByTestID(int testid){
        return topicMapper.deleteByTestID(testid);
    }
}
