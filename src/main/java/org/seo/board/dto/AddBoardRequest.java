package org.seo.board.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Board;

// DTO는 데이터를 옮기기 위한 전달자 역할

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBoardRequest {

    @NotNull
    @Size(min = 1, max = 50)
    private String title;

    @NotNull
    private String content;

    public Board toEntity(String author) {
        return Board.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
