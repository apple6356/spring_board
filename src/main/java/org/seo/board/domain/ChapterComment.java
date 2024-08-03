package org.seo.board.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
public class ChapterComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 댓글 작성자
    @Column(name = "author", nullable = false)
    private String author;

    // 댓글 내용
    @Column(name = "content", nullable = false)
    private String content;

    // 댓글 추천수
    @Column(name = "recommend")
    private Long recommend;

    // 댓글 작성 시간
    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 회차와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Chapter chapter;

    @Builder
    public ChapterComment(Chapter chapter, String author, String content) {
        this.author = author;
        this.content = content;
        this.chapter = chapter;
        this.recommend = 0L;
    }

    // 내용 변경
    public void update(String content) {
        this.content = content;
    }

    // 추천 +1
    public void updateRecommend() {
        this.recommend += 1;
    }

}
