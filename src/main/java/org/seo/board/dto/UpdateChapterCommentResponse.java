package org.seo.board.dto;

import org.seo.board.domain.ChapterComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChapterCommentResponse {
    
    private String content;

    public UpdateChapterCommentResponse(ChapterComment chapterComment) {
        this.content = chapterComment.getContent();
    }
}
