package org.seo.board.domain;


import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Novel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 소설 제목
    @Column(name = "title", nullable = false)
    private String title;

    // 소설에 대한 간단한 소개
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    // 작가
    @Column(name = "author", nullable = false)
    private String author;

    // 표지 경로
    @Column(name = "cover_image_path")
    private String coverImagePath;

    // 표지 파일 이름
    @Column(name = "filename")
    private String filename;

    // 추천수
    @Column(name = "recommend")
    private Long recommend;

    // 마지막으로 업데이트한 시간
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    // 해당 소설의 회차
    @OneToMany(mappedBy = "novel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference // 직렬화 시 사용되며, 주로 부모 엔티티에 적용, 무한루프 방지
    private List<Chapter> chapters;

    // 내 서재
    @OneToMany(mappedBy = "novel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<UserShelf> userShelfs;

    @Builder
    public Novel(String author, String content, String title, String coverImagePath, String filename) {
        this.author = author;
        this.content = content;
        this.title = title;
        this.recommend = 0L;
        if (coverImagePath != null && filename != null) {
            this.coverImagePath = coverImagePath;
            this.filename = filename;
        }
    }

    // 제목 및 내용 업데이트
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 업데이트 시간 최신화
    public void updateTime(LocalDateTime time) {
        this.lastUpdatedAt = time;
    }

    // 표지 이미지 업데이트
    public void updateCoverImage(String coverImagePath, String filename) {
        this.coverImagePath = coverImagePath;
        this.filename = filename;
    }

    // username 변경시
    public void updateUsername(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    // 추천 +1
    public void recommendNovel() {
        this.recommend++;
    }

    // 추천 -1
    public void recommendCancle() {
        this.recommend--;
    }

}
