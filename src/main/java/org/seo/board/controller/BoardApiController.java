package org.seo.board.controller;

import lombok.RequiredArgsConstructor;
import org.seo.board.domain.Board;
import org.seo.board.domain.Comment;
import org.seo.board.domain.User;
import org.seo.board.dto.*;
import org.seo.board.service.BoardService;
import org.seo.board.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController // @Controller + @ResponseBody 가 합쳐진 형태로 JSON 형태의 객체 데이터를 반환
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해줌
public class BoardApiController {

    private final BoardService boardService;
    private final UserService userService;

    // 글 작성(저장)
    @PostMapping("/api/boards")
    public ResponseEntity<Board> addBoard(@RequestPart(value = "board") @Validated AddBoardRequest request,
                                          @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles,
                                          @AuthenticationPrincipal Object principal) throws Exception {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        Board board = boardService.save(request, user.getUsername(), multipartFiles);

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
    public ResponseEntity<Board> updateBoard(@PathVariable("id") Long id,
                                             @RequestBody UpdateBoardRequest request) {
        Board board = boardService.update(id, request);

        return ResponseEntity.ok()
                .body(board);
    }

    // 추천수 +1
    @PutMapping("/api/recommend/{id}")
    public ResponseEntity<Board> updateRecommend(@PathVariable("id") Long id) {
        Board board = boardService.updateRecommend(id);

        return ResponseEntity.ok()
                .body(board);
    }

    // =================== comments ========================

    // 댓글 생성
    @PostMapping("/api/comments")
    public ResponseEntity<AddCommentResponse> addComment(@RequestBody AddCommentRequest request, @AuthenticationPrincipal Object principal) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        Comment savedComment = boardService.addComment(request, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddCommentResponse(savedComment));
    }

    // 댓글 수정
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<UpdateCommentResponse> updateComment(@PathVariable("id") Long id, @RequestBody UpdateCommentRequest request) {
        Comment comment = boardService.updateComment(id, request);

        return ResponseEntity.ok()
                .body(new UpdateCommentResponse(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        boardService.deleteComment(id);

        return ResponseEntity.ok()
                .build();
    }

    // 댓글 추천수 +1
    @PutMapping("/api/comment-recommend/{id}")
    public ResponseEntity<RecommendCommentResponse> updateCommentRecommend(@PathVariable("id") Long id, @RequestBody RecommendCommentRequest request) {
        Comment comment = boardService.updateCommentRecommend(id, request);

        return ResponseEntity.ok()
                .body(new RecommendCommentResponse(comment));
    }
}