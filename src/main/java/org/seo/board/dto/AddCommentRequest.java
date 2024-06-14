package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.domain.Comment;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentRequest {

    private Long boardId;
    private String content;

    public Comment toEntity(String author, Board board) {
        return Comment.builder()
                .board(board)
                .content(content)
                .author(author)
                .build();
    }

}
