package com.beyondbit.ias.admin.service;

import com.beyondbit.ias.admin.entity.Attachment;
import com.beyondbit.ias.admin.param.UploadParam;
import com.beyondbit.ias.admin.entity.dto.UploadResultDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AttachmentService {
    /**
     * 初始化方法
     */
    void init();

    /**
     * 上传文件方法1
     *
     * @param param
     * @throws IOException
     */
    void uploadFileRandomAccessFile(UploadParam param) throws IOException;

    /**
     * 上传文件方法2
     * 处理文件分块，基于MappedByteBuffer来实现文件的保存
     *
     * @param param
     * @throws IOException
     */
    UploadResultDTO uploadFileByMappedByteBuffer(UploadParam param,String creatorID,String creatorName) throws IOException;

    public int insert(Attachment entity,String creatorID,String creatorName);

    public int updateRef(int[] arrID,String refTableID,String refTableName,Integer isDelete);

    public List<Attachment> queryList(Map condition);

    public int delete(int[] idList);
}
