package com.beyondbit.ias.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Webpart {
     Integer id;
     String useruid;
     String boxid;
     Integer sequence;
     Date updatetime;
     String modularcode;
     String url;
     Integer isdelete;

}
