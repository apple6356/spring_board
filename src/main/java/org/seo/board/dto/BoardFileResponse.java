package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.BoardFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BoardFileResponse {

    private Long id;
    private String originalFileName;
    private String storedFileName;

    public BoardFileResponse(BoardFile boardFile) {
        this.id = boardFile.getId();
        this.originalFileName = boardFile.getOriginalFileName();
        this.storedFileName = boardFile.getStoredFileName();
    }
}
