package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChapterRequest {
    
    private Long novelId;
    private String title;
    private String content;
    private String authorComment;

}
