package org.seo.board.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Chapter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 소설 각 화(에피소드) 제목
    @Column(name = "title", nullable = false)
    private String title;

    // 각 에피소드 내용
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    // 작가
    @Column(name = "author", nullable = false)
    private String author;

    // 몇 화인지
    @Column(name = "chapter", nullable = false)
    private Long chapter;

    // 조회수
    @Column(name = "hits")
    private Long hits;

    // 추천수
    @Column(name = "recommend")
    private Long recommend;

    // 작성 시간
    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 수정 시간
    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // novel 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    private Novel novel;

    @Builder // builder 패턴
    public Chapter(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.hits = 0L; // 조회수 0 초기화
        this.recommend = 0L; // 추천수 0 초기화
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 추천 +1
    public void updateRecommend() {
        this.recommend += 1;
    }

}
