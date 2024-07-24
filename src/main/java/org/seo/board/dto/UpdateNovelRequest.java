package org.seo.board.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNovelRequest {

    @Size(min = 1, max = 50)
    private String title;
    
    @Size(min = 1, max = 200)
    private String content;
    
}
