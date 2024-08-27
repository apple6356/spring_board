package org.seo.board.repository;

import java.util.List;
import java.util.Optional;

import org.seo.board.domain.Novel;
import org.seo.board.domain.User;
import org.seo.board.domain.UserShelf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserShelfRepository extends JpaRepository<UserShelf, Long> {

    Optional<UserShelf> findByUserAndNovel(User user, Novel novel);

    List<UserShelf> findByUser(User user);

    Optional<UserShelf> findByUserIdAndNovelId(Long userId, Long novelId);

    Optional<UserShelf> findByLastReadChapterId(Long lastReadChapterId);

    Optional<UserShelf> findByNovelIdAndUserId(Long novelId, Long id);

    Page<UserShelf> findByUserAndFavoriteTrue(User user, Pageable pageable);

    Page<UserShelf> findByUserAndLastReadChapterIdIsNotNull(User user, Pageable pageable);
    
}
