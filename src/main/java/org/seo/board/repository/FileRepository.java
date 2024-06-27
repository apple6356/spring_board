package org.seo.board.repository;

import org.seo.board.domain.BoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<BoardFile, Long> {
}
