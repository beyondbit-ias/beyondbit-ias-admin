package com.beyondbit.ias.admin.service.impl;

import com.beyondbit.ias.admin.dao.AttachmentMapper;
import com.beyondbit.ias.admin.entity.Attachment;
import com.beyondbit.ias.admin.param.UploadParam;
import com.beyondbit.ias.admin.entity.dto.UploadResultDTO;
import com.beyondbit.ias.admin.service.AttachmentService;
import com.beyondbit.ias.admin.utils.FileMD5Util;
import com.beyondbit.ias.admin.utils.UploadConstants;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    AttachmentMapper attachmentMapper;
    // 保存文件的根目录
    private Path rootPath;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //这个必须与前端设定的值一致
    @Value("${upload.chunkSize}")
    private long CHUNK_SIZE;

    @Value("${upload.fileUploadPath}")
    private String finalDirPath;

    @Autowired
    public AttachmentServiceImpl(@Value("${upload.fileUploadPath}") String location) {
        this.rootPath = Paths.get(location);
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootPath);
            stringRedisTemplate.delete(UploadConstants.FILE_UPLOAD_STATUS);
            stringRedisTemplate.delete(UploadConstants.FILE_MD5_KEY);
        } catch (FileAlreadyExistsException e) {
            System.out.println("附件上传文件夹已经存在！");
        } catch (IOException e) {
            System.out.println("初始化附件上传文件夹失败！" + e);
        }
    }

    @Override
    public void uploadFileRandomAccessFile(UploadParam param) throws IOException {
        String fileName = param.getName();
        String tempDirPath = finalDirPath + param.getMd5();
        String tempFileName = fileName + "_tmp";
        File tmpDir = new File(tempDirPath);
        File tmpFile = new File(tempDirPath, tempFileName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        long offset = CHUNK_SIZE * param.getChunk();
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write(param.getFile().getBytes());
        // 释放
        accessTmpFile.close();

        boolean isOk = checkAndSetUploadProgress(param, tempDirPath);
        if (isOk) {
            boolean flag = renameFile(tmpFile, fileName);
            System.out.println("upload complete !!" + flag + " name=" + fileName);
        }
    }

    @Override
    public UploadResultDTO uploadFileByMappedByteBuffer(UploadParam param,String creatorID,String creatorName) throws IOException {
        String fileName = param.getName();
        String uploadDirPath = finalDirPath + param.getMd5();
        String tempFileName = fileName + "_tmp";
        File tmpDir = new File(uploadDirPath);
        File tmpFile = new File(uploadDirPath, tempFileName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        RandomAccessFile tempRaf = new RandomAccessFile(tmpFile, "rw");
        FileChannel fileChannel = tempRaf.getChannel();

        //写入该分片数据
        long offset = CHUNK_SIZE * param.getChunk();
        byte[] fileData = param.getFile().getBytes();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
        mappedByteBuffer.put(fileData);
        // 释放
        FileMD5Util.freedMappedByteBuffer(mappedByteBuffer);
        fileChannel.close();

        boolean isOk = checkAndSetUploadProgress(param, uploadDirPath);
        UploadResultDTO res = new UploadResultDTO();
        if (isOk) {
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
            String newFileName = java.util.UUID.randomUUID().toString() + "." + fileExtension;
            String pathName = tmpFile.getParent() + File.separatorChar + newFileName;
            String fileType = Files.probeContentType(Paths.get(pathName));
            File newFile = new File(pathName);
            tmpFile.renameTo(newFile);

            System.out.println("上传完成！文件名为" + fileName);

            //附件入库
            Attachment entity = new Attachment();
            //entity.setReftableID();
            //entity.setRefTableName("");
            //entity.setDisplayName();
            entity.setName(fileName);
            entity.setExtension(fileExtension);
            entity.setRelativePath(param.getMd5()+File.separatorChar);
            entity.setInternalName(newFileName);
            entity.setContentType(fileType);
            entity.setLength(newFile.length());
            entity.setIsDelete(1);
            this.insert(entity,creatorID,creatorName);

            res.setSuccess(true);
            res.setMessage("上传成功！");
            res.setFileID(entity.getId());
            res.setFileFullPath("/portal/uploads/"+param.getMd5()+File.separatorChar+newFileName);
            return res;
        } else {
            res.setSuccess(false);
            return res;
        }
    }

    /**
     * 检查并修改文件上传进度
     *
     * @param param
     * @param uploadDirPath
     * @return
     * @throws IOException
     */
    private boolean checkAndSetUploadProgress(UploadParam param, String uploadDirPath) throws IOException {
        String fileName = param.getName();
        File confFile = new File(uploadDirPath, fileName + ".conf");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");
        //把该分段标记为 true 表示完成
        System.out.println("set part " + param.getChunk() + " complete");
        accessConfFile.setLength(param.getChunks());
        accessConfFile.seek(param.getChunk());
        accessConfFile.write(Byte.MAX_VALUE);

        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
            System.out.println("check part " + i + " complete?:" + completeList[i]);
        }

        accessConfFile.close();
        if (isComplete == Byte.MAX_VALUE) {
            stringRedisTemplate.opsForHash().put(UploadConstants.FILE_UPLOAD_STATUS, param.getMd5(), "true");
            stringRedisTemplate.opsForValue().set(UploadConstants.FILE_MD5_KEY + param.getMd5(), uploadDirPath + "/" + fileName);
            return true;
        } else {
            if (!stringRedisTemplate.opsForHash().hasKey(UploadConstants.FILE_UPLOAD_STATUS, param.getMd5())) {
                stringRedisTemplate.opsForHash().put(UploadConstants.FILE_UPLOAD_STATUS, param.getMd5(), "false");
            }
            if (stringRedisTemplate.hasKey(UploadConstants.FILE_MD5_KEY + param.getMd5())) {
                stringRedisTemplate.opsForValue().set(UploadConstants.FILE_MD5_KEY + param.getMd5(), uploadDirPath + "/" + fileName + ".conf");
            }
            return false;
        }
    }


    public boolean renameFile(File toBeRenamedFile, String newFileName) {
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamedFile.exists() || toBeRenamedFile.isDirectory()) {
            System.out.println("文件不存在: " + toBeRenamedFile.getName());
            return false;
        }
        String parentPath = toBeRenamedFile.getParent();
        File newFile = new File(parentPath + File.separatorChar + newFileName);
        return toBeRenamedFile.renameTo(newFile);
    }

    public int insert(Attachment entity,String creatorID,String creatorName){
        entity.setCreatorID(creatorID);
        entity.setCreatorName(creatorName);
        entity.setCreatorTime(new Date());
        return this.attachmentMapper.insert(entity);
    }

    public int updateRef(int[] arrID,String refTableID,String refTableName,Integer isDelete){
        return this.attachmentMapper.updateRef(arrID,refTableID,refTableName,isDelete);
    }

    public List<Attachment> queryList(Map condition){
        return this.attachmentMapper.queryList(condition);
    }

    public int delete(int[] idList){
        return this.attachmentMapper.delete(idList);
    }
}
