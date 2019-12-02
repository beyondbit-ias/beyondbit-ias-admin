package com.beyondbit.ias.admin.dao;

import com.beyondbit.ias.admin.entity.Reply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {
    public int insert(Reply entity);
    public int deleteByTestID(int testid);
    public List<Reply> getReplyListByTestID(int testid);

    public Reply getByKeyIDAndGuid(int keyid,String guid);
    public Reply getContentByTopicIDAndGuid(int topicid,String guid);

    public List<Reply> getContentByTopicID(int topicid);

    public int getCheckReplyCountByTopicID(int topicid);

    public int getCountByKeyID(int keyid,int topicid);


}
