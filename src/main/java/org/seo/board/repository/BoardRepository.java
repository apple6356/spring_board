package org.seo.board.repository;

import org.seo.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 조회수 +1
    @Modifying // @Query 어노테이션을 통해 작성된 변경이 일어나는 쿼리(INSERT, DELETE, UPDATE )를 실행할 때 사용
    @Query(value = "update Board b set b.hits=b.hits+1 where b.id=:id")
    void updateHits(@Param("id") Long id);

    // 추천 30 이상인 글만 조회 | RecommendGreaterThanEqual -> recommend >= int recommend
    Page<Board> findByRecommendGreaterThanEqualOrderByIdDesc(int recommend, Pageable pageable);

    // 상위 10개의 글만 조회
    List<Board> findTop10ByOrderByIdDesc();

    // 상위 10개의 추천수 30개 이상 글 조회
    List<Board> findTop10ByRecommendGreaterThanEqualOrderByIdDesc(int recommend);

    // 검색
    Page<Board> findByTitleContains(String keyword, Pageable pageable);

    // 본인이 작성한 글
    Page<Board> findByAuthorLike(String username, Pageable pageable);

    // username 변경 시 기존 작성했던 글의 author 변경
    @Modifying
    @Query(value = "update Board b set b.author=:newName where b.author=:oldName")
    void updateUsername(@Param("oldName") String oldName, @Param("newName") String newName);

    // 회원 탈퇴 시 작성한 글 삭제
    void deleteByAuthor(String username);

    // 이미지를 옮길 때 사용
    @Modifying
    @Query(value = "update Board b set b.content=:content where b.id=:id")
    void updateContent(@Param("id") Long id, @Param("content") String content);

}
