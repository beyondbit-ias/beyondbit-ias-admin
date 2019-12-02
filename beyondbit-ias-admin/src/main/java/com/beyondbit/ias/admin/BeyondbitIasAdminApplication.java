package com.beyondbit.ias.admin;

import com.beyondbit.ias.admin.service.AttachmentService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@MapperScan(basePackages ={"com.beyondbit.ias.core.dao","com.beyondbit.ias.admin.dao"})
@EnableFeignClients(basePackages = "com.beyondbit.bua.client")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.beyondbit.ias.core", "com.beyondbit.ias.admin"})
public class BeyondbitIasAdminApplication extends SpringBootServletInitializer {
    /**
     * main方法
     *
     * @param args
     */
    public static void main(String[] args) {

        SpringApplication.run(BeyondbitIasAdminApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BeyondbitIasAdminApplication.class);
    }

    @Bean
    CommandLineRunner init(final AttachmentService attachmentService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                attachmentService.init();
            }
        };
    }
}
