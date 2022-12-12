package com.bwie.controller;

import com.bwie.mapper.TbUploadFileMapper;
import com.bwie.pojo.TbUploadFile;
import com.bwie.vo.FileUploadEndVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.UUID;

/**
 * @Author Sun
 * @Version 1.0
 * @description: TODO
 * @Date 2022/12/10 14:05:59
 */
@RequestMapping("/upload")
@Controller
@CrossOrigin
@Slf4j
public class UploadController {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private TbUploadFileMapper tbUploadFileMapper;


    final String uploadDir = "D:\\javatest\\temp\\upload";

    @GetMapping("/index")
    public ModelAndView index(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @PostMapping("/chunked-upload")
    @ResponseBody
    public String chunkedUpload(HttpServletRequest request){
        Enumeration<String> parameterNames = request.getParameterNames();
        log.info(parameterNames.toString());
        return "success";
    }

    /**
     * 接收上传的分片文件
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

    @RequestMapping("/fileupload")
    @ResponseBody
    public String doulefileupload(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String lastModifiedDate = request.getParameter("lastModifiedDate");
        String size = request.getParameter("size");
        String chunks = request.getParameter("chunks");
        String chunk = request.getParameter("chunk");

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");

        long size1 = file.getSize();
        String originalFilename = file.getOriginalFilename();
        System.out.println(""+size1+originalFilename);

        // 更新上传文件信息
        String uploadFileKey = "UPLOAD_" + id + "_" + size;
        log.info("uploadFileKey="+uploadFileKey);
        redisTemplate.opsForHash().put(uploadFileKey, "id", id);
        redisTemplate.opsForHash().put(uploadFileKey, "name", name);
        redisTemplate.opsForHash().put(uploadFileKey, "type", type);
        redisTemplate.opsForHash().put(uploadFileKey, "lastModifiedDate", lastModifiedDate);
        redisTemplate.opsForHash().put(uploadFileKey, "size", size);
        redisTemplate.opsForHash().put(uploadFileKey, "chunks", chunks);
        redisTemplate.opsForHash().put(uploadFileKey, "chunk", chunk);

        // 准备目录
        String storeDir = uploadDir + File.separator + uploadFileKey;
        log.info("storeDir1="+storeDir);

        File fileFolder = new File(storeDir);
        if (!fileFolder.exists()) {
            boolean mkdirs = fileFolder.mkdirs();
            log.info("准备工作,创建文件夹,fileFolderPath:{},mkdirs:{}", storeDir, mkdirs);
        }

        // 存储文件
        String tempName = UUID.randomUUID().toString();
        String fileName = storeDir + File.separator + tempName;
        log.info("fileName1="+fileName);


        File dest = new File(fileName);
        file.transferTo(dest);

        // 上传成功，设置成功标识
        String storeFileKey = "STORE_" + id + "_" + size;
        log.info("storeFileKey1={},chunk={}", storeFileKey,chunk);
        redisTemplate.opsForHash().put(storeFileKey, chunk, fileName);

        return name;
    }

    /**
     * 所有分片上传成功，开始问卷合并
     * @param fileUploadEndVo
     * @return
     */

    @PostMapping(value = "/uploadFileEnd")
    @ResponseBody
    public String uploadFileEnd(@RequestBody FileUploadEndVo fileUploadEndVo) {
        System.out.println(""+fileUploadEndVo.toString());

        try {
            // 检查文件是否全部上传完成
            String uploadFileKey = "UPLOAD_" + fileUploadEndVo.getId() + "_" + fileUploadEndVo.getSize();
            log.info("uploadFileKey="+uploadFileKey);

            String chunks = (String)redisTemplate.opsForHash().get(uploadFileKey, "chunks");
            log.info("chunks2="+chunks);


            Boolean isFinish = true;
            String storeFileKey = "STORE_" + fileUploadEndVo.getId() + "_" + fileUploadEndVo.getSize();

            for (int index = 0; index < Integer.valueOf(chunks); index++) {

                log.info("storeFileKey2="+storeFileKey);

                if(!redisTemplate.opsForHash().hasKey(storeFileKey, ""+index)) {
                    isFinish = false;
                    log.error("storeFileKey={},key={}", storeFileKey, ""+index);
                    break;
                }
            }
            if(!isFinish) {
                return "ERROR";
            }

            // 合并文件
            String storeFile = uploadDir + File.separator + UUID.randomUUID().toString() + "." + fileUploadEndVo.getExt();
            File resultFile = new File(storeFile);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(resultFile));
            int bufSize = 1024*4;
            byte[] buffer = new byte[bufSize];

            for (int index = 0; index < Integer.valueOf(chunks); index++) {
                String tempFile = (String)redisTemplate.opsForHash().get(storeFileKey, ""+index);

                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(tempFile));
                int readcount;
                while ((readcount = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readcount);
                }
                inputStream.close();
                Files.delete(Paths.get(tempFile));
            }
            outputStream.close();

            // 删除临时文件：直接删除目录
            String storeDir = uploadDir + File.separator + uploadFileKey;
            Files.deleteIfExists(Paths.get(storeDir));

            // 存入数据库，并删除缓存文件
            redisTemplate.delete(uploadFileKey);
            redisTemplate.delete(storeFileKey);


            //存入Mysql
            TbUploadFile tbUploadFile = new TbUploadFile();

            String fileName = fileUploadEndVo.getName();
            tbUploadFile.setFileName(fileName);
            tbUploadFile.setFileExt(fileUploadEndVo.getExt());
            tbUploadFile.setFileSize(Long.valueOf(fileUploadEndVo.getSize()));
            tbUploadFile.setFileType(fileUploadEndVo.getType());
            tbUploadFile.setStoreName(fileName.substring(fileName.lastIndexOf("//")+1,fileName.length()));
            tbUploadFileMapper.insert(tbUploadFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return fileUploadEndVo.getName();
    }

}
