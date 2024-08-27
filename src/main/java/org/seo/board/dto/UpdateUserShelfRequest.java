package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserShelfRequest {
    private Long chapterId;
    private Integer readPosition;
    private Integer maxScroll;
}
