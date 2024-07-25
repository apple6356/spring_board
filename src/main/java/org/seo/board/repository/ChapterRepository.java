package org.seo.board.repository;

import java.util.List;
import java.util.Optional;

import org.seo.board.domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // NovelId로 조회하며 Episode를 기준으로 내림차순, Top -> 검색된 값중 첫번째 값 리턴, Episode 필드를 리턴
    Optional<Chapter> findTopEpisodeByNovelIdOrderByEpisodeDesc(Long novelId);

    // 최신순 정렬
    List<Chapter> findByNovelIdOrderByEpisodeDesc(Long novelId);
    
}
