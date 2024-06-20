package org.seo.board.repository;

import org.seo.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 조회수 +1
    @Modifying // @Query 어노테이션을 통해 작성된 변경이 일어나는 쿼리(INSERT, DELETE, UPDATE )를 실행할 때 사용
    @Query(value = "update Board b set b.hits=b.hits+1 where b.id=:id")
    void updateHits(@Param("id") Long id);
}
