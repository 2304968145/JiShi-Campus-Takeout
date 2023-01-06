package com.jishi.Controller;

import com.jishi.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

//通用的一个Controller层，包含图片上传、下载等通用功能
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${ji-shi.dir}")
     String  fileDir;

    @PostMapping("/upload")
    public  R<String>  upload(MultipartFile file) throws IOException {
        //得到图片原始名字（后缀），重新生成名字存入服务器防止重复
        String fileName =file.getOriginalFilename();

        String suffix = fileName.substring(fileName.lastIndexOf("."));

        //先创建目标目录路径,会自动判断是否存在
        new File(fileDir).mkdirs();

        fileName =UUID.randomUUID().toString()+suffix;
        file.transferTo( new File(fileDir,fileName));

        //这里返回文件名，用于前端上传后再传回来名字查询图片回显
        return R.success(fileName) ;
    }

    //用于上传图片后进行图片回显
    @GetMapping("/download")
    public  void  download(String name, HttpServletResponse response) throws IOException {

        File pictureFile = new File(fileDir,name);
        FileInputStream fileInputStream = new FileInputStream(pictureFile);
        //创建response输出流，前端图片src只要指定这个访问地址，
        //response输出流直接写出数据，图片就能展示到前端
        ServletOutputStream outputStream = response.getOutputStream();
        //上传的图片有多种样式，这里只指定jpeg没关系吗
        response.setContentType("image/jpeg");

        byte[] bytes = new byte[1024];
        int len=0;
        while ((len=fileInputStream.read(bytes))!=-1){

            outputStream.write(bytes,0,len);

        }

        outputStream.close();
        fileInputStream.close();
    }

}
