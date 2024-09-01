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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController // @Controller + @ResponseBody 가 합쳐진 형태로 JSON 형태의 객체 데이터를 반환
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성해줌
public class BoardApiController {

    private final BoardService boardService;
    private final UserService userService;

    // 글 작성(저장)
    @PostMapping("/api/boards")
    public ResponseEntity<Board> addBoard(@RequestBody @Validated AddBoardRequest request,
            @AuthenticationPrincipal Object principal) throws Exception {

        // 이미지 파일만 업로드 가능하게
        // if (multipartFiles != null) {
        // for (MultipartFile multipartFile : multipartFiles) {
        // System.out.println("multipartFile.getContentType() = " +
        // multipartFile.getContentType());
        // if (!multipartFile.getContentType().startsWith("image")) {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        // .body("이미지 파일만 가능");
        // }
        // }
        // }

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        Board board = boardService.save(request, user.getUsername());

        // 이미지를 임시 저장소에서 각 게시글 저장소로 이동
        boardService.imageUpload(board.getId(), request.getContent());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(board);
    }

    // 이미지 업로드
    @PostMapping("/api/image-upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws Exception {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 파일입니다.");
        }

        if (!file.getContentType().startsWith("image")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 파일만 업로드 가능합니다.");
        }

        String tempDir = "/files/temp";
        Path tempPath = Paths.get(tempDir);
        if (!Files.exists(tempPath)) {
            Files.createDirectories(tempPath);
        }

        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        Path filePath = tempPath.resolve(storedFileName);
        Files.write(filePath, file.getBytes());

        Map<String, Object> response = new HashMap<>();
        response.put("url", tempDir + "/" + storedFileName);

        System.out.println("response = " + response);

        return ResponseEntity.ok(response);
    }

    // 이미지 삭제
    @DeleteMapping("/api/image-delete")
    public ResponseEntity<?> deleteImage(@RequestParam("filename") String filename) throws IOException {
        Path filePath = Paths.get("/files/temp", filename);
        Files.deleteIfExists(filePath);

        return ResponseEntity.ok().body("임시 저장된 이미지 삭제");
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
            @RequestBody UpdateBoardRequest request) throws IOException {
                
        Board board = boardService.update(id, request);

        // 이미지를 임시 저장소에서 각 게시글 저장소로 이동
        boardService.imageUpload(board.getId(), request.getContent());

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
    public ResponseEntity<List<Comment>> addComment(@RequestBody AddCommentRequest request,
            @AuthenticationPrincipal Object principal) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        boardService.addComment(request, user.getUsername());

        List<Comment> comments = boardService.findCommentsById(request.getBoardId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comments);
    }

    // 댓글 수정
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<List<Comment>> updateComment(@PathVariable("id") Long id,
            @RequestBody UpdateCommentRequest request) {
        Comment comment = boardService.updateComment(id, request);

        List<Comment> comments = boardService.findCommentsById(comment.getBoard().getId());

        return ResponseEntity.ok()
                .body(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<List<Comment>> deleteComment(@PathVariable("id") Long id) {
        Comment comment = boardService.findCommentById(id);
        boardService.deleteComment(id);
        List<Comment> comments = boardService.findCommentsById(comment.getBoard().getId());

        return ResponseEntity.ok()
                .body(comments);
    }

    // 댓글 추천수 +1
    @PutMapping("/api/comment-recommend/{id}")
    public ResponseEntity<List<Comment>> updateCommentRecommend(@PathVariable("id") Long id,
            @RequestBody RecommendCommentRequest request) {
        Comment comment = boardService.updateCommentRecommend(id, request);

        List<Comment> comments = boardService.findCommentsById(comment.getBoard().getId());

        return ResponseEntity.ok()
                .body(comments);
    }
}