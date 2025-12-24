package com.nvkproject3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files từ thư mục uploads/ (ngoài project)
        String uploadPath = new File("uploads/products/").getAbsolutePath() + "/";

        System.out.println("=== WebConfig ===");
        System.out.println("Serving uploads from: " + uploadPath);

        registry
                .addResourceHandler("/uploads/products/**")
                .addResourceLocations("file:" + uploadPath);

        // Vẫn serve static resources bình thường
        registry
                .addResourceHandler("/images/products/**")
                .addResourceLocations("classpath:/static/images/products/");
    }
}