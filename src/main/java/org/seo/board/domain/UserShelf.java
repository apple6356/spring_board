package org.seo.board.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserShelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    // 마지막으로 읽은 회차id
    @Column(name = "last_read_chapter_id")
    private Long lastReadChapterId;

    @Column(name = "next_chapter_id")
    private Long nextChapterId;

    // 마지막으로 읽은 회차 episode
    @Column(name = "last_read_episode")
    private Long lastReadEpisode;

    // 읽은 시간
    @Column(name = "last_read_date")
    private LocalDateTime lastReadDate;

    // 한번이라도 읽은 소설
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // 역직렬화 시 사용되며, 주로 자식 엔티티에 적용, 무한루프 방지
    private Novel novel;

    // 유저 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;

    // @Builder
    // public UserShelf(Long lastReadChapterId, Novel novel, User user, LocalDateTime lastReadDate, Long lastReadEpisode) {
    //     this.lastReadChapterId = lastReadChapterId;
    //     this.novel = novel;
    //     this.user = user;
    //     this.lastReadDate = lastReadDate;
    //     this.lastReadEpisode = lastReadEpisode;
    //     this.nextChapterId = lastReadChapterId + 1;
    // }

    public UserShelf(Long lastReadChapterId, Novel novel, User user, LocalDateTime lastReadDate, Long lastReadEpisode) {
        this.lastReadChapterId = lastReadChapterId;
        this.novel = novel;
        this.user = user;
        this.lastReadDate = lastReadDate;
        this.lastReadEpisode = lastReadEpisode;
    }

    public void update(Long lastReadChapterId, LocalDateTime lastReadDate, Long lastReadEpisode) {
        this.lastReadChapterId = lastReadChapterId;
        this.lastReadDate = lastReadDate;
        this.lastReadEpisode = lastReadEpisode;
    }

    public void nextChapterId(Long nextChapterId) {
        this.nextChapterId = nextChapterId;
    }

}
