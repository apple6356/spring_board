package org.seo.board.repository;

import java.util.List;

import org.seo.board.domain.ChapterComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterCommentRepository extends JpaRepository<ChapterComment, Long> {

    @Modifying
    @Query(value = "update ChapterComment c set c.author=:newName where c.author=:oldName")
    void updateUsername(@Param("oldName") String oldName, @Param("newName") String newName);

    // 댓글 list 반환
    List<ChapterComment> findByChapterId(Long chapterId);

}
