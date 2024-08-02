package org.seo.board.dto;

import java.time.LocalDateTime;
import org.seo.board.domain.Novel;
import org.seo.board.domain.User;
import org.seo.board.domain.UserShelf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserShelfViewResponse {
    
    private Long id;
    private Long lastReadChapterId;
    private LocalDateTime lastReadDate;
    private Long lastReadEpisode;
    private Long nextChapterId;
    private Novel novel;
    private User user;

    public UserShelfViewResponse(UserShelf userShelf){
        this.id = userShelf.getId();
        this.lastReadChapterId = userShelf.getLastReadChapterId();
        this.lastReadDate = userShelf.getLastReadDate();
        this.lastReadEpisode = userShelf.getLastReadEpisode();
        this.nextChapterId = userShelf.getNextChapterId();
        this.novel = userShelf.getNovel();
        this.user = userShelf.getUser();
    }
    
}
