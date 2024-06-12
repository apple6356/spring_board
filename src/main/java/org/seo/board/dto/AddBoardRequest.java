package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Board;

// DTO는 데이터를 옮기기 위한 전달자 역할

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBoardRequest {

    private String title;
    private String content;

    public Board toEntity(String author) {
        return Board.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}

