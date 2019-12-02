package com.beyondbit.ias.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.beyondbit.ias.admin.entity.Key;
import com.beyondbit.ias.admin.entity.Reply;
import com.beyondbit.ias.admin.entity.Test;
import com.beyondbit.ias.admin.entity.Topic;
import com.beyondbit.ias.admin.service.KeyService;
import com.beyondbit.ias.admin.service.ReplyService;
import com.beyondbit.ias.admin.service.TestService;
import com.beyondbit.ias.admin.service.TopicService;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import com.beyondbit.ias.core.common.PageUtils;
import lombok.var;
import net.sf.jsqlparser.statement.select.Top;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
@RequestMapping("commentonline")
public class CommentOnlineController extends BaseController {
    @Autowired
    private TestService testSerice;
    @Autowired
    private TopicService topicService;
    @Autowired
    private KeyService keyService;
    @Autowired
    private ReplyService replyService;

    @GetMapping("/addtest")
    public String addTest(){
        return "commentonline/addtest.html";
    }

    @GetMapping("/addtopic")
    public String addTopic(){
        return "commentonline/addtopic.html";
    }

    @GetMapping("/topicmanager")
    public String topManager(){return "commentonline/topicmanager.html";}

    @GetMapping("/testlist")
    public String testList(){return "commentonline/testlist.html";}

    @GetMapping("/topicweb")
    public String topicWeb(){
        return "commentonline/topicweb.html";
    }

    @GetMapping("/replylist")
    public String replayList(){return "commentonline/replylist.html";}

    @GetMapping("/restoretest")
    public String restoreTest(){return "commentonline/restoretest.html";}

    @GetMapping("/analyse")
    public String analyse(){return "commentonline/analyse.html";}

    @RequestMapping("/savetest")
    @ResponseBody
    public int saveTest(Test entity) {
        if(entity.getId()==null) {
            entity.setCreatetime(new Date());
            entity.setCreateusername(getCurrentUser().getName());
        }
        else {
            var test=testSerice.getByID(entity.getId());
            entity.setCreatetime(test.getCreatetime());
            entity.setCreateusername(test.getCreateusername());
        }

        int result=testSerice.save(entity);
        if(result>0)
            return entity.getId();
        else
            return 0;
    }
    
    @RequestMapping("/savetopic")
    @ResponseBody
    public int saveTopic(Topic entity)
    {
        //先保存topic
        entity.setCreatetime(new Date());
        //判断如果isallowotherkey为0，那么allowotherkeyname为空【注意：如果isallowotherkey的checkbox没有选中，并不会传该参数。】
        if(entity.getIsallowotherkey()==null || entity.getIsallowotherkey()==0)
            entity.setAllowotherkeyname("");
        int topicResult=topicService.save(entity);

        int keyResult=0;
        if(topicResult>0 && entity.getKeylists()!=null && entity.getKeylists()!="")
        {

            List<Key> keyList =  JSON.parseArray(entity.getKeylists(), Key.class);
            for (Key key:keyList ) {
                if(key.getContent()!=null && key.getContent().length()>0) {
                    key.setCreatetime(new Date());
                    if (entity.getId() != key.getTopicid()) {
                        //新增or复制情况
                        key.setTopicid(entity.getId());
                        //复制情况
                        key.setId(null);
                    }
                    keyService.save(key);
                    keyResult++;
                }

            }
        }
        return keyResult+topicResult;
    }

    @RequestMapping("/savekeys")
    @ResponseBody
    public int saveKeys(String keylists)
    {
        List<Key> keyList =  JSON.parseArray(keylists, Key.class);
        return keyList.size();
    }

    @RequestMapping("/getkeys")
    @ResponseBody
    public List<Key> getKeys(int topicid)
    {
        return keyService.getListByTopicID(topicid);
    }

    @RequestMapping("/gettopics")
    @ResponseBody
    public Test getTopics(int testid)
    {
        Test test=testSerice.getByID(testid);
        List<Topic> topicList=topicService.getListByTestID(testid);
        for(Topic t:topicList)
        {
            int topicid=t.getId();
            List<Key> keys=keyService.getListByTopicID(topicid);
            t.setKeys(keys);
        }
        test.setTopics(topicList);
        return test;
    }

    @RequestMapping("/gettopic")
    @ResponseBody
    public Topic getTopic(int id)
    {
        Topic topic= topicService.getByID(id);
        List<Key> refKeys=keyService.getListByTopicID(topic.getId());
        topic.setKeys(refKeys);
        return topic;
    }

    @RequestMapping("/deletetopic")
    @ResponseBody
    public int deletetopic(int id)
    {
        return topicService.delete(id);
    }

    @RequestMapping("/deletekey")
    @ResponseBody
    public int deleteKey(int id)
    {
        return keyService.delete(id);
    }

    @RequestMapping("/gettestlist")
    @ResponseBody
    public Layui getTestList(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                             @RequestParam(value = "limit", defaultValue = "10") int pageSize){
        var list= testSerice.getlist(pageIndex,pageSize);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    @RequestMapping("/deletetest")
    @ResponseBody
    public int deleteTest(int id){
        //删除reply
        replyService.deleteByTestID(id);
        //删除keys
        var topicList=topicService.getListByTestID(id);
        for(Topic t:topicList)
        {

            keyService.deleteByTopicID(t.getId());
        }
        topicService.deleteByTestID(id);
        return testSerice.delete(id);
    }


    @RequestMapping("/updateTestSequence")
    @ResponseBody
    public int updateTestSequence(int sequence,int id){
        return testSerice.updateSequence(sequence,id);
    }

    @RequestMapping("/getTest")
    @ResponseBody
    public Test getTest(int id)
    {
        return testSerice.getByID(id);
    }

    @RequestMapping("/saveReplies")
    @ResponseBody
    public String saveReplies(String replies,int testid,String ip)
    {
        var replyList=JSON.parseArray(replies, Reply.class);
        UUID uuid = UUID.randomUUID();
        var successNumber=0;
        for(Reply r:replyList)
        {
            var reply=new Reply();
            reply.setTestid(testid);
            reply.setTopicid(r.getTopicid());
            reply.setKeyid(r.getKeyid());
            reply.setContent(r.getContent());
            reply.setCreatetime(new Date());
            reply.setCreator(getCurrentUser().getUserUid());
            reply.setCreatorname(getCurrentUser().getName());
            reply.setIp(ip);
            reply.setIsright(r.getIsright());
            reply.setGuid(uuid.toString());
            int i=replyService.insert(reply);
            if(i>0)
                successNumber++;
        }
        if(successNumber==replyList.size()) {
            var test=testSerice.getByID(testid);
            var returnurl=test.getReturnurl();
            return returnurl;
        }
        else
            return "";
    }

    @RequestMapping("/updateTestStauts")
    @ResponseBody
    public int updateTestStauts(int id,int status)
    {
        var test=testSerice.getByID(id);
        test.setTeststatus(status);
        return testSerice.save(test);
    }

    @RequestMapping("/getreplylist")
    @ResponseBody
    public Layui getReplyList(@RequestParam(value = "page", defaultValue = "1") int pageIndex,
                              @RequestParam(value = "limit", defaultValue = "10") int pageSize,int testid){
        var list= replyService.getReplyListByTestID(pageIndex,pageSize,testid);
        return Layui.data(list.getTotalCount(),list.getList(),"OK");
    }

    @RequestMapping("/getTestName")
    @ResponseBody
    public String getTestName(int id)
    {
        var test=testSerice.getByID(id);
        return test.getTestname();
    }


    @RequestMapping("/getTestAnswer")
    @ResponseBody
    public Test getTestAnswer(int testid,@RequestParam(value="replyguid") String guid)
    {
        Test test=testSerice.getByID(testid);
        List<Topic> topicList=topicService.getListByTestID(testid);
        for(Topic t:topicList)
        {
            int topicid=t.getId();
            //再topic的replyContent属性存问答题答案、允许其他答案的textbox内容
            var contentReply=replyService.getContentByTopicIDAndGuid(topicid,guid);
            if(contentReply!=null)
            {
                t.setReplycontent(contentReply.getContent());
            }
            List<Key> keys=keyService.getListByTopicID(topicid);
            for(Key key :keys)
            {
                //只能获取单选或者多选，reply中有keyid的数据
                var selectedreply=replyService.getByKeyIDAndGuid(key.getId(),guid);
                if(selectedreply!=null)
                    key.setSelected("checked");
                else
                    key.setSelected("");
            }
            t.setKeys(keys);
        }
        test.setTopics(topicList);
        return test;
    }

    @RequestMapping("/getAnswerCount")
    @ResponseBody
    public Test getAnswerCount(int testid){
        Test test=testSerice.getByID(testid);
        List<Topic> topicList=topicService.getListByTestID(testid);
        for(Topic t:topicList)
        {
            int topicid=t.getId();
            //再topic的replyContent属性存问答题答案、允许其他答案的textbox内容
            var contentReplyList=replyService.getContentByTopicID(topicid);
            if(contentReplyList.size()>0)
            {
                t.setContentreplylist(contentReplyList);
            }
            List<Key> keys=keyService.getListByTopicID(topicid);
            for(Key key :keys)
            {
                var selectedNumber=replyService.getCountByKeyID(key.getId(),topicid);
                key.setSelectedcount(selectedNumber);
            }
            t.setKeys(keys);
            //获取这个topic所有的选择个数作为总数,包括允许其他答案
            var checkReplyTotalNum=replyService.getCheckReplyCountByTopicID(topicid);
            t.setSelectedtotalcount(checkReplyTotalNum);
            //获取这个topic允许其他选项的reply总数
            var othernumber=replyService.getCountByKeyID(0,topicid);
            t.setSelectedothercount(othernumber);
        }
        test.setTopics(topicList);
        return test;
    }

}
