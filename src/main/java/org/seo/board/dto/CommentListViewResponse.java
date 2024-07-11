package org.seo.board.dto;

import lombok.Getter;
import org.seo.board.domain.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentListViewResponse {
    private final Long id;
    private final String author;
    private final String content;
    private final Long recommend;
    private final LocalDateTime createdAt;
    private final Long boardId;
    private final String boardTitle;

    public CommentListViewResponse(Comment comment) {
        this.id = comment.getId();
        this.author = comment.getAuthor();
        this.content = comment.getContent();
        this.recommend = comment.getRecommend();
        this.createdAt = comment.getCreatedAt();
        this.boardId = comment.getBoard().getId();
        this.boardTitle = comment.getBoard().getTitle();
    }
}
