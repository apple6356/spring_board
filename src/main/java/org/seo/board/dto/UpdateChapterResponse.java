package org.seo.board.dto;

import org.seo.board.domain.Chapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChapterResponse {
    
    private String title;
    private String content;

    public UpdateChapterResponse(Chapter chapter) {
        this.title = chapter.getTitle();
        this.content = chapter.getContent();
    }
}
