package org.seo.board.repository;

import java.util.List;
import java.util.Optional;

import org.seo.board.domain.Novel;
import org.seo.board.dto.NovelViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelRepository extends JpaRepository<Novel, Long> {

    // 본인 작품 목록
    List<Novel> findByAuthor(String username);

    // 상위 10개 반환
    List<Novel> findTop10ByOrderByIdDesc();
    
}
