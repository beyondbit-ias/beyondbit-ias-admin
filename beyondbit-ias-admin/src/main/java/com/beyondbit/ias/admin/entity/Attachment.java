package com.beyondbit.ias.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Attachment {
    Integer id;
    String refTableID;
    String refTableName;
    String displayName;
    String name;
    String extension;
    String relativePath;
    String internalName;
    String contentType;
    Long length;
    String content;
    Integer typeCode;
    Integer persistentType;
    String creatorID;
    String creatorName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date creatorTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;
    Integer sequence;
    Integer isDelete;
}
