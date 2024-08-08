package org.seo.board.dto;

import org.seo.board.domain.Novel;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NovelViewResponse {

    private Long id; 
    private String title;
    private String content;
    private String author;
    private String coverImagePath;
    private String filename;

    public NovelViewResponse(Novel novel) {
        this.id = novel.getId();
        this.title = novel.getTitle();
        this.content = novel.getContent();
        this.author = novel.getAuthor();
        this.coverImagePath = novel.getCoverImagePath();
        this.filename = novel.getFilename();
    }
}
