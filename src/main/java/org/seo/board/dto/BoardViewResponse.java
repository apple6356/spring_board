package org.seo.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Board;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class BoardViewResponse {

    private Long id;
    private String author;
    private String title;
    private String content;
    private LocalDateTime createTime;

    public BoardViewResponse(Board board) {
        this.id = board.getId();
        this.author = board.getAuthor();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createTime = board.getCreateTime();
    }
}
