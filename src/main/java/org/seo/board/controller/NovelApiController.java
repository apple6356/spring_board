package org.seo.board.controller;

import org.seo.board.domain.User;
import org.seo.board.domain.Novel;
import org.seo.board.dto.AddNovelRequest;
import org.seo.board.dto.UpdateNovelRequest;
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

        return ResponseEntity.ok()
        .build();
    }

}
