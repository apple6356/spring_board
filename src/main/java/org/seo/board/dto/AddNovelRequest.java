package org.seo.board.dto;

import org.seo.board.domain.Novel;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddNovelRequest {

    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    @Size(min = 1, max = 10000)
    private String content;

    private MultipartFile coverImage;

    // 표지 이미지가 있을 경우
    public Novel toEntity(String author, String coverImagePath, String filename) {
        return Novel.builder()
                .title(title)
                .content(content)
                .author(author)
                .coverImagePath(coverImagePath)
                .filename(filename)
                .build();
    }

    // 표지 이미지가 없을 경우
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
