package com.bwie;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.bwie.mapper"})
public class ServerWygUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerWygUploadApplication.class, args);
    }

}
