package com.easyphoto;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.easyphoto.mapper")
public class EasyPhotoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyPhotoApplication.class, args);
    }
}
