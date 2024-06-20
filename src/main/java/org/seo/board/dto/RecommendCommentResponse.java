package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendCommentResponse {

    private Long recommend;

    public RecommendCommentResponse(Comment comment) {
        this.recommend = comment.getRecommend();
    }
}
