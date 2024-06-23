package org.seo.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.dto.BoardListViewResponse;
import org.seo.board.dto.BoardViewResponse;
import org.seo.board.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
//    @GetMapping("/boards")
//    public String getBoards(HttpServletRequest request, Model model) {
//        List<BoardListViewResponse> boardList = boardService.findAll()
//                .stream()
//                .map(BoardListViewResponse::new)
//                .toList();
//
//        model.addAttribute("boardList", boardList);
//
//        return "boards";
//    }

    // 메인 화면 글 리스트
    @GetMapping("/main")
    public String getBoards(HttpServletRequest request, Model model) {
        List<BoardListViewResponse> boardList = boardService.findTop()
                .stream()
                .map(BoardListViewResponse::new)
                .toList();

        List<BoardListViewResponse> popularBoardList = boardService.findTopPopular()
                .stream()
                .map(BoardListViewResponse::new)
                .toList();

        model.addAttribute("boardList", boardList);
        model.addAttribute("popularBoardList", popularBoardList);

        return "main";
    }

    // 글 조회 뷰
    @GetMapping("/boards/{id}")
    public String getBoard(@PathVariable("id") Long id, Model model) {
        Board board = boardService.findById(id);
        boardService.updateHits(id); // 조회수 +1

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

    // 페이징
    @GetMapping("/boards")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<BoardListViewResponse> boardList = boardService.paging(pageable);

        int blockLimit = 10;
        // 1 11 21 31
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;;
        // 10 20 30 40
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        int prev = startPage - 1;
        int next = endPage + 1;

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);

        return "boards";
    }

    // 추천 30 이상 인기 게시판
    @GetMapping("/popularBoards")
    public String popularBoards(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<BoardListViewResponse> boardList = boardService.popularPaging(pageable);

        int blockLimit = 10;
        // 1 11 21 31
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;;
        // 10 20 30 40
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        int prev = startPage - 1;
        int next = endPage + 1;

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);

        return "popularBoards";
    }

}
