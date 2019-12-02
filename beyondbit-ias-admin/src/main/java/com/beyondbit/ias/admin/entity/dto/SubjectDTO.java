package com.beyondbit.ias.admin.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SubjectDTO {
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

    //附件
    String uploadFiles;
}
