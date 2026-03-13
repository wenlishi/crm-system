package com.crm.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CRM 系统启动类
 * 
 * @author wenlishi
 * @since 2026-03-14
 */
@SpringBootApplication
@MapperScan("com.crm.system.**.mapper")
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("====================================");
        System.out.println("✅ CRM 系统启动成功！");
        System.out.println("📍 访问地址：http://localhost:8080/api");
        System.out.println("📚 项目文档：https://github.com/wenlishi/crm-system");
        System.out.println("====================================");
    }
}
