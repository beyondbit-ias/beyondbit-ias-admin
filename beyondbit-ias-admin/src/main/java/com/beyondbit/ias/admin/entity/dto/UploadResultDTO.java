package com.beyondbit.ias.admin.entity.dto;

import lombok.Data;

@Data
public class UploadResultDTO {
    public boolean success;

    public String message;

    public Integer fileID;

    public String fileFullPath;
}
