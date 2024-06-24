package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Comment;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentResponse {
    // 무한 순환 참조의 방지를 위해 사용
    private String content;

    public UpdateCommentResponse(Comment comment) {
        this.content = comment.getContent();
    }
}

