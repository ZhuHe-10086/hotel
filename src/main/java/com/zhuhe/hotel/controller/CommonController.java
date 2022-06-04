package com.zhuhe.hotel.controller;

import com.zhuhe.hotel.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${images.path}")
    private String imagePath;
    /**
     * 这个方法的参数名需要与前端的name属性保持一致
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是一个临时文件，需要对他进行转存，否则请求完毕file就会被删除
        String filename = null;
        try {
            String[] split = file.getOriginalFilename().split("[.]");
            String last = split[split.length-1];
            filename  = System.currentTimeMillis()+""+(Math.random()*10)+"."+last;
            //判断路径是否存在,不存在创建一个
            File f = new File(imagePath);
            if (!f.exists()){
                f.mkdir();
            }
            //转存
            file.transferTo(new File(imagePath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(file.toString());
        return R.success(filename);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(imagePath+name));
            outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
