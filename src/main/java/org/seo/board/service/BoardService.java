package org.seo.board.service;

import lombok.RequiredArgsConstructor;
import org.seo.board.config.error.exception.BoardNotFoundException;
import org.seo.board.domain.Board;
import org.seo.board.domain.Comment;
import org.seo.board.dto.*;
import org.seo.board.repository.BoardRepository;
import org.seo.board.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor // final이나 @NotNull인 필드의 생성자 추가
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    // 글 작성(저장)
    public Board save(AddBoardRequest boardDTO, String userName) {
        return boardRepository.save(boardDTO.toEntity(userName));
    }

    // 글 전체 조회
    public List<Board> findAll() {
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // 글 조회
    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(BoardNotFoundException::new);
//                .orElseThrow(IllegalArgumentException("not found : " + id));
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

    // 댓글 추가
    public Comment addComment(AddCommentRequest request, String username) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("not found : " + request.getBoardId()));

        return commentRepository.save(request.toEntity(username, board));
    }

    // 댓글 수정
    public Comment updateComment(Long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        comment.update(request.getContent());
        commentRepository.save(comment);

        return comment;
    }

    // 댓글 삭제
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        commentRepository.delete(comment);
    }

    // 페이징
    public Page<BoardListViewResponse> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        int pageLimit = 10; // 한페이지에 보여줄 글 갯수

        // JpaRepository 의 findAll() 사용시 pageable 인터페이스로 파라미터를 넘기면 페이징 사용가능
        Page<Board> boardPage = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        Page<BoardListViewResponse> boardList = boardPage.map(BoardListViewResponse::new);

        return boardList;
    }
}
