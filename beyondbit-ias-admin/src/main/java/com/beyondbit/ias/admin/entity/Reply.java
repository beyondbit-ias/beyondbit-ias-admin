package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Reply {
    Integer id;
    Integer testid;
    Integer topicid;
    Integer keyid;
    String content;
    Date createtime;
    String creator;
    String creatorname;
    Integer status;
    String ip;
    String guid;
    Integer isright;
    Integer sex;
    Integer age;
    String education;
    String occupation;
    String revenue;
}
