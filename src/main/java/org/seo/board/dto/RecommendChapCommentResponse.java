package org.seo.board.dto;

import org.seo.board.domain.ChapterComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendChapCommentResponse {
    private Long recommend;

    public RecommendChapCommentResponse(ChapterComment chapterComment) {
        this.recommend = chapterComment.getRecommend();
    }
}
