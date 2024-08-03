package org.seo.board.dto;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.ChapterComment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddChapterCommentRequest {

    private Long chapterId;
    private String content;

    public ChapterComment toEntity(Chapter chapter, String author) {
        return ChapterComment.builder()
                .author(author)
                .chapter(chapter)
                .content(content)
                .build();
    }
}
