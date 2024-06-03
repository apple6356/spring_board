package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.dto.BoardDTO;
import org.seo.board.dto.BoardListViewResponse;
import org.seo.board.dto.BoardResponse;
import org.seo.board.dto.UpdateBoardRequest;
import org.seo.board.service.BoardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody가 합쳐진 형태로 JSON 형태의 객체 데이터를 반환
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해줌
public class BoardController {

    private final BoardService boardService;

    // 글 작성(저장)
    @PostMapping("/write") // wirte 경로로 post 요청 받음
    public ResponseEntity<Board> write(@RequestBody BoardDTO boardDTO) {
        Board board = boardService.save(boardDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(board);
    }

    // 글 전체 조회
    @GetMapping("/board")
    public ResponseEntity<List<BoardResponse>> findALL() {
        List<BoardResponse> boardResponseList = boardService.findAll()
                .stream()
                .map(BoardResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(boardResponseList);
    }

    // 글 조회
    @GetMapping("/board/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable("id") Long id) {
        Board board = boardService.findById(id);

        return ResponseEntity.ok()
                .body(new BoardResponse(board));
    }

    // 글 삭제
    @DeleteMapping("/boards/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long id) {
        boardService.delete(id);

        System.out.println("삭제 실행");

        return ResponseEntity.ok()
                .build();
    }

    // 글 수정
    @PutMapping("/board/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable("id") Long id, @RequestBody UpdateBoardRequest request) {
        Board board = boardService.update(id, request);
        System.out.println("수정");
        return ResponseEntity.ok()
                .body(board);
    }
}
