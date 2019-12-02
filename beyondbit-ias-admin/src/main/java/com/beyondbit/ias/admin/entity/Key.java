package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Key {
    Integer id;
    Integer topicid;
    Integer isrightkey;
    Integer status;
    String content;
    Date createtime;
    String creatorid;
    Integer sequence;
    Integer value;
    Integer isdefaultkey;
//问卷答题情况  多选单选这个key答题者是否选择，选择为checked，没有选择为''；
    String selected;

    //分析界面
    int selectedcount ;


}
