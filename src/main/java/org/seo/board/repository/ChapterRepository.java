package org.seo.board.repository;

import java.util.List;
import java.util.Optional;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.Novel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // NovelId로 조회하며 Episode를 기준으로 내림차순, Top -> 검색된 값중 첫번째 값 리턴, Episode 필드를 리턴
    Optional<Chapter> findTopEpisodeByNovelIdOrderByEpisodeDesc(Long novelId);

    // 최신순 정렬
    List<Chapter> findByNovelIdOrderByEpisodeDesc(Long novelId);

    // 오래된 순으로 정렬
    List<Chapter> findByNovelIdOrderByEpisodeAsc(Long novelId);

    // 현재 회차 정보를 기반으로 novel이 일치하고 episode가 현재 episode보다 큰 것 중 episode가 가장 작은 하나를 반환
    // 현재 회차 정보로 다음 회차 반환
    Optional<Chapter> findFirstByNovelAndEpisodeGreaterThanOrderByEpisode(Novel novel, Long episode);

    @Modifying
    @Query(value = "update Chapter c set c.author=:newName where c.author=:oldName")
    void updateUsername(@Param("oldName") String oldName, @Param("newName") String newName);

}
