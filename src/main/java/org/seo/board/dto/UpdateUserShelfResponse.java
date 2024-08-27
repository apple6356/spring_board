package org.seo.board.dto;

import org.seo.board.domain.UserShelf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserShelfResponse {
    private Integer position;

    public UpdateUserShelfResponse(UserShelf userShelf) {
        this.position = userShelf.getReadPosition();
    }
}
