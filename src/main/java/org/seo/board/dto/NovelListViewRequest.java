package org.seo.board.dto;

import org.seo.board.domain.Novel;

import lombok.Getter;

@Getter
public class NovelListViewRequest {

    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final String coverImagePath;

    public NovelListViewRequest(Novel novel) {
        this.id = novel.getId();
        this.title = novel.getTitle();
        this.content = novel.getContent();
        this.author = novel.getAuthor();
        this.coverImagePath = novel.getCoverImagePath();
    }
    
}
