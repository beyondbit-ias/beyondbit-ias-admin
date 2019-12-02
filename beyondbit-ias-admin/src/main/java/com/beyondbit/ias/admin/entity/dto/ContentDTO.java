package com.beyondbit.ias.admin.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ContentDTO {
    Integer id;
    String uuid;
    String title;
    String subTitle;
    String keyWords;
    String url;
    String source;
    String Abstract;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date endTime;
    String content;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    String author;
    String isDelete;

    String isVisible;
    Integer sequence;
    String contentUUID;
    String subjectUUID;
    String subjectName;
    String contentSubjectUUID;

    String subjectUUIDList;

    String uploadFiles;
}
