package org.seo.board.controller;

import org.seo.board.domain.User;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.ChapterComment;
import org.seo.board.domain.Novel;
import org.seo.board.dto.AddChapterCommentRequest;
import org.seo.board.dto.AddChapterRequest;
import org.seo.board.dto.AddCommentRequest;
import org.seo.board.dto.AddCommentResponse;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.RecommendChapCommentResponse;
import org.seo.board.dto.UpdateChapterCommentRequest;
import org.seo.board.dto.UpdateChapterCommentResponse;
import org.seo.board.dto.UpdateChapterRequest;
import org.seo.board.dto.UpdateChapterResponse;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.service.NovelService;
import org.seo.board.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NovelApiController {

    private final NovelService novelService;
    private final UserService userService;

    // 작품 생성
    @PostMapping("/api/novel")
    public ResponseEntity<Novel> addNovel(@AuthenticationPrincipal Object principal,
            @RequestBody AddNovelRequest request) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        Novel novel;

        novel = novelService.save(request, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novel);
    }

    // 작품 수정
    @PutMapping("/api/novel/{id}")
    public ResponseEntity<Novel> updateNovel(@AuthenticationPrincipal Object principal,
            @PathVariable("id") Long id,
            @RequestBody UpdateNovelRequest request) {

        Novel novel = novelService.update(id, request);

        return ResponseEntity.ok()
                .body(novel);
    }

    // 작품 삭제
    @DeleteMapping("/api/novel/{id}")
    public ResponseEntity<Void> deleteNovel(@PathVariable("id") Long id) {
        novelService.delete(id);

        System.out.println("id: " + id);

        return ResponseEntity.ok()
                .build();
    }

    // 새 회차 작성
    @PostMapping("/api/chapter")
    public ResponseEntity<Chapter> createChapter(@RequestBody AddChapterRequest request,
            @AuthenticationPrincipal Object principal) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        Chapter chapter = novelService.saveChapter(request, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chapter);
    }

    // 해당 회차 수정
    @PutMapping("/api/chapter/{id}")
    public ResponseEntity<UpdateChapterResponse> updateChapter(@RequestBody UpdateChapterRequest request,
            @AuthenticationPrincipal Object principal, @PathVariable("id") Long id) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        Chapter chapter = novelService.updateChapter(request, id, user.getUsername());

        return ResponseEntity.ok()
                .body(new UpdateChapterResponse(chapter));
    }

    // 최신 회차 삭제
    @DeleteMapping("/api/chapter/{novelId}")
    public ResponseEntity<Void> deleteChapter(@PathVariable("novelId") Long novelId) {

        novelService.deleteChapter(novelId);

        return ResponseEntity.ok().build();
    }

    // 댓글 작성
    @PostMapping("/api/chapter-comments")
    public ResponseEntity<ChapterComment> chapterComment(@RequestBody AddChapterCommentRequest request,
            @AuthenticationPrincipal Object principal) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        ChapterComment chapterComment = novelService.saveComment(request, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(chapterComment);
    }

    // 댓글 수정
    @PutMapping("/api/chapter-comments/{commentId}")
    public ResponseEntity<UpdateChapterCommentResponse> updateChapterComment(@PathVariable("commentId") Long id,
            @RequestBody UpdateChapterCommentRequest request, @AuthenticationPrincipal Object principal) {

        ChapterComment chapterComment = novelService.updateComment(id, request);
        
        return ResponseEntity.ok().body(new UpdateChapterCommentResponse(chapterComment));
    }

    // 댓글 삭제
    @DeleteMapping("/api/chapter-comments/{commentId}")
    public ResponseEntity<Void> deleteChapterComment(@PathVariable("commentId") Long id) {
        novelService.deleteComment(id);

        return ResponseEntity.ok().build();
    }

    // 댓글 추천
    @PutMapping("/api/chapter-comments-recommend/{commentId}")
    public ResponseEntity<RecommendChapCommentResponse> recommendChapterComment(@PathVariable("commentId") Long id) {
        ChapterComment chapterComment = novelService.recommendComment(id);

        return ResponseEntity.ok().body(new RecommendChapCommentResponse(chapterComment));
    }

    // 표지 업로드
    // @PostMapping("/api/coverImage")
    // public ResponseEntity<String> coverImageUpload(@RequestParam MultipartFile
    // file, @RequestParam("novel-id") Long novelId) throws Exception {

    // if (!file.isEmpty()) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 파일입니다.");
    // }

    // if (!file.getContentType().startsWith("image")) {
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미지 파일만 업로드
    // 가능합니다.");
    // }

    // novelService.coverImage(file, novelId);

    // return ResponseEntity.ok().build();
    // }

}
