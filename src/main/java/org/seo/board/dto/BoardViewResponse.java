package org.seo.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.domain.BoardFile;
import org.seo.board.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class BoardViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;
    private List<Comment> comments;
    private Long hits;
    private Long recommend;
    private List<BoardFile> files;

    public BoardViewResponse(Board board) {
        this.id = board.getId();
        this.author = board.getAuthor();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.comments = board.getComments();
        this.hits = board.getHits();
        this.recommend = board.getRecommend();
        this.files = board.getFiles();
    }
}
