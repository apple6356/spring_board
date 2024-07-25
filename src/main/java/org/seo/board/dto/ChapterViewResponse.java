package org.seo.board.dto;

import java.time.LocalDateTime;

import org.seo.board.domain.Chapter;

import lombok.Getter;

@Getter
public class ChapterViewResponse {
    
    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final Long episode;
    private final Long hits;
    private final LocalDateTime createdAt;

    public ChapterViewResponse(Chapter chapter) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.content = chapter.getContent();
        this.author = chapter.getAuthor();
        this.episode = chapter.getEpisode();
        this.hits = chapter.getHits();
        this.createdAt = chapter.getCreatedAt();
    }

}
