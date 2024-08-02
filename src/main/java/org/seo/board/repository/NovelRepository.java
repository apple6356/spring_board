package org.seo.board.repository;

import java.util.List;

import org.seo.board.domain.Novel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository<Novel, Long> {

    // 본인 작품 목록
    List<Novel> findByAuthor(String username);

    // 상위 10개 반환
    List<Novel> findTop10ByOrderByIdDesc();

    // 표지 저장
    // @Modifying
    // @Query(value = "update Novel n set n.coverImagePath=:fileDir where n.id=:id")
    // void updateCoverImage(String fileDir, Long id);
    
}
