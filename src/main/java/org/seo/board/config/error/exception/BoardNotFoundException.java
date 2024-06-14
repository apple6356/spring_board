package org.seo.board.config.error.exception;

import org.seo.board.config.error.ErrorCode;

public class BoardNotFoundException extends NotFoundException {

    public BoardNotFoundException() {
        super(ErrorCode.BOARD_NOT_FOUND);
    }

}
