package org.seo.board.service;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.User;
import org.seo.board.domain.UserRole;
import org.seo.board.dto.AddUserRequest;
import org.seo.board.dto.UpdateUserRequest;
import org.seo.board.repository.BoardRepository;
import org.seo.board.repository.ChapterCommentRepository;
import org.seo.board.repository.ChapterRepository;
import org.seo.board.repository.CommentRepository;
import org.seo.board.repository.NovelRepository;
import org.seo.board.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterCommentRepository chapterCommentRepository;

    public Long save(AddUserRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .username(request.getUsername())
                .role(UserRole.USER)
                .build()).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    // user 정보 변경
    @Transactional
    public User update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));

        String oldName = user.getUsername();

        user.update(request.getUsername());
        userRepository.save(user);

        boardRepository.updateUsername(oldName, request.getUsername());
        commentRepository.updateUsername(oldName, request.getUsername());
        novelRepository.updateUsername(oldName, request.getUsername());
        chapterRepository.updateUsername(oldName, request.getUsername());
        chapterCommentRepository.updateUsername(oldName, request.getUsername());

        return user;
    }

    // username 중복 확인
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // 회원 탈퇴
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));

        boardRepository.deleteByAuthor(user.getUsername());
        commentRepository.deleteByAuthor(user.getUsername());
        userRepository.delete(user);
    }
}