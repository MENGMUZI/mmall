package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author : mengmuzi
 * create at:  2019-03-04  20:04
 * @description:
 */


@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


    public String upload(MultipartFile file,String path){ //文件和路径
        String fileName = file.getOriginalFilename();//获取文件的名字
        //扩展名
        //abc.jpg
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);//获取扩展名，例如文件名为：abc.jpg
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;//上传文件的名字，随机字符串+文件扩展名
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);//创建目录
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);//创建文件（假设在upload文件夹下）


        try {
            file.transferTo(targetFile);
            logger.info("文件已经上传成功了");
            //文件已经上传成功了


            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //logger.info("已经上传到ftp服务器上");
            //已经上传到ftp服务器上

            targetFile.delete();//上传成功之后，就删除创建的的文件，如upload下面的文件
            logger.info("将本地的信息已经删除");

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        //A:abc.jpg
        //B:abc.jpg
        return targetFile.getName();
    }

}

