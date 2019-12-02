package com.beyondbit.ias.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 信息发布-内容栏目关联 gjs 编写 2019-08-27
 */
@Data
public class ContentSubject {
    Integer id;
    String uuid;
    String contentUUID;
    String subjectUUID;
    Integer viewCount;
    String isVisible;
    Integer sequence;

    String reviewStatus;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date reviewTime;
    String reviewer;
    String reviewOpinion;
    String isDelete;
}
