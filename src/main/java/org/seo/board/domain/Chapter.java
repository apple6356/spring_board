package org.seo.board.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // createtime, updatetime 저장
public class Chapter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 회차 제목
    @Column(name = "title", nullable = false)
    private String title;

    // 회차 내용
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    // 작가
    @Column(name = "author", nullable = false)
    private String author;

    // 회차
    @Column(name = "episode", nullable = false)
    private Long episode;

    // 조회수
    @Column(name = "hits")
    private Long hits;

    // 작성 시간
    @CreatedDate // 엔티티가 생성될 때 생성 시간 저장
    @Column(name = "created_at", updatable = false) // 처음에만 저장되기 때문에 update 막기
    private LocalDateTime createdAt;

    // 수정 시간
    @LastModifiedDate // 엔티티가 수정될 때 수정 시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // novel 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // 역직렬화 시 사용되며, 주로 자식 엔티티에 적용, 무한루프 방지
    private Novel novel;

    // 댓글과 다대일 관계
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChapterComment> chapterComments;

    @Builder // builder 패턴
    public Chapter(Novel novel, String author, String title, String content, Long episode) {
        this.novel = novel;
        this.author = author;
        this.title = title;
        this.content = content;
        this.hits = 0L; // 조회수 0 초기화
        this.episode = episode + 1;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void hits() {
        this.hits++;
    }

}
