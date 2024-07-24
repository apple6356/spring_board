package org.seo.board.dto;

import org.seo.board.domain.Novel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddNovelRequest {

    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    @Size(min = 1, max = 200)
    private String content;

    public Novel toEntity(String author) {
        return Novel.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
