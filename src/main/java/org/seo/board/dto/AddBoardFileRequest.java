package org.seo.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.domain.BoardFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBoardFileRequest {

    private String originalFileName;
    private String storedFileName;

    public BoardFile toEntity(Board board, String originalFileName, String storedFileName) {
        return BoardFile.builder()
                .board(board)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .build();
    }
}
