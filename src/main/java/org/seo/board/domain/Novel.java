package org.seo.board.domain;


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

    // 표지
    @Column(name = "coverImagePath")
    private String coverImagePath;

    // 추천수
    @Column(name = "recommend")
    private Long recommend;

    @OneToMany(mappedBy = "novel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference // 직렬화 시 사용되며, 주로 부모 엔티티에 적용, 무한루프 방지
    private List<Chapter> chapters;

    @Builder
    public Novel(String author, String content, String title, String coverImagePath) {
        this.author = author;
        this.content = content;
        this.title = title;
        if (coverImagePath != null) {
            this.coverImagePath = coverImagePath;
        }
    }

    // 제목 및 내용 업데이트
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
