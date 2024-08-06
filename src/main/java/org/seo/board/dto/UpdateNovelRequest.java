package org.seo.board.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNovelRequest {

    @Size(min = 1, max = 50)
    @NotNull
    private String title;
    
    @Size(min = 1, max = 200)
    @NotNull
    private String content;
    
    private MultipartFile coverImage;
}
