package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Topic;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TopicService {
    public int save(Topic entity);
    public List<Topic> getListByTestID(int testid);
    public Topic getByID(int id);
    public int delete(int id);
    public int deleteByTestID(int testid);
}
