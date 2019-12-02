package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.ReplyMapper;
import com.beyondbit.ias.admin.entity.Reply;
import com.beyondbit.ias.admin.service.ReplyService;
import com.beyondbit.ias.core.common.PageUtils;
import com.github.pagehelper.PageHelper;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {
    @Autowired
    private ReplyMapper replyMapper;
    public int insert(Reply entity){
        return  replyMapper.insert(entity);
    }
    public int deleteByTestID(int testid){
        return replyMapper.deleteByTestID(testid);
    }

    public PageUtils getReplyListByTestID(int pageIndex,int pageSize,int testid){
        PageHelper.startPage(pageIndex,pageSize);
        var list=replyMapper.getReplyListByTestID(testid);
        return new PageUtils(list);
    }

    public Reply getByKeyIDAndGuid(int keyid,String guid){
        return  replyMapper.getByKeyIDAndGuid(keyid,guid);
    }

    public Reply getContentByTopicIDAndGuid(int topicid,String guid)
    {
        return replyMapper.getContentByTopicIDAndGuid(topicid,guid);
    }

    public List<Reply> getContentByTopicID(int topicid)
    {
        return replyMapper.getContentByTopicID(topicid);
    }

    public int getCheckReplyCountByTopicID(int topicid)
    {
        return replyMapper.getCheckReplyCountByTopicID(topicid);
    }

    public int getCountByKeyID(int keyid,int topicid)
    {
        return replyMapper.getCountByKeyID(keyid,topicid);
    }
}
