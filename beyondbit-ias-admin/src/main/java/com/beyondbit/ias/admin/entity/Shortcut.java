package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Shortcut {
    Integer id;
    String useraccount;
    String modularname;
    String modularcode;
    String modularurl;
    Date createtime;


}
