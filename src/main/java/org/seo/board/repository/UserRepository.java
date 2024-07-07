package org.seo.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.seo.board.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
