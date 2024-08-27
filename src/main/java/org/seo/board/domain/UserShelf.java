package org.seo.board.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.protobuf.BoolValue;

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
import lombok.Setter;

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

    // 첫 회차
    @Column(name = "first_chapter_id")
    private Long firstChapterId;

    // 다음 회차
    @Column(name = "next_chapter_id")
    private Long nextChapterId;

    // 마지막으로 읽은 회차 episode
    @Column(name = "last_read_episode")
    private Long lastReadEpisode;

    // 읽은 시간
    @Column(name = "last_read_date")
    private LocalDateTime lastReadDate;

    // 마지막으로 읽던 위치
    @Column(name = "read_position")
    private Integer readPosition;

    // readPosition의 위치 확인을 위해
    @Column(name = "max-scroll")
    private Integer maxScroll;

    // 해당 소설을 추천했는지
    @Column(name = "recommend")
    private Boolean recommend = false;

    // 해당 소설이 선호작품인지
    @Column(name = "favorite")
    private Boolean favorite;

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
        this.recommend = false;
        this.favorite = false;
    }

    public UserShelf(User user, Novel novel) {
        this.user = user;
        this.novel = novel;
        this.recommend = false;
        this.favorite = false;
    }

    public void update(Long lastReadChapterId, LocalDateTime lastReadDate, Long lastReadEpisode) {
        this.lastReadChapterId = lastReadChapterId;
        this.lastReadDate = lastReadDate;
        this.lastReadEpisode = lastReadEpisode;
    }

    public void nextChapterId(Long nextChapterId) {
        this.nextChapterId = nextChapterId;
    }

    public void updateReadPosition(Integer readPosition, Integer maxScroll) {
        this.readPosition = readPosition;
        this.maxScroll = maxScroll;
    }

    public void recommendNovel() {
        this.recommend = true;
    }

    public void recommendCancle() {
        this.recommend = false;
    }

    public void favorite() {
        if (this.favorite) {
            this.favorite = false;
        } else {
            this.favorite = true;
        }
    }

    public void setFirstChapterId(Long firstChapterId) {
        this.firstChapterId = firstChapterId;
    }

}
