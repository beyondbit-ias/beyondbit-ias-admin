package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Topic {
    Integer id;
    Integer testid;
    String name;
    Integer type;
    Integer status;
    Date createtime;
    String creatorid;
    String content;
    Integer colnumber;
    Integer isallowotherkey;
    Integer ismustanswer;
    Integer textareaheight;
    Integer textlength;
    String allowotherkeyname;
    Integer sequence;
    //keylists
    String keylists;

    List<Key> keys;

    //卷答题情况，这个topic下面的textbox或者textarea内容
    // 如果这个topic是选择题，那么content为允许其他选项的content
    // 如果是问答题，那么就是textarea内容
    String replycontent;

    //用于分析，放所有文本的replay,包括其他选项和问答题
    List<Reply> contentreplylist;

    //用于分析，放其他选项的percent
    int selectedothercount;
    //用于分析，这个topic的所有选项（包括允许其他选项）选择总和
    int selectedtotalcount;


}
