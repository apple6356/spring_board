package org.seo.board.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**", "/cover_image/**") // /files/ 로 D:/files/ 에 접근 가능
                .addResourceLocations("file:///D:/files/", "file:///D:/cover_image/");

        // registry.addResourceHandler("/cover_image/**") // /files/ 로 D:/files/ 에 접근 가능
        //         .addResourceLocations("file:///D:/cover_image/");
    }
}
