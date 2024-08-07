package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.BoardFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBoardFileResponse {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String filePath;

    public AddBoardFileResponse(BoardFile boardFile) {
        this.id = boardFile.getId();
        this.originalFileName = boardFile.getOriginalFileName();
        this.storedFileName = boardFile.getStoredFileName();
        this.filePath = boardFile.getFilePath();
    }
}
