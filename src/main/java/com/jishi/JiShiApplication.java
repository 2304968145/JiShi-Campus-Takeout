package com.jishi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.annotation.WebServlet;


@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class JiShiApplication {

    public static void main(String[] args) {

        SpringApplication.run(JiShiApplication.class,args);
        log.info("程序启动成功！");
    }
}
