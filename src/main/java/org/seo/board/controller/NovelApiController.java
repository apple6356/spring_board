package org.seo.board.controller;

import org.seo.board.domain.User;
import org.seo.board.domain.UserShelf;

import java.io.IOException;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.ChapterComment;
import org.seo.board.domain.Novel;
import org.seo.board.dto.AddChapterCommentRequest;
import org.seo.board.dto.AddChapterRequest;
import org.seo.board.dto.AddCommentRequest;
import org.seo.board.dto.AddCommentResponse;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.ChapterCommentViewResponse;
import org.seo.board.dto.RecommendChapCommentResponse;
import org.seo.board.dto.UpdateChapterCommentRequest;
import org.seo.board.dto.UpdateChapterCommentResponse;
import org.seo.board.dto.UpdateChapterRequest;
import org.seo.board.dto.UpdateChapterResponse;
import org.seo.board.dto.UpdateNovelRequest;
import org.seo.board.dto.UpdateUserShelfRequest;
import org.seo.board.dto.UpdateUserShelfResponse;
import org.seo.board.service.NovelService;
import org.seo.board.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NovelApiController {

    private final NovelService novelService;
    private final UserService userService;

    // 작품 생성
    @PostMapping("/api/novel")
    public ResponseEntity<Novel> addNovel(@AuthenticationPrincipal Object principal,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws IllegalStateException, IOException {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        AddNovelRequest request = new AddNovelRequest(title, content, file);

        Novel novel = novelService.save(request, user.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(novel);
    }

    // 작품 수정
    @PutMapping("/api/novel/{id}")
    public ResponseEntity<Novel> updateNovel(@AuthenticationPrincipal Object principal,
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file)
            throws IllegalStateException, IOException {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        UpdateNovelRequest request = new UpdateNovelRequest(title, content, file);

        Novel novel = novelService.update(id, request, user.getUsername());

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

    // 소설 추천
    @PostMapping("/api/novel-recommend/{novelId}")
    public ResponseEntity<UserShelf> recommendNovel(@PathVariable("novelId") Long novelId,
            @AuthenticationPrincipal Object principal) {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        UserShelf userShelf = novelService.recommendNovel(novelId, user);

        return ResponseEntity.ok().body(userShelf);
    }

    // 선호작 등록 / 해제
    @PostMapping("/api/favorite/{novelId}")
    public ResponseEntity<UserShelf> favoriteNovel(@PathVariable("novelId") Long novelId,
            @AuthenticationPrincipal Object principal) {
        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        UserShelf userShelf = novelService.favoriteNovel(user, novelId);

        return ResponseEntity.ok().body(userShelf);
    }

    // 댓글 작성
    @PostMapping("/api/chapter-comments")
    public ResponseEntity<List<ChapterComment>> chapterComment(
            @RequestBody AddChapterCommentRequest request,
            @AuthenticationPrincipal Object principal) throws JsonProcessingException {

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        User user = userService.findByEmail(email);

        novelService.saveComment(request, user);

        List<ChapterComment> chapterComments = novelService.findCommentByChapterId(request.getChapterId());

        return ResponseEntity.status(HttpStatus.CREATED).body(chapterComments);
    }

    // 댓글 수정
    @PutMapping("/api/chapter-comments/{commentId}")
    public ResponseEntity<List<ChapterComment>> updateChapterComment(@PathVariable("commentId") Long id,
            @RequestBody UpdateChapterCommentRequest request, @AuthenticationPrincipal Object principal) {

        ChapterComment chapterComment = novelService.updateComment(id, request);

        List<ChapterComment> chapterComments = novelService.findCommentByChapterId(chapterComment.getChapter().getId());

        return ResponseEntity.ok().body(chapterComments);
    }

    // 댓글 삭제
    @DeleteMapping("/api/chapter-comments/{commentId}")
    public ResponseEntity<List<ChapterComment>> deleteChapterComment(@PathVariable("commentId") Long id) {
        ChapterComment chapterComment = novelService.findChapterCommentBycommentId(id);
        novelService.deleteComment(id);
        List<ChapterComment> chapterComments = novelService.findCommentByChapterId(chapterComment.getChapter().getId());

        return ResponseEntity.ok().body(chapterComments);
    }

    // 댓글 추천
    @PutMapping("/api/chapter-comments-recommend/{commentId}")
    public ResponseEntity<List<ChapterComment>> recommendChapterComment(@PathVariable("commentId") Long id) {
        ChapterComment chapterComment = novelService.recommendComment(id);

        List<ChapterComment> chapterComments = novelService.findCommentByChapterId(chapterComment.getChapter().getId());

        return ResponseEntity.ok().body(chapterComments);
    }

    // 마지막으로 읽던 위치 저장
    @PutMapping("/api/saveReadPosition")
    public ResponseEntity<UpdateUserShelfResponse> saveReadPosition(@RequestBody UpdateUserShelfRequest request,
            @AuthenticationPrincipal Object principal) {
        UserShelf userShelf = novelService.updateReadPosition(request);

        return ResponseEntity.ok().body(new UpdateUserShelfResponse(userShelf));
    }

}
