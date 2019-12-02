package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Test {
    Integer id;
   String testname;
    Integer teststatus;
    Date createtime;
    String createusername;
    Date finishtime;
    Integer testtype;
    Integer isneedrealname;
    String description;
    Integer ispublish;
    String deptid;
    Integer sequence;
    String code;
    Integer answerfrequency;
    Integer answerobject;
    String returnurl;
    String iplimit;
    String iplimitstart;
    String iplimitend;
    String testuserids;
    List<Topic> topics;


}


