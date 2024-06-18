package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Comment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentResponse {
    private String content;

    public UpdateCommentResponse(Comment comment) {
        this.content = comment.getContent();
    }
}

