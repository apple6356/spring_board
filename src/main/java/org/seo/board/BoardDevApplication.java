package org.seo.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // createTime, updateTime 자동 업데이트
@SpringBootApplication
public class BoardDevApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoardDevApplication.class, args);
    }
}
