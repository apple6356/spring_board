package org.seo.board.dto;

import lombok.Getter;
import org.seo.board.domain.Board;

import java.time.LocalDateTime;

@Getter
public class BoardListViewResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String author;
    private final LocalDateTime createdAt;
    private final Long hits;
    private final Long recommend;

    public BoardListViewResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.author = board.getAuthor();
        this.createdAt = board.getCreatedAt();
        this.hits = board.getHits();
        this.recommend = board.getRecommend();
    }
}
