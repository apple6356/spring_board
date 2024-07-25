package org.seo.board.dto;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.Novel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddChapterRequest {

    private Long novelId;
    private String title;
    private String content;

    public Chapter toEntity(Novel novel, String author, Long episode) {
        return Chapter.builder()
                .novel(novel)
                .title(title)
                .content(content)
                .author(author)
                .episode(episode)
                .build();
    }

}
