package org.seo.board.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "author", nullable = false)
    private String author;

    // 조회수
    @Column(name = "hits")
    private Long hits;

    // 추천수
    @Column(name = "recommend")
    private Long recommend;

    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY) // 일 대 다, cascade 글(부모) 삭제되면 댓글(자식)도 삭제
    @JsonManagedReference // 직렬화 시 사용되며, 주로 부모 엔티티에 적용, 무한루프 방지
    private List<Comment> comments;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<BoardFile> files;

    @Builder // builder 패턴
    public Board(String author, String title, String content) {
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
