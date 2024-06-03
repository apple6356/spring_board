package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.dto.BoardListViewResponse;
import org.seo.board.dto.BoardViewResponse;
import org.seo.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BoardViewController {

    private final BoardService boardService;

    // 글 전체 리스트 뷰
    @GetMapping("/boards")
    public String getBoards(Model model) {
        List<BoardListViewResponse> boardList = boardService.findAll()
                .stream()
                .map(BoardListViewResponse::new)
                .toList();

        model.addAttribute("boardList", boardList);

        return "boards";
    }

    // 글 조회 뷰
    @GetMapping("/boards/{id}")
    public String getBoard(@PathVariable("id") Long id, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("board", new BoardViewResponse(board));

        return "board";
    }

    // 수정 화면 뷰
    @GetMapping("/write-board")
    // id 키를 가진 쿼리 파라미터 값을 id 변수에 매핑(id 값이 없을 수도 있음)
    public String updateBoard(@RequestParam(value = "id", required = false) Long id, Model model) {

        if (id == null) { // id가 없으면 생성
            model.addAttribute("board", new BoardViewResponse());
        } else { // id가 있으면 수정
            Board board = boardService.findById(id);
            model.addAttribute("board", board);
        }

        return "writeBoard";
    }
}
