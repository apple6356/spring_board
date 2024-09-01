package org.seo.board.repository;

import java.util.List;

import org.seo.board.domain.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NovelRepository extends JpaRepository<Novel, Long> {

    // 본인 작품 목록
    List<Novel> findByAuthor(String username);

    // 상위 10개 반환
    List<Novel> findTop10ByOrderByIdDesc();

    // 유저 이름 변경
    @Modifying
    @Query(value = "update Novel n set n.author=:newName where n.author=:oldName")
    void updateUsername(@Param("oldName") String oldName, @Param("newName") String newName);

    // 소설 검색 제목 또는 작가명으로
    Page<Novel> findByTitleContainingOrAuthorContaining(String titleKeyword, String authorKeyword, Pageable pageable);

}
