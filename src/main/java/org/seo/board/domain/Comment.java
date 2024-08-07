package org.seo.board.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "recommend")
    private Long recommend;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY) // 다 대 일
    @JsonBackReference // 역직렬화 시 사용되며, 주로 자식 엔티티에 적용, 무한루프 방지
    private Board board;

    @Builder
    public Comment(Board board, String author, String content) {
        this.board = board;
        this.author = author;
        this.content = content;
        this.recommend = 0L;
    }

    public void update(String content) {
        this.content = content;
    }

    // 추천 +1
    public void updateRecommend() {
        this.recommend += 1;
    }
}
