package org.seo.board.dto;

import java.time.LocalDateTime;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.ChapterComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterCommentViewResponse {
    private Long id;
    private String author;
    private String content;
    private Long recommend;
    private LocalDateTime createdAt;
    private Chapter chapter;

    public ChapterCommentViewResponse(ChapterComment chapterComment) {
        this.id = chapterComment.getId();
        this.author = chapterComment.getAuthor();
        this.content = chapterComment.getContent();
        this.recommend = chapterComment.getRecommend();
        this.createdAt = chapterComment.getCreatedAt();
        this.chapter = chapterComment.getChapter();
    }
}
