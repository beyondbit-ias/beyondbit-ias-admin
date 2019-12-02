package com.beyondbit.ias.admin.controller;

import com.beyondbit.ias.admin.entity.Attachment;
import com.beyondbit.ias.admin.entity.dto.UploadResultDTO;
import com.beyondbit.ias.admin.entity.dto.UploadResultStatusDTO;
import com.beyondbit.ias.admin.entity.dto.UploadResultVoDTO;
import com.beyondbit.ias.admin.param.UploadParam;
import com.beyondbit.ias.admin.service.AttachmentService;
import com.beyondbit.ias.admin.utils.UploadConstants;
import com.beyondbit.ias.core.base.BaseController;
import com.beyondbit.ias.core.common.Layui;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("attachment")
public class AttachmentController extends BaseController {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AttachmentService attachmentService;

    public static String fileUploadPath ;
    @Value("${upload.fileUploadPath}")
    public void setFileUploadPath(String fileUploadUrl){
        fileUploadPath=fileUploadUrl;
    }

    /**
     * 秒传判断，断点判断
     *
     * @return
     */
    @RequestMapping(value = "checkFileMd5", method = RequestMethod.POST)
    @ResponseBody
    public Object checkFileMd5(String md5) throws IOException {
        Object processingObj = stringRedisTemplate.opsForHash().get(UploadConstants.FILE_UPLOAD_STATUS, md5);
        if (processingObj == null) {
            return new UploadResultVoDTO(UploadResultStatusDTO.NO_HAVE);
        }
        String processingStr = processingObj.toString();
        boolean processing = Boolean.parseBoolean(processingStr);
        String value = stringRedisTemplate.opsForValue().get(UploadConstants.FILE_MD5_KEY + md5);
        if (processing) {
            return new UploadResultVoDTO(UploadResultStatusDTO.IS_HAVE, value);
        } else {
            File confFile = new File(value);
            byte[] completeList = FileUtils.readFileToByteArray(confFile);
            List<String> missChunkList = new LinkedList<>();
            for (int i = 0; i < completeList.length; i++) {
                if (completeList[i] != Byte.MAX_VALUE) {
                    missChunkList.add(i + "");
                }
            }
            return new UploadResultVoDTO<>(UploadResultStatusDTO.ING_HAVE, missChunkList);
        }
    }

    /**
     * 上传文件
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity fileUpload(UploadParam param, HttpServletRequest request) {
        String creatorID = super.getCurrentUser().getUserUid();
        String creatorName = super.getCurrentUser().getName();

        UploadResultDTO res = null;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            try {
                // 方法1
                //attachmentService.uploadFileRandomAccessFile(param);
                // 方法2 这个更快点
                res =  attachmentService.uploadFileByMappedByteBuffer(param,creatorID,creatorName);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("文件上传失败。"+ param.toString());
            }
        }
        return ResponseEntity.ok(res);
    }


    @CrossOrigin(origins = "http://localhost:8094")
    @RequestMapping("/manage")
    public String manage(String uploadFiles, Model model,HttpServletResponse response){
        if (uploadFiles!=null && uploadFiles.length()>0){
            int[] arrID = Arrays.stream(uploadFiles.split(",")).mapToInt(Integer::valueOf).toArray();
            Map condition = new HashMap();
            condition.put("arrID",arrID);
            //condition.put("isDelete",0);
            List<Attachment> list = this.attachmentService.queryList(condition);
            model.addAttribute("fileList",list);
            model.addAttribute("uploadFiles",uploadFiles);
            model.addAttribute("uploadFilesLength",list.size());
        }else{
            model.addAttribute("uploadFilesLength",0);
        }
        response.addHeader("x-frame-options","SAMEORIGIN");
        return "attachment/manage.html";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Layui delete(Integer fileID){
        //todo:不安全，最好增加一个UUID字段，不然通过接口从1删除到99999999999
        try{
            int res = this.attachmentService.delete(new int[]{fileID});
            return Layui.data(res, null, "删除成功！");
        }catch(Exception ex){
            ex.printStackTrace();
            return Layui.data(-1, null, "删除失败！" + ex.getMessage());
        }
    }
}
