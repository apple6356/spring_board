package org.seo.board.repository;

import org.seo.board.domain.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChaptereRepository extends JpaRepository<Chapter, Long> {
    
}
