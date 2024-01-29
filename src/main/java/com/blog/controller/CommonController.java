package com.blog.controller;

import com.blog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${resource.path}")
    private String basePath;

    @PostMapping("/upload")
    //file是个临时文件，我们在断点调试的时候可以看到，但是执行完整个方法之后就消失了
    public Result<String> upload(MultipartFile file) {
        log.info("获取文件：{}", file.getOriginalFilename());
        //判断一下当前目录是否存在，不存在则创建
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        // 获取项目所在的文件夹
        String jarPath = jarFile.getParentFile().getPath();
        File dir = new File(jarPath +  File.separator + basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //获取一下传入的原文件名
        String originalFilename = file.getOriginalFilename();
        //我们只需要获取一下格式后缀，取子串，起始点为最后一个.
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //为了防止出现重复的文件名，我们需要使用UUID
        String fileName = UUID.randomUUID() + suffix;
        try {
            //我们将其转存到我们的指定目录下
            String fileAbsPath = jarPath +  File.separator + basePath + File.separator + fileName;
            log.info("文件写入绝对路径 " + fileAbsPath);
            File newFile = new File(fileAbsPath);
            file.transferTo(newFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将文件名返回给前端，便于后期的开发
        return Result.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        FileInputStream fis = null;
        ServletOutputStream os = null;
        try {
            ApplicationHome home = new ApplicationHome(getClass());
            File jarFile = home.getSource();
            // 获取项目所在的文件夹
            String jarPath = jarFile.getParentFile().getPath();
            String fileAbsPath = jarPath +  File.separator + basePath + File.separator + name;
            log.info("文件读取绝对路径 " + fileAbsPath);
            fis = new FileInputStream(fileAbsPath);
            os = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1)
                os.write(buffer, 0, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {

                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
