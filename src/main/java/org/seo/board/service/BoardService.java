package org.seo.board.service;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.dto.BoardDTO;
import org.seo.board.dto.UpdateBoardRequest;
import org.seo.board.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor // final이나 @NotNull인 필드의 생성자 추가
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    // 글 작성(저장)
    public Board save(BoardDTO boardDTO) {
        return boardRepository.save(boardDTO.toEntity());
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
        boardRepository.deleteById(id);
    }

    // 글 수정
    @Transactional // 메서드를 하나의 트랜잭션으로 묶음
    public Board update(Long id, UpdateBoardRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        board.update(request.getTitle(), request.getContent());

        return board;
    }
}
