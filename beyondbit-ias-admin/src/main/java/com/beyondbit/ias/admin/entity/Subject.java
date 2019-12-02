package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;

/**
 * 信息发布-栏目 gsj 编写 2019-08-27
 */
@Data
public class Subject {
    Integer id;
    String uuid;
    String name;
    String parentUUID;
    String url;
    String code;
    String isVisible;
    String subjectDESC;
    Date createTime;
    String creater;
    Integer sequence;
    String isReview;
    String reviewers;
    String isDelete;
}
