package org.seo.board.repository;

import java.util.Optional;

import org.seo.board.domain.Novel;
import org.seo.board.domain.User;
import org.seo.board.domain.UserShelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserShelfRepository extends JpaRepository<UserShelf, Long> {

    Optional<UserShelf> findByUserAndNovel(User user, Novel novel);

    Page<UserShelf> findByUser(User user, Pageable pageable);
    
}
