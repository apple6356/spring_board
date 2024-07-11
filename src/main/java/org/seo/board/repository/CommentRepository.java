package org.seo.board.repository;

import org.seo.board.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query(value = "update Comment c set c.author=:newName where c.author=:oldName")
    void updateUsername(@Param("oldName") String oldName, @Param("newName") String newName);

    // 작성한 댓글 목록
    Page<Comment> findByAuthorLike(String username, Pageable pageable);

    // 회원 탈퇴 시 작성한 댓글 삭제
    void deleteByAuthor(String username);
}
