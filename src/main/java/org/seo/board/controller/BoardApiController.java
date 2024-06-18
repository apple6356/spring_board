package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.domain.Comment;
import org.seo.board.dto.*;
import org.seo.board.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController // @Controller + @ResponseBody 가 합쳐진 형태로 JSON 형태의 객체 데이터를 반환
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해줌
public class BoardApiController {

    private final BoardService boardService;

    // 글 작성(저장)
    @PostMapping("/api/boards")
    public ResponseEntity<Board> addBoard(@RequestBody @Validated AddBoardRequest request, Principal principal) {
        Board board = boardService.save(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(board);
    }

    // 글 전체 조회
    @GetMapping("/api/boards")
    public ResponseEntity<List<BoardResponse>> findALL() {
        List<BoardResponse> boardResponseList = boardService.findAll()
                .stream()
                .map(BoardResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(boardResponseList);
    }

    // 글 조회
    @GetMapping("/api/boards/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable("id") Long id) {
        Board board = boardService.findById(id);

        return ResponseEntity.ok()
                .body(new BoardResponse(board));
    }

    // 글 삭제
    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long id) {
        boardService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    // 글 수정
    @PutMapping("/api/boards/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable("id") Long id, @RequestBody UpdateBoardRequest request) {
        Board board = boardService.update(id, request);

        return ResponseEntity.ok()
                .body(board);
    }

    // 댓글 생성
    @PostMapping("/api/comments")
    public ResponseEntity<AddCommentResponse> addComment(@RequestBody AddCommentRequest request, Principal principal) {
        Comment savedComment = boardService.addComment(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddCommentResponse(savedComment));
    }

    // 댓글 수정
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<UpdateCommentResponse> updateComment(@PathVariable("id") Long id, @RequestBody UpdateCommentRequest request) {
        Comment comment = boardService.updateComment(id, request);

        System.out.println("comment.getId() = " + comment.getId());
        System.out.println("comment.getContent() = " + comment.getContent());
        System.out.println("===========================zxcv==============================");

        return ResponseEntity.ok()
                .body(new UpdateCommentResponse(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        System.out.println("controller in");
        boardService.deleteComment(id);
        System.out.println("after delete");

        return ResponseEntity.ok()
                .build();
    }
}
