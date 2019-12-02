package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Key;

import java.util.List;

public interface KeyService {
    public int save(Key entity);
    public List<Key> getListByTopicID(int topicid);
    public int delete(int id);

    public int deleteByTopicID(int topicid);
}
