package org.seo.board.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import org.seo.board.domain.Board;

@Getter
public class BoardListViewResponse {
    private final Long id;
    private final String title;
    private final String content;

    public BoardListViewResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
