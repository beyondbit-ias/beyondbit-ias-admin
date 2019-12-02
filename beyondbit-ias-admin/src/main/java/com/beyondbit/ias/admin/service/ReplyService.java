package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Reply;
import com.beyondbit.ias.core.common.PageUtils;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ReplyService {
    public int insert(Reply entity);

    public int deleteByTestID(int testid);

    public PageUtils getReplyListByTestID(int pageIndex,int pageSize,int testid);

    public Reply getByKeyIDAndGuid(int keyid,String guid);

    public Reply getContentByTopicIDAndGuid(int topicid,String guid);

    public List<Reply> getContentByTopicID(int topicid);

    public int getCheckReplyCountByTopicID(int topicid);

    public int getCountByKeyID(int keyid,int topicid);
}
