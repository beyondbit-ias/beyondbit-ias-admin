package com.beyondbit.ias.admin.entity.dto;

import lombok.Data;

@Data
public class UploadResultVoDTO<T> {

    private UploadResultStatusDTO status;

    private String msg;

    private T data;

    public UploadResultVoDTO(UploadResultStatusDTO status) {
        this(status, status.getReasonPhrase(), null);
    }

    public UploadResultVoDTO(UploadResultStatusDTO status, T data) {
        this(status, status.getReasonPhrase(), data);
    }

    public UploadResultVoDTO(UploadResultStatusDTO status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
