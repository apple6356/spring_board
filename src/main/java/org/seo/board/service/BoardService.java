package org.seo.board.service;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.dto.AddBoardRequest;
import org.seo.board.dto.UpdateBoardRequest;
import org.seo.board.repository.BoardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor // final이나 @NotNull인 필드의 생성자 추가
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    // 글 작성(저장)
    public Board save(AddBoardRequest boardDTO, String userName) {
        return boardRepository.save(boardDTO.toEntity(userName));
    }

    // 글 전체 조회
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    // 글 조회
    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    // 글 삭제
    public void delete(Long id) {
//        boardRepository.deleteById(id);

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeBoardAuthor(board);
        boardRepository.delete(board);
    }

    // 글 수정
    @Transactional // 메서드를 하나의 트랜잭션으로 묶음
    public Board update(Long id, UpdateBoardRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        authorizeBoardAuthor(board); // 추가
        board.update(request.getTitle(), request.getContent());

        return board;
    }

    // 게시글을 작성한 유저인지 확인
    private static void authorizeBoardAuthor(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!board.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
