package org.seo.board.repository;

import org.seo.board.domain.ChapterComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterCommentRepository extends JpaRepository<ChapterComment, Long> {
    
}
