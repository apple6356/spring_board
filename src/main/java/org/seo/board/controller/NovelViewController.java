package org.seo.board.controller;

import java.util.List;

import org.seo.board.domain.Chapter;
import org.seo.board.domain.Novel;
import org.seo.board.domain.User;
import org.seo.board.dto.BoardViewResponse;
import org.seo.board.dto.ChapterViewResponse;
import org.seo.board.dto.NovelListViewRequest;
import org.seo.board.dto.NovelViewResponse;
import org.seo.board.repository.ChapterRepository;
import org.seo.board.service.NovelService;
import org.seo.board.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NovelViewController {

    private final NovelService novelService;
    private final UserService userService;

    // 소설 관리 페이지
    @GetMapping("/novelManage")
    public String novelManage(@RequestParam(value = "id", required = false) Long id, Model model,
            @AuthenticationPrincipal Object principal) {

        if (id == null) { // id가 없으면 생성
            model.addAttribute("novel", new NovelViewResponse());
        } else { // id가 있으면 수정
            Novel novel = novelService.findById(id);
            model.addAttribute("novel", novel);
        }

        boolean isLogin = false; // 로그인을 안 했을 경우

        // 로그인을 했을 경우
        if (!principal.equals("anonymousUser")) {
            isLogin = true;
        }

        model.addAttribute("isLogin", isLogin);

        return "novelManage";
    }

    // 소설 목록
    @GetMapping("/novelList")
    public String novelList(@PageableDefault(page = 1) Pageable pageable, Model model,
            @AuthenticationPrincipal Object principal) {

        User user;
        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        if (!email.equals("")) {
            user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }
        
        Page<NovelViewResponse> novelList = novelService.novelPaging(pageable);

        int blockLimit = 10;
        // 1 11 21 31
        int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        ;
        // 10 20 30 40
        int endPage = ((startPage + blockLimit - 1) < novelList.getTotalPages()) ? startPage + blockLimit - 1
                : novelList.getTotalPages();

        int prev = startPage - 1;
        int next = endPage + 1;

        model.addAttribute("novelList", novelList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);

        return "novelList";
    }

    // 내가 작성한 소설 목록
    @GetMapping("/myNovels")
    public String myNovels(@RequestParam("username") String username, Model model,
            @AuthenticationPrincipal Object principal) {

        boolean isLogin = false; // 로그인을 안 했을 경우

        // 로그인을 했을 경우
        if (!principal.equals("anonymousUser")) {
            isLogin = true;
        }

        List<Novel> novelList = novelService.myNovels(username);

        model.addAttribute("isLogin", isLogin);
        model.addAttribute("novelList", novelList);

        return "myNovels";
    }

    // 소설 페이지
    @GetMapping("/novel/{id}")
    public String novelPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal Object principal) {

        User user = null;
        boolean isLogin = false; // 로그인을 안 했을 경우

        // 로그인을 했을 경우
        if (!principal.equals("anonymousUser")) {
            isLogin = true;
        }

        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        if (isLogin) {
            user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }

        Novel novel = novelService.findById(id);

        List<ChapterViewResponse> chapterList = novelService.findByIdDesc(novel.getId());

        model.addAttribute("chapterList", chapterList);
        model.addAttribute("novel", novel);
        model.addAttribute("isLogin", isLogin);

        return "novel";
    }

    // 회차 정렬 변경 (최신화부터, 1화부터)
    @GetMapping("/episode-sort")
    public String episodeSort(@RequestParam("novelId") Long novelId,
            @RequestParam("order") String order, Model model, @AuthenticationPrincipal Object principal) {

        User user;
        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        if (!email.equals("")) {
            user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }

        // 정렬 방식 요청에 따라 값 변경
        List<ChapterViewResponse> chapterList = "1화부터".equals(order) ? chapterList = novelService.findByIdAsc(novelId)
                : novelService.findByIdDesc(novelId);

        model.addAttribute("chapterList", chapterList);

        // thymeleaf fragment 사용, novel 페이지에 id가 chapters인 요소에만 반환
        return "novel :: #chapters";
    }

    // 회차 작성 or 수정 페이지
    @GetMapping("/novel-write")
    public String novelUpdateWrite(@RequestParam(value = "novelId", required = false) Long novelId, Model model,
            @RequestParam(value = "id", required = false) Long id, @AuthenticationPrincipal Object principal) {

        boolean isLogin = false; // 로그인을 안 했을 경우
        Chapter chapter;

        // 로그인을 했을 경우
        if (!principal.equals("anonymousUser")) {
            isLogin = true;
        }

        if (novelId != null) {
            // 새 회차 작성 시
            model.addAttribute("novelId", novelId);
        } else if (id != null) {
            // 회차 수정 시
            chapter = novelService.findByIdChapter(id);
            model.addAttribute("novelId", chapter.getNovel().getId());
            model.addAttribute("chapter", chapter);
        }

        model.addAttribute("isLogin", isLogin);

        return "novelWrite";
    }

    // 해당 회차 조회 페이지
    @GetMapping("/novelView")
    public String novelViewPage(@RequestParam(value = "id") Long id, Model model,
            @AuthenticationPrincipal Object principal) {

        User user;
        String email = "";

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        }

        if (!email.equals("")) {
            user = userService.findByEmail(email);
            model.addAttribute("user", user);
        }

        Chapter chapter = novelService.findByIdChapter(id);

        model.addAttribute("chapter", chapter);

        return "novelView";
    }

    // 내 서재
    // @GetMapping("/myLibrary/{username}")
    // public String myLibrary(@PageableDefault(page = 1) Pageable pageable,
    // @PathVariable("username") String username, Model model,
    // @AuthenticationPrincipal Object principal) {

    // User user;
    // String email = "";

    // if (principal instanceof UserDetails) {
    // email = ((UserDetails) principal).getUsername();
    // } else if (principal instanceof OAuth2User) {
    // email = (String) ((OAuth2User) principal).getAttributes().get("email");
    // }

    // if (!email.equals("")) {
    // user = userService.findByEmail(email);
    // model.addAttribute("user", user);
    // }

    // Page<NovelViewResponse> novelList = novelService.myLibrary(username,
    // pageable);

    // int blockLimit = 10;
    // // 1 11 21 31
    // int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() /
    // blockLimit))) - 1) * blockLimit + 1;
    // ;
    // // 10 20 30 40
    // int endPage = ((startPage + blockLimit - 1) < novelList.getTotalPages()) ?
    // startPage + blockLimit - 1
    // : novelList.getTotalPages();

    // int prev = startPage - 1;
    // int next = endPage + 1;

    // model.addAttribute("novelList", novelList);
    // model.addAttribute("startPage", startPage);
    // model.addAttribute("endPage", endPage);
    // model.addAttribute("prev", prev);
    // model.addAttribute("next", next);

    // return "myLibrary";
    // }

}
